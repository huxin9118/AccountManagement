package com.swjtu.huxin.accountmanagement.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.swjtu.huxin.accountmanagement.base.MyApplication;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by huxin on 2017/2/27.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String ACCOUNT_RECORD=
            "create table account_record ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "icon VARCHAR(20) NOT NULL,"+
                "recordname VARCHAR(10) NOT NULL,"+
                "money VARCHAR(20) NOT NULL,"+
                "remark VARCHAR(255) NOT NULL,"+
                "account_id INTEGER NOT NULL,"+
                "accountbook_id INTEGER NOT NULL,"+
                "member VARCHAR(20) NOT NULL,"+
                "recordtime TIMESTAMP NOT NULL)";
    private static final String ACCOUNT =
            "create table account ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "color VARCHAR(10) NOT NULL,"+
                "accountname VARCHAR(20) NOT NULL,"+
                "accountdetail VARCHAR(50) NOT NULL,"+
                "type INTEGER NOT NULL,"+
                "money VARCHAR(20) NOT NULL)";

    private static final String ACCOUNT_BOOK =
            "create table account_book ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "bookname VARCHAR(20) NOT NULL,"+
                    "type INTEGER NOT NULL)";

    /**
     * 饿汉单例模式
     * 在类加载时就完成了初始化，所以类加载较慢，但获取对象的速度快,无法自定义参数
     */
    private static final DatabaseHelper instance = new DatabaseHelper(MyApplication.getApplication().getContext(),
            "accountmanagement.db",null,1);//静态私有成员，已初始化

    public static DatabaseHelper getInstance(){
        return instance;
    }

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
    }

    /**
     * 懒汉单例模式(对象延迟加载 Lazy Loading)
     * 类加载时，不创建实例，因此类加载速度快，但运行时获取对象的速度慢
     */
//    public static DatabaseHelper getInstance(){
//        return DatabaseHelperSingletonHolder.INSTANCE;
//    }
//
//    private static class DatabaseHelperSingletonHolder {
//        private static final DatabaseHelper INSTANCE = new DatabaseHelper(getContext(), "accountmanagement.db",null,1);
//    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(ACCOUNT_RECORD);
            db.execSQL(ACCOUNT);
            db.execSQL(ACCOUNT_BOOK);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
