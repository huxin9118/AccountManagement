package com.swjtu.huxin.accountmanagement.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.dao.AccountDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.dao.DatabaseManager;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;

import java.util.Map;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountService {
    private DatabaseManager databaseManager;

    public AccountService(){
        databaseManager = DatabaseManager.getInstance(DatabaseHelper.getInstance());
    }

    public long addAccount(Account record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        long id = dao.insert(record);
        databaseManager.closeDatabase();
        return id;
    }

    public long removeAccount(Account record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        long id = dao.delete(record);
        databaseManager.closeDatabase();
        return id;
    }

    public long updateAccount(Account record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        long id = dao.update(record);
        databaseManager.closeDatabase();
        return id;
    }

    public Account findAccount(int recordID){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        Account record = dao.find(recordID);
        databaseManager.closeDatabase();
        return record;
    }

    public Map<Integer, Account> getAccountMap(){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountDao dao = new AccountDao(db);
        Map<Integer, Account> records = dao.getMap();
        databaseManager.closeDatabase();
        return records;
    }
}

