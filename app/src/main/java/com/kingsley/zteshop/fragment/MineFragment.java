package com.kingsley.zteshop.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kingsley.zteshop.MyApplication;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.activity.LoginActivity;
import com.kingsley.zteshop.activity.SettingActivity;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.ImageLoaderUtils;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.PhotoPopupWindows;
import com.kingsley.zteshop.widget.WaveView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends Fragment implements View.OnClickListener {

    @ViewInject(R.id.favorite)
    LinearLayout favorite;

    @ViewInject(R.id.about)
    LinearLayout about;

    @ViewInject(R.id.setting)
    LinearLayout setting;

    @ViewInject(R.id.img_logo)
    ImageView imgLogo;

    @ViewInject(R.id.wave_view)
    private WaveView waveView;

    @ViewInject(R.id.fab)
    FloatingActionButton fab;

    @ViewInject(R.id.tv_name)
    TextView tv_name;

    private Intent intent;
    private PopupWindow pop;
    private User user;

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int REQUEST_BIG_IMAGE_CUTTING = 3;
    private static final String IMAGE_FILE_NAME = "icon.jpg";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ViewUtils.inject(this, view);
        initView();
        return view;
    }

    private void initView() {

        fab.setOnClickListener(this);
        favorite.setOnClickListener(this);
        setting.setOnClickListener(this);
        about.setOnClickListener(this);
        imgLogo.setOnClickListener(this);

        user = BmobUser.getCurrentUser(User.class);
        updatePersonalInfo(user);
    }

    private void updatePersonalInfo(User user) {

        if (user != null) {
            tv_name.setText(user.getUsername());
            if (user.getPhoto() != null) {
                ImageLoaderUtils.displayRound(getContext(), imgLogo, user.getPhoto().getFileUrl());
            } else {
                ImageLoaderUtils.displayRound(getContext(), imgLogo, R.drawable.logo);
            }
        } else {
            tv_name.setText("我的");
            ImageLoaderUtils.displayRound(getContext(), imgLogo, R.drawable.logo);
        }
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = Gravity.CENTER;
        waveView.setOnWaveAnimationListener(new WaveView.OnWaveAnimationListener() {
            @Override
            public void OnWaveAnimation(float y) {
                lp.setMargins(0, 0, 0, (int) y + 2);
                imgLogo.setLayoutParams(lp);
            }
        });
    }

    /**
     * 添加点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.favorite:
                ToastUtils.show(getContext(), "我收藏的商品");
                break;

            case R.id.setting:
                /**
                 * 跳转到设置界面 ，完成个人信息
                 */
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.about:
                ToastUtils.show(getContext(), "关于主页");
                break;

            case R.id.fab:
                ToastUtils.show(getContext(), "登出");
                MyApplication.getInstance().clearUser();
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.img_logo:
                changePhotos();
                break;
        }
    }

    private void changePhotos() {
        pop = new PhotoPopupWindows(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 相册选ToastUtils.show(SettingActivity.this, "相册选择");
                // 取消pop
                pop.dismiss();

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                // 判断系统中是否有处理该Intent的Activity
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                } else {
                    ToastUtils.show(getActivity(), "未找到图片查看器");
                }


            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拍照ToastUtils.show(SettingActivity.this, "相机拍照");
                pop.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_setting, null);
        pop.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 70);
    }

    /**
     * 处理回调结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //小图切割
                case REQUEST_SMALL_IMAGE_CUTTING:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
                case REQUEST_BIG_IMAGE_CUTTING:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
                //相册选取
                case REQUEST_IMAGE_GET:
                    try {
                        startSmallPhotoZoom(data.getData());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    File temp = new File(Environment.getExternalStorageDirectory() + "/" + IMAGE_FILE_NAME);
                    startSmallPhotoZoom(Uri.fromFile(temp));
                    break;
            }
        }

    }

    /**
     * 小图模式，保存图片后 设置到视图中
     *
     * @param data
     */
    private void setPicToView(Intent data) {

        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");//直接从内存中保存的bitmap
            //创建smallIcon文件夹
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String storage = Environment.getExternalStorageDirectory().getPath();
                File dirFile = new File(storage + "/smallIcon");
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                        Log.e("TAG", "文件夹创建失败");
                    } else {
                        Log.e("TAG", "文件夹创建成功");
                    }
                }
                File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
                //保存图片
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String filePath = file.getAbsolutePath();
                upload(filePath);
            }

        }

    }

    /**
     * 小图模式切割图片
     * 此方式直接返回截图后的 bitmap，由于内存的限制，返回的图片会比较小
     *
     * @param data
     */
    private void startSmallPhotoZoom(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 52); // 输出图片大小
        intent.putExtra("outputY", 52);
        intent.putExtra("scale", false);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }

    /**
     * 大图模式切割图片
     * 直接创建一个文件将切割后的图片写入
     */
    public void startBigPhotoZoom(Uri uri) {
        // 创建大图文件夹
        Uri imageUri = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String storage = Environment.getExternalStorageDirectory().getPath();
            File dirFile = new File(storage + "/bigIcon");
            if (!dirFile.exists()) {
                if (!dirFile.mkdirs()) {
                    Log.e("TAG", "文件夹创建失败");
                } else {
                    Log.e("TAG", "文件夹创建成功");
                }
            }
            File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
            imageUri = Uri.fromFile(file);
        }
        // 开始切割
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600); // 输出图片大小
        intent.putExtra("outputY", 600);
        intent.putExtra("scale", false);
        intent.putExtra("return-data", true); // 不直接返回数据
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // 返回一个文件
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_BIG_IMAGE_CUTTING);
    }

    /**
     * 将图片上传
     */
    private void upload(final String imgpath) {

        final BmobFile icon = new BmobFile(new File(imgpath));
        icon.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                user = BmobUser.getCurrentUser(User.class);
                user.setPhoto(icon);
                user.update(user.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        ToastUtils.show(getActivity(), "上传成功");
                        refreshAvater(imgpath);
                    }
                });
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度百分比
            }
        });

    }

    /**
     * 更新图片
     */
    private void refreshAvater(String avatar) {
        if (avatar != null && !avatar.equals("")) {
            ImageLoaderUtils.displayRound(getActivity(), imgLogo, avatar);
        }
    }

}
