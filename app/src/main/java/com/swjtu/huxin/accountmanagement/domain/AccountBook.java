package com.swjtu.huxin.accountmanagement.domain;

import java.io.Serializable;

/**
 * Created by huxin on 2017/2/27.
 */

public class AccountBook implements Serializable {
    int id;
    String bookname;
    int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccountBook{" +
                "id=" + id +
                ", bookname='" + bookname + '\'' +
                ", type=" + type +
                '}';
    }
}
