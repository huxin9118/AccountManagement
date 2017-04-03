package com.swjtu.huxin.accountmanagement.domain;

import java.io.Serializable;

/**
 * Created by huxin on 2017/2/27.
 */

public class Account implements Serializable {
    int id;
    String color;
    String accountname;
    String accountdetail;
    int type;
    String money;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getAccountdetail() {
        return accountdetail;
    }

    public void setAccountdetail(String accountdetail) {
        this.accountdetail = accountdetail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", color=" + color +
                ", accountname='" + accountname + '\'' +
                ", type=" + type +
                ", money='" + money + '\'' +
                '}';
    }
}
