package com.swjtu.huxin.accountmanagement.base;

import android.app.Application;
import android.content.Context;

import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;
import com.swjtu.huxin.accountmanagement.domain.AddItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by huxin on 2017/2/28.
 */

public class MyApplication extends Application{
    private static Context context;
    private static MyApplication application;
    private ArrayList<AddItem> shouruAddItems;
    private ArrayList<AddItem> zhichuAddItems;
    private Map<Integer,Account> accounts;
    private Map<Integer,AccountBook> accountBooks;
    private Set<String> members;
    private int screenHeight;
    private int screenWidth;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication getApplication() {
        return application;
    }

    public ArrayList<AddItem> getShouruAddItems() {
        return shouruAddItems;
    }

    public void setShouruAddItems(ArrayList<AddItem> shouruAddItems) {
        this.shouruAddItems = shouruAddItems;
    }

    public ArrayList<AddItem> getZhichuAddItems() {
        return zhichuAddItems;
    }

    public void setZhichuAddItems(ArrayList<AddItem> zhichuAddItems) {
        this.zhichuAddItems = zhichuAddItems;
    }

    public Map<Integer, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<Integer, Account> accounts) {
        this.accounts = accounts;
    }

    public Map<Integer, AccountBook> getAccountBooks() {
        return accountBooks;
    }

    public void setAccountBooks(Map<Integer, AccountBook> accountBooks) {
        this.accountBooks = accountBooks;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }
}
