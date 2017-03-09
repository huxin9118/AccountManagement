package com.swjtu.huxin.accountmanagement.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swjtu.huxin.accountmanagement.application.MyApplication;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountBookDao {
    private SQLiteDatabase db;
    public AccountBookDao(SQLiteDatabase db){
        this.db = db;
    }

    public long insert(AccountBook AccountBook){
        //以键值对的形式保存要存入数据库的数据
        ContentValues cv = new ContentValues();

        cv.put("bookname", AccountBook.getBookname());
        cv.put("type", AccountBook.getType());
        //返回值是改行的主键，如果出错返回-1
        return db.insert("account_book", null, cv);
    }

    public long update(AccountBook AccountBook){
        //以键值对的形式保存要存入数据库的数据
        ContentValues cv = new ContentValues();
        cv.put("bookname", AccountBook.getBookname());
        //返回值是影响的行数
        return db.update("account_book", cv,"id = ?", new String[]{AccountBook.getId()+""});
    }

    public long delete(AccountBook AccountBook){
        //返回值是影响的行数
        return db.delete("account_book","id = ?", new String[]{AccountBook.getId()+""});
    }

    public AccountBook find(int AccountBookID){
        Cursor cs = db.query("account_book", null, "id = ?", new String[]{AccountBookID+""}, null, null,null, "0,1");
        AccountBook book = new AccountBook();
        while(cs.moveToNext()){
            MyApplication app = MyApplication.getApplication();
            //获取指定列的索引值
            book.setId(cs.getInt(cs.getColumnIndex("id")));
            book.setBookname(cs.getString(cs.getColumnIndex("bookname")));
            book.setType(cs.getInt(cs.getColumnIndex("type")));
        }
        cs.close();
        return book;
    }

    public Map<Integer, AccountBook> getMap(){
        Cursor cs = db.query("account_book", null, null, null, null, null, null, null);
        Map<Integer, AccountBook> books = new TreeMap<Integer, AccountBook>();
        while(cs.moveToNext()){
            MyApplication app = MyApplication.getApplication();
            //获取指定列的索引值
            AccountBook book = new AccountBook();
            book.setId(cs.getInt(cs.getColumnIndex("id")));
            book.setBookname(cs.getString(cs.getColumnIndex("bookname")));
            book.setType(cs.getInt(cs.getColumnIndex("type")));
            books.put(cs.getInt(cs.getColumnIndex("id")),book);
        }
        cs.close();
        return books;
    }
}
