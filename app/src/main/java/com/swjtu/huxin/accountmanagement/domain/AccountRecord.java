package com.swjtu.huxin.accountmanagement.domain;

import java.io.Serializable;

/**
 * Created by huxin on 2017/2/27.
 */

public class AccountRecord implements Serializable{
    int id;
    String icon;
    String recordname;
    String money;
    String remark;
    Account account;
    AccountBook accountbook;
    String member;
    Long recordtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRecordname() {
        return recordname;
    }

    public void setRecordname(String recordname) {
        this.recordname = recordname;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public AccountBook getAccountbook() {
        return accountbook;
    }

    public void setAccountbook(AccountBook accountbook) {
        this.accountbook = accountbook;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public Long getRecordtime() {
        return recordtime;
    }

    public void setRecordtime(Long recordtime) {
        this.recordtime = recordtime;
    }

    @Override
    public String toString() {
        return "AccountRecord{" +
                "id=" + id +
                ", icon=" + icon +
                ", recordname='" + recordname + '\'' +
                ", money='" + money + '\'' +
                ", remark='" + remark + '\'' +
                ", account=" + account +
                ", accountbook=" + accountbook +
                ", member='" + member + '\'' +
                ", recordtime=" + recordtime +
                '}';
    }
}
