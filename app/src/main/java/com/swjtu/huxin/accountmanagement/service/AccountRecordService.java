package com.swjtu.huxin.accountmanagement.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.swjtu.huxin.accountmanagement.dao.AccountDao;
import com.swjtu.huxin.accountmanagement.dao.AccountRecordDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountRecordService {

    public long addAccountRecord(AccountRecord record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        long id = dao.insert(record);
        db.close();
        return id;
    }

    public long removeAccountRecord(AccountRecord record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        long id = dao.delete(record);
        db.close();
        return id;
    }

    public long updateAccountRecord(AccountRecord record){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        long id = dao.update(record);
        db.close();
        return id;
    }

    public AccountRecord findAccountRecord(int recordID){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        AccountRecord record = dao.find(recordID);
        db.close();
        return record;
    }

    public List<AccountRecord> getAccountRecordListByTime(long firsttime, long lasttime){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListByTime(firsttime,lasttime);
        db.close();
        return records;
    }

    public String[] getMonthMoneyByTime(int month,int indexYear){
        long firstMilliSeconds = TimeUtils.getMonthFirstMilliSeconds(month,indexYear);
        long lastMilliSeconds = TimeUtils.getMonthLastMilliSeconds(month,indexYear);
        List<AccountRecord> records = getAccountRecordListByTime(firstMilliSeconds,lastMilliSeconds);
        BigDecimal numShouru = new BigDecimal("0.00");
        BigDecimal numZhichu = new BigDecimal("0.00");
        for(int i = 0; i<records.size(); i++){
            BigDecimal money = new BigDecimal(records.get(i).getMoney());
            if(money.doubleValue() > 0) numShouru = numShouru.add(money);
            else numZhichu = numZhichu.add(money);
        }
        return new String[]{numShouru.toString(),numZhichu.negate().toString()};
    }

    public String[] getDayMoneyByTime(int day,int indexMonth,int indexYear){
        long firstMilliSeconds = TimeUtils.getDayFirstMilliSeconds(day,indexMonth,indexYear);
        long lastMilliSeconds = TimeUtils.getDayLastMilliSeconds(day,indexMonth,indexYear);
        List<AccountRecord> records = getAccountRecordListByTime(firstMilliSeconds,lastMilliSeconds);
        BigDecimal numShouru = new BigDecimal("0.00");
        BigDecimal numZhichu = new BigDecimal("0.00");
        for(int i = 0; i<records.size(); i++){
            BigDecimal money = new BigDecimal(records.get(i).getMoney());
            if(money.doubleValue() > 0) numShouru = numShouru.add(money);
            else numZhichu = numZhichu.add(money);
        }
        return new String[]{numShouru.toString(),numZhichu.negate().toString()};
    }

    public String[] getDayMoneyByRecords(List<AccountRecord>records){
        BigDecimal numShouru = new BigDecimal("0.00");
        BigDecimal numZhichu = new BigDecimal("0.00");
        for(int i = 0; i<records.size(); i++){
            BigDecimal money = new BigDecimal(records.get(i).getMoney());
            if(money.doubleValue() > 0) numShouru = numShouru.add(money);
            else numZhichu = numZhichu.add(money);
        }
        return new String[]{numShouru.toString(),numZhichu.negate().toString()};
    }
}
