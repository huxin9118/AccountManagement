package com.swjtu.huxin.accountmanagement.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;
import com.swjtu.huxin.accountmanagement.utils.ExportDatabaseUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * Created by huxin on 2017/3/11.
 */

public class MoreExportActivity extends BaseAppCompatActivity {
    private LinearLayout btnBack;
    private RelativeLayout btnAccountbook;
    private RelativeLayout btnTime;
    private TextView textAccountbook;
    private TextView textTime;

    private PopupWindow accountbookPopupWindow;
    private int selectAccountbook;
    private Date start;
    private Date end;

    private TextView textSdcard;
    private ImageView sendSdcard;

    private EditText editEmail;
    private ImageView clearEmail;
    private ImageView sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_export);
        initView();
    }

    private void initView() {
        btnBack = (LinearLayout) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                finish();
            }
        });

        btnAccountbook = (RelativeLayout) findViewById(R.id.btnAccountbook);
        btnTime = (RelativeLayout) findViewById(R.id.btnTime);
        textAccountbook = (TextView) findViewById(R.id.textAccountbook);
        textTime = (TextView) findViewById(R.id.textTime);
        btnAccountbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(accountbookPopupWindow);
            }
        });

        start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        updateDatePickerText();
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreExportActivity.this, DateRangePickerActivity.class);
                intent.putExtra("back", "导出数据");
                intent.putExtra("start", start);
                intent.putExtra("end",end);
                startActivityForResult(intent, 1);
            }
        });

        textSdcard = (TextView) findViewById(R.id.text_sdcard);
        sendSdcard = (ImageView) findViewById(R.id.send_sdcard);
        textSdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooserIntent = new Intent(MoreExportActivity.this, DirectoryChooserActivity.class);
                DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .newDirectoryName("选择保存路径")
                        .allowReadOnlyDirectory(true)
                        .allowNewDirectoryNameModification(true)
                        .build();
                chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
                startActivityForResult(chooserIntent, 2);
            }
        });
        sendSdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"".equals(textSdcard.getText().toString())) {
                    Date now = new Date();
                    WritableWorkbook wwb = ExportDatabaseUtils.exportData(textSdcard.getText().toString(),
                            "accountmanagement"+new SimpleDateFormat("-yyyy年MM月dd日 HH:mm").format(now),start,end);
                    try {
                        // 从内存中写入文件中
                        wwb.write();
                        // 关闭资源，释放内存
                        wwb.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                    showToast("数据成功导出到  "+textSdcard.getText().toString()+ "/accountmanagement" +
                            new SimpleDateFormat("-yyyy年MM月dd日 HH:mm").format(now) + ".xls", Toast.LENGTH_SHORT);
                }
                else{
                    showToast("请先选择数据导出路径", Toast.LENGTH_SHORT);
                }
            }
        });

        editEmail = (EditText) findViewById(R.id.edit_email);
        clearEmail = (ImageView) findViewById(R.id.clear_email);
        sendEmail = (ImageView) findViewById(R.id.send_email);
        clearEmail.setVisibility(View.GONE);
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearEmail.setVisibility(View.VISIBLE);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        clearEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEmail.setText("");
                clearEmail.setVisibility(View.GONE);
            }
        });
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"".equals(editEmail.getText().toString())) {
                    Date now = new Date();
                    WritableWorkbook wwb = ExportDatabaseUtils.exportData(getExternalCacheDir().getPath(),
                            "accountmanagement" + new SimpleDateFormat("-yyyy年MM月dd日 HH:mm").format(now), start, end);
                    try {
                        // 从内存中写入文件中
                        wwb.write();
                        // 关闭资源，释放内存
                        wwb.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String[] tos = {editEmail.getText().toString()};
//                String[] ccs = { "way.ping.li@gmail.com" };
//                String[] bccs = {"way.ping.li@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, tos);
//                intent.putExtra(Intent.EXTRA_CC, ccs);
//                intent.putExtra(Intent.EXTRA_BCC, bccs);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "数据导出-accountmanagement" + new SimpleDateFormat("-yyyy年MM月dd日 HH:mm").format(now));
                    intent.putExtra(Intent.EXTRA_TEXT, "亲爱的accountmanagement用户，您在" + new SimpleDateFormat(" yyyy年MM月dd日 HH:mm ")
                            .format(now) + "成功导出数据，详见附件。");

                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + getExternalCacheDir()
                            + "/accountmanagement" + new SimpleDateFormat("-yyyy年MM月dd日 HH:mm").format(now) + ".xls"));
                    intent.setType("application/vnd.ms-excel");
                    intent.setType("message/rfc882");
                    Intent.createChooser(intent, "Choose Email Client");
                    startActivity(intent);
                }
                else{
                    showToast("请先输入收件箱地址", Toast.LENGTH_SHORT);
                    editEmail.requestFocus();
                }
            }
        });


        SharedPreferences sharedPreferences = this.getSharedPreferences("userData", MODE_PRIVATE);
        selectAccountbook = sharedPreferences.getInt("defaultAccountbook", 1);
        MyApplication app = MyApplication.getApplication();
        AccountBook accountbook = app.getAccountBooks().get(selectAccountbook);
        textAccountbook.setText(accountbook.getBookname());
        initAccountBookPopupWindow();
    }

    private void initAccountBookPopupWindow() {
        View contentView = LayoutInflater.from(MoreExportActivity.this).inflate(R.layout.popupwindow_accountbook, null);
        accountbookPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        ListView list = (ListView) contentView.findViewById(R.id.list);

        MyApplication app = MyApplication.getApplication();
        final List<AccountBook> accountbooks = new ArrayList<AccountBook>(app.getAccountBooks().values());
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < accountbooks.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item_text", accountbooks.get(i).getBookname());
            if(selectAccountbook == accountbooks.get(i).getId())
                map.put("item_selector", R.drawable.ic_selector_blue);
            else
                map.put("item_selector", null);
            data.add(map);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_list_accountbook,
                new String[]{"item_icon", "item_text","item_money","item_selector"}, new int[]{R.id.item_icon,
                R.id.item_text,R.id.item_money,R.id.item_selector});
        list.setAdapter(simpleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < data.size(); i++) {
                    if(position == i)
                        data.get(i).put("item_selector", R.drawable.ic_selector_blue);
                    else
                        data.get(i).put("item_selector", null);
                }
                simpleAdapter.notifyDataSetChanged();
                accountbookPopupWindow.dismiss();

                selectAccountbook = accountbooks.get(position).getId();
                SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("defaultAccountbook",selectAccountbook);
                editor.apply();
                textAccountbook.setText(accountbooks.get(position).getBookname());
            }
        });

        list.setDivider(null);//去除分割线

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountbookPopupWindow.dismiss();
            }
        });
    }

    private void showPopupWindow(PopupWindow popupWindow) {
        View rootview = LayoutInflater.from(MoreExportActivity.this).inflate(R.layout.activity_more_export, null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private void updateDatePickerText(){
        if(TimeUtils.getTime(start,TimeUtils.YEAR) != TimeUtils.getTime(end,TimeUtils.YEAR)) {
            textTime.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("yyyy年MM月dd日").format(end));
        }
        else{
            if(TimeUtils.getTime(start,TimeUtils.YEAR) == TimeUtils.getTime(new Date(),TimeUtils.YEAR)){
                if (TimeUtils.getTime(start, TimeUtils.MONTH) != TimeUtils.getTime(end, TimeUtils.MONTH)) {
                    textTime.setText(new SimpleDateFormat("MM月dd日").format(start) + "~" + new SimpleDateFormat("MM月dd日").format(end));
                }
                else{
                    if (TimeUtils.getTime(start, TimeUtils.DAY) != TimeUtils.getTime(end, TimeUtils.DAY)) {
                        textTime.setText(new SimpleDateFormat("MM月dd日").format(start) + "~" + new SimpleDateFormat("dd日").format(end));
                    }
                    else{
                        textTime.setText(new SimpleDateFormat("MM月dd日").format(start));
                    }
                }
            }
            else {
                if (TimeUtils.getTime(start, TimeUtils.MONTH) != TimeUtils.getTime(end, TimeUtils.MONTH)) {
                    textTime.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("MM月dd日").format(end));
                }
                else{
                    textTime.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("dd日").format(end));
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    start = (Date)intent.getSerializableExtra("start");
                    end = (Date)intent.getSerializableExtra("end");
                    updateDatePickerText();
                }
                break;
            case 2:
                if(resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                    textSdcard.setText(intent.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                }
                break;
            case 3:
                if(resultCode == RESULT_OK) {

                }
                break;
        }
    }
}
