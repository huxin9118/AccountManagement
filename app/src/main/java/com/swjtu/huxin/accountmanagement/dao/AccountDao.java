package com.swjtu.huxin.accountmanagement.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swjtu.huxin.accountmanagement.application.MyApplication;
import com.swjtu.huxin.accountmanagement.domain.Account;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountDao {
    private SQLiteDatabase db;
    public AccountDao(SQLiteDatabase db){
        this.db = db;
    }

    public long insert(Account account){
        //以键值对的形式保存要存入数据库的数据
        ContentValues cv = new ContentValues();
        cv.put("color", account.getColor());
        cv.put("accountname", account.getAccountname());
        cv.put("money", account.getMoney());
        cv.put("type", account.getType());
        //返回值是改行的主键，如果出错返回-1
        return db.insert("account", null, cv);
    }

    public long update(Account account){
        //以键值对的形式保存要存入数据库的数据
        ContentValues cv = new ContentValues();
        cv.put("color", account.getColor());
        cv.put("accountname", account.getAccountname());
        cv.put("money", account.getMoney());
        //返回值是影响的行数
        return db.update("account", cv,"id = ?", new String[]{account.getId()+""});
    }

    public long delete(Account account){
        //返回值是影响的行数
        return db.delete("account","id = ?", new String[]{account.getId()+""});
    }

    public Account find(int accountID){
        Cursor cs = db.query("account", null, "id = ?", new String[]{accountID+""}, null, null,null, "0,1");
        Account account = new Account();
        while(cs.moveToNext()){
            MyApplication app = MyApplication.getApplication();
            //获取指定列的索引值
            account.setId(cs.getInt(cs.getColumnIndex("id")));
            account.setColor(cs.getInt(cs.getColumnIndex("color")));
            account.setAccountname(cs.getString(cs.getColumnIndex("accountname")));
            account.setMoney(cs.getString(cs.getColumnIndex("money")));
            account.setType(cs.getInt(cs.getColumnIndex("type")));
        }
        cs.close();
        return account;
    }

    public Map<Integer, Account> getMap(){
        Cursor cs = db.query("account", null, null, null, null, null, null, null);
        Map<Integer, Account> accounts = new TreeMap<Integer, Account>();
        while(cs.moveToNext()){
            //获取指定列的索引值
            Account account = new Account();
            account.setId(cs.getInt(cs.getColumnIndex("id")));
            account.setColor(cs.getInt(cs.getColumnIndex("color")));
            account.setAccountname(cs.getString(cs.getColumnIndex("accountname")));
            account.setMoney(cs.getString(cs.getColumnIndex("money")));
            account.setType(cs.getInt(cs.getColumnIndex("type")));
            accounts.put(cs.getInt(cs.getColumnIndex("id")), account);
        }
        cs.close();
        return accounts;
    }
}
