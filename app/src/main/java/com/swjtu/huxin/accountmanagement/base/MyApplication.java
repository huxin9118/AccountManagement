package com.swjtu.huxin.accountmanagement.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.StrictMode;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;
import com.swjtu.huxin.accountmanagement.domain.AddItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import jp.wasabeef.glide.transformations.BlurTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by huxin on 2017/2/28.
 */

public class MyApplication extends Application{
    private static MyApplication application;
    private Context context;
    private ArrayList<AddItem> shouruAddItems;
    private ArrayList<AddItem> zhichuAddItems;
    private Map<Integer,Account> accounts;
    private Map<Integer,AccountBook> accountBooks;
    private Set<String> members;
    private int screenHeight;
    private int screenWidth;
    private String myTheme;
    private DataChangeObservable dataChangeObservable;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/fzltxh.ttf")
                .setFontAttrId(R.attr.fontPath).build());

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String myTheme = sharedPreferences.getString("currentTheme","MyTheme_White");
        setMyTheme(myTheme);

        dataChangeObservable = new DataChangeObservable();

//        int[] attrsArray = { R.attr.mainBackgrount };
//        TypedArray typedArray = obtainStyledAttributes(attrsArray);
//        int imgResID = typedArray.getResourceId(0,-1);
//        typedArray.recycle();
//        Glide.with(this).load(imgResID).bitmapTransform(new BlurTransformation(this, 10)).into();
    }

    public static MyApplication getApplication() {
        return application;
    }

    public Context getContext() {
        return context;
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

    public String getMyTheme() {
        return myTheme;
    }

    public void setMyTheme(String myTheme) {
        this.myTheme = myTheme;
    }

    public DataChangeObservable getDataChangeObservable() {
        return dataChangeObservable;
    }
}
