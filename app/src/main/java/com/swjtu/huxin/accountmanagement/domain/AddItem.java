package com.swjtu.huxin.accountmanagement.domain;

/**
 * Created by huxin on 2017/2/25.
 */

public class AddItem {
    private String iconAddItem;
    private String nameAddItem;

    public AddItem() {
    }

    public String getNameAddItem() { return nameAddItem; }

    public void setNameAddItem(String nameAddItem) {
        this.nameAddItem = nameAddItem;
    }

    public String getIconAddItem() {
        return iconAddItem;
    }

    public void setIconAddItem(String iconAddItem) {
        this.iconAddItem = iconAddItem;
    }
}
