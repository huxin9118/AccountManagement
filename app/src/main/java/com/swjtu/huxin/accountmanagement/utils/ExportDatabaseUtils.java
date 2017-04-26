package com.swjtu.huxin.accountmanagement.utils;

import java.io.File;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;

/**
 * Created by huxin on 2017/4/11.
 */

public class ExportDatabaseUtils {
    private static SQLiteDatabase db = DatabaseHelper.getInstance().getWritableDatabase();

    public static WritableWorkbook exportData(String xlsPath,String xlsName,Date start,Date end) {
        try {
            //  Log.i("mdb", db.getPath());
            // get the tables out of the given sqlite database
            String sql = "SELECT * FROM sqlite_master";

            Cursor cur = db.rawQuery(sql, new String[0]);
            cur.moveToFirst();
            List<String> tableNames = new ArrayList<String>();
            while (cur.getPosition() < cur.getCount()) {
                // don't process these two tables since they are used
                // for metadata
                String tableName = cur.getString(cur.getColumnIndex("name"));
                if (!tableName.equals("android_metadata")
                        && !tableName.equals("sqlite_sequence")) {
                    tableNames.add(tableName);
                }
                cur.moveToNext();
            }
            return writeExcel(tableNames,xlsPath,xlsName,start,end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static WritableWorkbook writeExcel(List<String> tableNames,String xlsPath,String xlsName,Date start,Date end) {
        WritableWorkbook wwb = null;
        String fileName;
        fileName = xlsPath + "/" + xlsName + ".xls";

        try {
            // 首先要使用Workbook类的工厂方法创建一个可写入的工作薄(Workbook)对象
            wwb = Workbook.createWorkbook(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int k = 0; k < tableNames.size(); k++) {
            String tableName = tableNames.get(k);
            Log.i("===", k+"==="+tableName);

            String sql;
            if("account_record".equals(tableName))
                sql = "select * from " + tableName + " WHERE recordtime between " + start.getTime() + " and " + end.getTime();
            else
                sql = "select * from " + tableName;
            Cursor cur = db.rawQuery(sql, new String[0]);
            int numcols = cur.getColumnCount();
            int numrows = cur.getCount();
            // Log.i("row", numrows + "");
            // Log.i("col", numcols + "");

            String records[][] = new String[numrows + 1][numcols];// 存放答案，多一行标题行
            int r = 0;
            if (cur.moveToFirst()) {
                while (cur.getPosition() < cur.getCount()) {
                    for (int c = 0; c < numcols; c++) {
                        if (r == 0) {
                            records[r][c] = cur.getColumnName(c);
                            if ("recordtime".equals(cur.getColumnName(c))) {
                                records[r + 1][c] = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(Long.parseLong(cur.getString(c))));
                            } else {
                                records[r + 1][c] = cur.getString(c);
                            }
                        } else {
                            if ("recordtime".equals(cur.getColumnName(c))) {
                                records[r + 1][c] = new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(Long.parseLong(cur.getString(c))));
                            } else {
                                records[r + 1][c] = cur.getString(c);
                            }
                        }
                        //  Log.i("value" + r + " " + c, records[r][c]);
                    }
                    cur.moveToNext();
                    r++;
                }
                cur.close();
            }
            if (wwb != null) {
                // 创建一个可写入的工作表
                // Workbook的createSheet方法有两个参数，第一个是工作表的名称，第二个是工作表在工作薄中的位置
                WritableSheet ws = wwb.createSheet(tableName, k);
                // 下面开始添加单元格
                for (int i = 0; i < numrows + 1; i++) {
                    for (int j = 0; j < numcols; j++) {
                        // 这里需要注意的是，在Excel中，第一个参数表示列，第二个表示行
                        Label labelC = new Label(j, i, records[i][j]);
                        //      Log.i("Newvalue" + i + " " + j, records[i][j]);
                        try {
                            // 将生成的单元格添加到工作表中
                            ws.addCell(labelC);
                        } catch (RowsExceededException e) {
                            e.printStackTrace();
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
        return wwb;
    }
}