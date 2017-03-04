package com.swjtu.huxin.accountmanagement.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swjtu.huxin.accountmanagement.dao.AccountBookDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;

import java.util.List;
import java.util.Map;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountBookService {

    public long addAccountBook(AccountBook record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        long id = dao.insert(record);
        db.close();
        return id;
    }

    public long removeAccountBook(AccountBook record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        long id = dao.delete(record);
        db.close();
        return id;
    }

    public long updateAccountBook(AccountBook record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        long id = dao.update(record);
        db.close();
        return id;
    }

    public AccountBook findAccountBook(int recordID){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        AccountBook book = dao.find(recordID);
        db.close();
        return book;
    }

    public Map<Integer, AccountBook> getAccountBookMap(){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountBookDao dao = new AccountBookDao(db);
        Map<Integer, AccountBook> books = dao.getMap();
        db.close();
        return books;
    }
}
