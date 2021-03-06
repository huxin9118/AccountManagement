package com.swjtu.huxin.accountmanagement.service;

import android.database.sqlite.SQLiteDatabase;

import com.swjtu.huxin.accountmanagement.dao.AccountRecordDao;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.dao.DatabaseManager;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by huxin on 2017/2/28.
 */

public class AccountRecordService {
    private DatabaseManager databaseManager;
    
    public AccountRecordService(){
        databaseManager = DatabaseManager.getInstance(DatabaseHelper.getInstance());
    }
    

    public long addAccountRecord(AccountRecord record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        long id = dao.insert(record);
        databaseManager.closeDatabase();
        return id;
    }

    public long removeAccountRecord(AccountRecord record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        long id = dao.delete(record);
        databaseManager.closeDatabase();
        return id;
    }

    public long updateAccountRecord(AccountRecord record){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        long id = dao.update(record);
        databaseManager.closeDatabase();
        return id;
    }

    public AccountRecord findAccountRecord(int recordID){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        AccountRecord record = dao.find(recordID);
        databaseManager.closeDatabase();
        return record;
    }

    public List<AccountRecord> getAccountRecordListByTime(long firsttime, long lasttime, String recordname, Account account,String member){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListByTime(firsttime,lasttime,recordname,account,member);
        databaseManager.closeDatabase();
        return records;
    }

    public List<String> getMoneyByTime(long firsttime, long lasttime){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<String> MoneyList = dao.getMoneyListByTime(firsttime,lasttime);
        databaseManager.closeDatabase();
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
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListGroupByRecordname(firsttime,lasttime,isPositive);
        databaseManager.closeDatabase();
        return records;
    }

    public List<AccountRecord> getAccountRecordListGroupByMember(Date firsttime, Date lasttime,boolean isPositive){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        List<AccountRecord> records = dao.getListGroupByMember(firsttime,lasttime,isPositive);
        databaseManager.closeDatabase();
        return records;
    }

    public String getRangeTotalMoneyByRecordname(Date firsttime, Date lasttime, String recordname,boolean isPositive){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        String money = dao.getRangeTotalMoneyByRecordname(firsttime,lasttime,recordname,isPositive);
        databaseManager.closeDatabase();
        return new DecimalFormat("0.00").format(Double.parseDouble(money));
    }

    /**
     * account相关类专用
     * @param Account
     * @return
     */
    public String getTotalMoneyByAccount(Account Account){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        String money = dao.getTotalMoneyByAccount(Account);
        databaseManager.closeDatabase();
        return money;
    }

    /**
     * account相关类专用
     * @param firsttime
     * @param lasttime
     * @param Account
     * @param isPositive
     * @return
     */
    public String getRangeTotalMoneyByAccount(Date firsttime, Date lasttime,Account Account,boolean isPositive){
        SQLiteDatabase db = databaseManager.getWritableDatabase();
        AccountRecordDao dao = new AccountRecordDao(db);
        String money = dao.getRangeTotalMoneyByAccount(firsttime,lasttime,Account,isPositive);
        databaseManager.closeDatabase();
        return money;
    }
}
