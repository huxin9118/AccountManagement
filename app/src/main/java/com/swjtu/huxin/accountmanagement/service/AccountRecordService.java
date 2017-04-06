package com.swjtu.huxin.accountmanagement.service;

import android.database.sqlite.SQLiteDatabase;

import com.swjtu.huxin.accountmanagement.dao.AccountRecordDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.util.Date;
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

    public List<AccountRecord> getAccountRecordListByTime(long firsttime, long lasttime, String recordname, Account account,String member){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListByTime(firsttime,lasttime,recordname,account,member);
        db.close();
        return records;
    }

    public List<String> getMoneyByTime(long firsttime, long lasttime){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<String> MoneyList = dao.getMoneyListByTime(firsttime,lasttime);
        db.close();
        return MoneyList;
    }

    public String[] getMonthMoneyByTime(int month,int indexYear){
        long firstMilliSeconds = TimeUtils.getMonthFirstMilliSeconds(month,indexYear);
        long lastMilliSeconds = TimeUtils.getMonthLastMilliSeconds(month,indexYear);
        List<String> MoneyList = getMoneyByTime(firstMilliSeconds,lastMilliSeconds);
        BigDecimal numShouru = new BigDecimal("0.00");
        BigDecimal numZhichu = new BigDecimal("0.00");
        for(int i = 0; i<MoneyList.size(); i++){
            BigDecimal money = new BigDecimal(MoneyList.get(i));
            if(money.doubleValue() > 0) numShouru = numShouru.add(money);
            else numZhichu = numZhichu.add(money);
        }
        return new String[]{numShouru.toString(),numZhichu.negate().toString()};
    }

    public String[] getDayMoneyByTime(int day,int indexMonth,int indexYear){
        long firstMilliSeconds = TimeUtils.getDayFirstMilliSeconds(day,indexMonth,indexYear);
        long lastMilliSeconds = TimeUtils.getDayLastMilliSeconds(day,indexMonth,indexYear);
        List<String> MoneyList = getMoneyByTime(firstMilliSeconds,lastMilliSeconds);
        BigDecimal numShouru = new BigDecimal("0.00");
        BigDecimal numZhichu = new BigDecimal("0.00");
        for(int i = 0; i<MoneyList.size(); i++){
            BigDecimal money = new BigDecimal(MoneyList.get(i));
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

    public BigDecimal getRangeTotalMoneyByTime(Date start, Date end,boolean isPositive){
        long firstMilliSeconds = start.getTime();
        long lastMilliSeconds = end.getTime();
        List<String> MoneyList = getMoneyByTime(firstMilliSeconds,lastMilliSeconds);
        BigDecimal numShouru = new BigDecimal("0.00");
        BigDecimal numZhichu = new BigDecimal("0.00");
        for(int i = 0; i<MoneyList.size(); i++){
            BigDecimal money = new BigDecimal(MoneyList.get(i));
            if(isPositive && money.doubleValue() > 0) numShouru = numShouru.add(money);
            if(!isPositive && money.doubleValue() < 0) numZhichu = numZhichu.add(money);
        }
        if(isPositive)
            return numShouru;
        else
            return numZhichu;
    }

    public List<AccountRecord> getAccountRecordListGroupByRecordname(Date firsttime, Date lasttime,boolean isPositive){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListGroupByRecordname(firsttime,lasttime,isPositive);
        db.close();
        return records;
    }

    public List<AccountRecord> getAccountRecordListGroupByMember(Date firsttime, Date lasttime,boolean isPositive){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListGroupByMember(firsttime,lasttime,isPositive);
        db.close();
        return records;
    }

    public String getRangeTotalMoneyByRecordname(Date firsttime, Date lasttime, String recordname){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        String money = dao.getRangeTotalMoneyByRecordname(firsttime,lasttime,recordname);
        db.close();
        return money;
    }

    public String getTotalMoneyByAccount(Account Account){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        String money = dao.getTotalMoneyByAccount(Account);
        db.close();
        return money;
    }

    public String getRangeTotalMoneyByAccount(Date firsttime, Date lasttime,Account Account,boolean isPositive){
        SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        String money = dao.getRangeTotalMoneyByAccount(firsttime,lasttime,Account,isPositive);
        db.close();
        return money;
    }
}
