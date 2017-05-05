package com.swjtu.huxin.accountmanagement.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.swjtu.huxin.accountmanagement.dao.AccountBookDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.dao.DatabaseManager;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;

import java.util.List;
import java.util.Map;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountBookService {
    private DatabaseManager databaseManager;

    public AccountBookService(){
        databaseManager = DatabaseManager.getInstance(DatabaseHelper.getInstance());
    }

    public long addAccountBook(AccountBook record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        long id = dao.insert(record);
        databaseManager.closeDatabase();
        return id;
    }

    public long removeAccountBook(AccountBook record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        long id = dao.delete(record);
        databaseManager.closeDatabase();
        return id;
    }

    public long updateAccountBook(AccountBook record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        long id = dao.update(record);
        databaseManager.closeDatabase();
        return id;
    }

    public AccountBook findAccountBook(int recordID){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        AccountBook book = dao.find(recordID);
        databaseManager.closeDatabase();
        return book;
    }

    public Map<Integer, AccountBook> getAccountBookMap(){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        Map<Integer, AccountBook> books = dao.getMap();
        databaseManager.closeDatabase();
        return books;
    }
}
