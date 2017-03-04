package com.swjtu.huxin.accountmanagement.domain;

import java.math.BigDecimal;

/**
 * Created by huxin on 2017/2/27.
 */

public class Account {
    int id;
    int color;
    String accountname;
    int type;
    String money;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
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
