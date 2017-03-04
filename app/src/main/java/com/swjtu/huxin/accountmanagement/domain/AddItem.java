package com.swjtu.huxin.accountmanagement.domain;

/**
 * Created by huxin on 2017/2/25.
 */

public class AddItem {
    private int iconAddItem;
    private String nameAddItem;

    public AddItem() {
    }

    public AddItem(int IDAddItem, int iconAddItem, String nameAddItem) {
        this.iconAddItem = iconAddItem;
        this.nameAddItem = nameAddItem;
    }

    public String getNameAddItem() { return nameAddItem; }

    public void setNameAddItem(String nameAddItem) {
        this.nameAddItem = nameAddItem;
    }

    public int getIconAddItem() {
        return iconAddItem;
    }

    public void setIconAddItem(int iconAddItem) {
        this.iconAddItem = iconAddItem;
    }
}
