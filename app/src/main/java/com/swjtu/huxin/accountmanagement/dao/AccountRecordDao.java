package com.swjtu.huxin.accountmanagement.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;

import java.util.ArrayList;
import java.util.Date;
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
        cv.put("recordtime", record.getRecordtime());
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
            MyApplication app = MyApplication.getApplication();
            //获取指定列的索引值
            record.setId(cs.getInt(cs.getColumnIndex("id")));
            record.setIcon(cs.getString(cs.getColumnIndex("icon")));
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

    public List<AccountRecord> getListByTime(long firsttime,long lasttime,String recordname,Account account,String member){
        String sql = "SELECT * FROM account_record WHERE recordtime between ? and ?";
        if(recordname != null)
            sql = sql + " AND recordname = '" + recordname + "'";
        if(account != null)
            sql = sql + " AND account_id = " + account.getId();
        if(member != null)
            sql = sql + " AND member = '" + member + "'";
        sql = sql +" ORDER BY recordtime desc";
        Cursor cs = db.rawQuery(sql, new String[]{firsttime+"",lasttime+""});
        List<AccountRecord> records = new ArrayList<AccountRecord>();
        while(cs.moveToNext()){
            MyApplication app = MyApplication.getApplication();
            //获取指定列的索引值
            AccountRecord record = new AccountRecord();
            record.setId(cs.getInt(cs.getColumnIndex("id")));
            record.setIcon(cs.getString(cs.getColumnIndex("icon")));
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

    public List<String> getMoneyListByTime(long firsttime,long lasttime){
        Cursor cs = db.query("account_record", new String[]{"money"}, "recordtime between ? and ?", new String[]{firsttime+"",lasttime+""}, null, null, "recordtime desc",null);
        List<String> MoneyList = new ArrayList<String>();
        while(cs.moveToNext()){
            MoneyList.add(cs.getString(cs.getColumnIndex("money")));
        }
        cs.close();
        return MoneyList;
    }

    public List<AccountRecord> getListGroupByRecordname(Date firsttime, Date lasttime, boolean isPositive){
        String sql;
        if(isPositive)
            sql = "SELECT recordname ,icon,SUM(money) money ,COUNT(money) counts FROM account_record WHERE money > 0 AND recordtime between ? and ? GROUP BY recordname ORDER BY recordname";
        else
            sql = "SELECT recordname ,icon,SUM(money) money ,COUNT(money) counts FROM account_record WHERE money < 0 AND recordtime between ? and ? GROUP BY recordname ORDER BY recordname";
        Cursor cs = db.rawQuery(sql, new String[]{firsttime.getTime()+"",lasttime.getTime()+""});
        List<AccountRecord> records = new ArrayList<AccountRecord>();
        while(cs.moveToNext()){
            //获取指定列的索引值
            AccountRecord record = new AccountRecord();
            record.setIcon(cs.getString(cs.getColumnIndex("icon")));
            record.setRecordname(cs.getString(cs.getColumnIndex("recordname")));
            record.setMoney(cs.getString(cs.getColumnIndex("money")));
            record.setId(cs.getInt(cs.getColumnIndex("counts")));
            records.add(record);
        }
        cs.close();
        return records;
    }

    public List<AccountRecord> getListGroupByMember(Date firsttime, Date lasttime, boolean isPositive){
        String sql;
        if(isPositive)
            sql = "SELECT member ,SUM(money) money FROM account_record WHERE money > 0 AND recordtime between ? and ? GROUP BY member ORDER BY member";
        else
            sql = "SELECT member ,SUM(money) money FROM account_record WHERE money < 0 AND recordtime between ? and ? GROUP BY member ORDER BY member";
        Cursor cs = db.rawQuery(sql, new String[]{firsttime.getTime()+"",lasttime.getTime()+""});
        List<AccountRecord> records = new ArrayList<AccountRecord>();
        while(cs.moveToNext()){
            //获取指定列的索引值
            AccountRecord record = new AccountRecord();
            record.setMember(cs.getString(cs.getColumnIndex("member")));
            record.setMoney(cs.getString(cs.getColumnIndex("money")));
            records.add(record);
        }
        cs.close();
        return records;
    }

    public String getRangeTotalMoneyByRecordname(Date firsttime, Date lasttime,String recordname){
        String sql = "SELECT SUM(money) money FROM account_record WHERE recordname = ? AND recordtime between ? and ?";
        Cursor cs = db.rawQuery(sql, new String[]{recordname,firsttime.getTime()+"",lasttime.getTime()+""});
        cs.moveToNext();
        String money = cs.getString(cs.getColumnIndex("money"));
        cs.close();
        if(money == null) return "0.00";
        return money;
    }

    public String getTotalMoneyByAccount(Account account){
        String sql = "SELECT SUM(money) money FROM account_record WHERE recordtime <= ?";
        if(account != null)
            sql = sql + " AND account_id = " + account.getId();
        Cursor cs = db.rawQuery(sql, new String[]{new Date().getTime()+""});
        cs.moveToNext();
        String money = cs.getString(cs.getColumnIndex("money"));
        cs.close();
        if(money == null) return "0.00";
        return money;
    }

    public String getRangeTotalMoneyByAccount(Date firsttime, Date lasttime,Account Account,boolean isPositive){
        String sql;
        if(isPositive)
            sql = "SELECT SUM(money) money FROM account_record WHERE money > 0 AND account_id = ? AND recordtime between ? and ?";
        else
            sql = "SELECT SUM(money) money FROM account_record WHERE money < 0 AND account_id = ? AND recordtime between ? and ?";
        Cursor cs = db.rawQuery(sql, new String[]{Account.getId()+"",firsttime.getTime()+"",lasttime.getTime()+""});
        cs.moveToNext();
        String money = cs.getString(cs.getColumnIndex("money"));
        cs.close();
        if(money == null) return "0.00";
        return money;
    }
}
