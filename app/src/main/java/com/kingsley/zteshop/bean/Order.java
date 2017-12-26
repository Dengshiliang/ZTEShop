package com.kingsley.zteshop.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Kingsley on 2017/12/21.
 */

public class Order extends BmobObject {

    // 订单id
    private String id;
    // 订单地址
    private Address address;
    // 订单用户
    private User user;
    // 支付金额
    private int Amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }
}
