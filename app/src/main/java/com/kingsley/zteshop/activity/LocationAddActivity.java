package com.kingsley.zteshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import com.kingsley.zteshop.R;
import com.kingsley.zteshop.bean.Address;
import com.kingsley.zteshop.bean.JsonBean;
import com.kingsley.zteshop.bean.User;
import com.kingsley.zteshop.utils.GetJsonDataUtil;
import com.kingsley.zteshop.utils.ToastUtils;
import com.kingsley.zteshop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class LocationAddActivity extends BasicActivity {

    private int TAG;

    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    @ViewInject(R.id.et_consignee)
    private EditText mEtConsignee;

    @ViewInject(R.id.et_phone)
    private EditText mEtPhone;

    @ViewInject(R.id.et_add_des)
    private EditText mEtAddDes;

    @ViewInject(R.id.tv_address)
    private TextView mTvAddress;

    private Activity context;

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_add;
    }

    @Override
    public void init() {
        context = LocationAddActivity.this;

        /**
         * 初始化省市数据
         */
        initJsonData();
    }

    @Override
    public void setToolbar() {
        /**
         * 根据传入的TAG，toolbar显示相应布局
         */
        TAG = getIntent().getIntExtra("tag", -1);

        final Address address = (Address) getIntent().getExtras().getSerializable("addressBean");
        if (TAG == Constants.TAG_SAVE) {
            getToolbar().getRightButton().setText("保存");
            getToolbar().setTitle("添加新地址");
            getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        } else if (TAG == Constants.TAG_COMPLETE) {
            getToolbar().getRightButton().setText("完成");
            getToolbar().setTitle("编辑地址");
            getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
            showAddress(address);
        }
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TAG == Constants.TAG_SAVE) {
                    //添加新地址
                    creatAddress();
                } else if (TAG == Constants.TAG_COMPLETE) {
                    final Address address = (Address) getIntent().getExtras().getSerializable("addressBean");
                    //编辑地址
                    updateAddress(address);
                }
            }
        });
    }

    /**
     * 显示添加地址页面
     */
    private void showAddress(Address address) {
        String addrArr[] = address.getAddr().split("-");
        mEtConsignee.setText(address.getConsignee());
        mEtPhone.setText(address.getPhone());
        mTvAddress.setText(addrArr[0] == null ? "" : addrArr[0]);
        mEtAddDes.setText(addrArr[1] == null ? "" : addrArr[1]);
    }

    /**
     * 编辑地址
     */
    public void updateAddress(Address address) {
        check();

        String consignee = mEtConsignee.getText().toString();
        String phone = mEtPhone.getText().toString();
        String addr = mTvAddress.getText().toString() + "-" + mEtAddDes.getText().toString();

        address.setPhone(phone);
        address.setAddr(addr);
        address.setConsignee(consignee);

        address.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    ToastUtils.show(LocationAddActivity.this,"更新成功");
                    setResult(RESULT_OK);
                    finish();
                }else {
                    ToastUtils.show(LocationAddActivity.this,"更新失败"+e.getErrorCode());
                }
            }
        });


    }

    /**
     * 添加新地址
     */
    private void creatAddress() {

        //检查是否为空
        check();

        String consignee = mEtConsignee.getText().toString();
        String phone = mEtPhone.getText().toString();
        String address = mTvAddress.getText().toString() + "-" + mEtAddDes.getText().toString();

        if(checkPhone(phone)){
            // 添加新地址

            User user = BmobUser.getCurrentUser(User.class);

            Address address1 = new Address();

            address1.setUserId(user);
            address1.setPhone(phone);
            address1.setConsignee(consignee);
            address1.setAddr(address);
            address1.setIsDefault(false);

            address1.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {

                    if (e == null){
                        ToastUtils.show(LocationAddActivity.this,"添加成功");
                        setResult(RESULT_OK);
                        finish();
                    }else{
                        ToastUtils.show(LocationAddActivity.this,"添加失败"+e.getErrorCode());
                    }
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == Constants.ADDRESS_ADD){//添加地址
                creatAddress();
            }else if (requestCode == Constants.ADDRESS_EDIT){//编辑地址
                Address address = (Address) getIntent().getExtras().getSerializable("addressBean");
                updateAddress(address);
            }
        }
    }

    /**
     * 检查是否为空
     */
    private void check() {
        String name = mEtConsignee.getText().toString().trim();
        String phone = mEtPhone.getText().toString().trim();
        String address = mTvAddress.getText().toString();
        String address_des = mEtAddDes.getText().toString().trim();
        if (TextUtils.isEmpty(name)){
            ToastUtils.show(this,"请输入收件人姓名");
        }else if (TextUtils.isEmpty(phone)){
            ToastUtils.show(this,"请输入收件人电话");
        }else if (TextUtils.isEmpty(address)){
            ToastUtils.show(this,"请选择地区");
        }else if (TextUtils.isEmpty(address_des)){
            ToastUtils.show(this,"请输入具体地址");
        }
    }

    /**
     * 检验手机号码
     * @param phone
     * @return
     */
    private boolean checkPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return false;
        }
        if (phone.length() != 11) {
            ToastUtils.show(this, "手机号码长度不对");
            return false;
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.show(this, "您输入的手机号码格式不正确");
            return false;
        }

        return true;
    }

    private void ShowPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(LocationAddActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String address = options1Items.get(options1).getPickerViewText()+
                        options2Items.get(options1).get(options2)+
                        options3Items.get(options1).get(options2).get(options3);

                mTvAddress.setText(address);
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .setOutSideCancelable(false)// default is true
                .build();

        pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this,"province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);

            /**
             * 添加地区数据
             */
            options3Items.add(Province_AreaList);
        }

    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @OnClick(R.id.ll_city_picker)
    public void showCityPickerView(View v) {
        //确认省市数据
        ShowPickerView();
    }
}
