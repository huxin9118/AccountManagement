package com.swjtu.huxin.accountmanagement.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swjtu.huxin.accountmanagement.dao.AccountDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.domain.Account;

import java.util.Map;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountService {

    public long addAccount(Account record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        long id = dao.insert(record);
        db.close();
        return id;
    }

    public long removeAccount(Account record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        long id = dao.delete(record);
        db.close();
        return id;
    }

    public long updateAccount(Account record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        long id = dao.update(record);
        db.close();
        return id;
    }

    public Account findAccount(int recordID){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        Account record = dao.find(recordID);
        db.close();
        return record;
    }

    public Map<Integer, Account> getAccountMap(){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        Map<Integer, Account> records = dao.getMap();
        db.close();
        return records;
    }
}
