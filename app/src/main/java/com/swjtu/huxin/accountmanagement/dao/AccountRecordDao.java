package com.swjtu.huxin.accountmanagement.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swjtu.huxin.accountmanagement.application.myApplication;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountRecordDao {
    private SQLiteDatabase db;
    public AccountRecordDao(SQLiteDatabase db){
        this.db = db;
    }

    public long insert(AccountRecord record){
        //以键值对的形式保存要存入数据库的数据
        ContentValues cv = new ContentValues();
        cv.put("icon", record.getIcon());
        cv.put("recordname", record.getRecordname());
        cv.put("money", record.getMoney());
        cv.put("remark", record.getRemark());
        cv.put("account_id", record.getAccount().getId());
        cv.put("accountbook_id", record.getAccountbook().getId());
        cv.put("member", record.getMember());
        cv.put("recordtime", record.getRecordtime());
        //返回值是改行的主键，如果出错返回-1
        return db.insert("account_record", null, cv);
    }

    public long update(AccountRecord record){
        //以键值对的形式保存要存入数据库的数据
        ContentValues cv = new ContentValues();
        cv.put("icon", record.getIcon());
        cv.put("recordname", record.getRecordname());
        cv.put("money", record.getMoney());
        cv.put("remark", record.getRemark());
        cv.put("account_id", record.getAccount().getId());
        cv.put("member", record.getMember());
        //返回值是影响的行数
        return db.update("account_record", cv,"id = ?", new String[]{record.getId()+""});
    }

    public long delete(AccountRecord record){
        //返回值是影响的行数
        return db.delete("account_record","id = ?", new String[]{record.getId()+""});
    }

    public AccountRecord find(int recordID){
        Cursor cs = db.query("account_record", null, "id = ?", new String[]{recordID+""}, null, null,null, "0,1");
        AccountRecord record = new AccountRecord();
        while(cs.moveToNext()){
            myApplication app = myApplication.getApplication();
            //获取指定列的索引值
            record.setId(cs.getInt(cs.getColumnIndex("id")));
            record.setIcon(cs.getInt(cs.getColumnIndex("icon")));
            record.setRecordname(cs.getString(cs.getColumnIndex("recordname")));
            record.setMoney(cs.getString(cs.getColumnIndex("money")));
            record.setRemark(cs.getString(cs.getColumnIndex("remark")));
            record.setAccount(app.getAccounts().get(cs.getInt(cs.getColumnIndex("account_id"))));
            record.setAccountbook(app.getAccountBooks().get(cs.getInt(cs.getColumnIndex("accountbook_id"))));
            record.setMember(cs.getString(cs.getColumnIndex("member")));
            record.setRecordtime(cs.getLong(cs.getColumnIndex("recordtime")));
        }
        cs.close();
        return record;
    }

    public List<AccountRecord> getListByTime(long firsttime,long lasttime){
        Cursor cs = db.query("account_record", null, "recordtime between ? and ?", new String[]{firsttime+"",lasttime+""}, null, null, "recordtime desc",null);
        List<AccountRecord> records = new ArrayList<AccountRecord>();
        while(cs.moveToNext()){
            myApplication app = myApplication.getApplication();
            //获取指定列的索引值
            AccountRecord record = new AccountRecord();
            record.setId(cs.getInt(cs.getColumnIndex("id")));
            record.setIcon(cs.getInt(cs.getColumnIndex("icon")));
            record.setRecordname(cs.getString(cs.getColumnIndex("recordname")));
            record.setMoney(cs.getString(cs.getColumnIndex("money")));
            record.setRemark(cs.getString(cs.getColumnIndex("remark")));
            record.setAccount(app.getAccounts().get(cs.getInt(cs.getColumnIndex("account_id"))));
            record.setAccountbook(app.getAccountBooks().get(cs.getInt(cs.getColumnIndex("accountbook_id"))));
            record.setMember(cs.getString(cs.getColumnIndex("member")));
            record.setRecordtime(cs.getLong(cs.getColumnIndex("recordtime")));
            records.add(record);
        }
        cs.close();
        return records;
    }
}
