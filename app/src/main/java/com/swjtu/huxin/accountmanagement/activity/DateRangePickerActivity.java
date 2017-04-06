package com.swjtu.huxin.accountmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.OnDatePickerChangedListener;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;
import com.swjtu.huxin.accountmanagement.view.DatePickerView;

import java.util.Date;

/**
 * Created by huxin on 2017/3/12.
 */

public class DateRangePickerActivity extends BaseAppCompatActivity {

    private LinearLayout back;
    private TextView backText;
    private TextView ok;

    private TextView week;
    private TextView month;
    private TextView year;

    private DatePickerView startDatePicker;
    private DatePickerView endDatePicker;

    private Date start;
    private Date end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_range_picker);
        initView();
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        backText = (TextView) findViewById(R.id.back_text);
        ok = (TextView) findViewById(R.id.ok);
        week = (TextView) findViewById(R.id.week);
        month = (TextView) findViewById(R.id.month);
        year = (TextView) findViewById(R.id.year);

        startDatePicker = (DatePickerView) findViewById(R.id.startDatePicker);
        endDatePicker = (DatePickerView) findViewById(R.id.endDatePicker);
        startDatePicker.setOnDatePickerChangedListener(new OnDatePickerChangedListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                week.setTextColor(getResources().getColor(R.color.darkgray));
                month.setTextColor(getResources().getColor(R.color.darkgray));
                year.setTextColor(getResources().getColor(R.color.darkgray));
            }
        });
        endDatePicker.setOnDatePickerChangedListener(new OnDatePickerChangedListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                week.setTextColor(getResources().getColor(R.color.darkgray));
                month.setTextColor(getResources().getColor(R.color.darkgray));
                year.setTextColor(getResources().getColor(R.color.darkgray));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = startDatePicker.getValue();
                end = endDatePicker.getValue();
                if(TimeUtils.getTimeDistance(start,end) < 0) {
                    showToast("结束时间要大于开始时间哦~", Toast.LENGTH_SHORT);
                }
                else {
                    start = new Date(TimeUtils.getDateFirstMilliSeconds(startDatePicker.getValue()));
                    end = new Date(TimeUtils.getDateLastMilliSeconds(endDatePicker.getValue()));
                    Intent intent = new Intent();
                    intent.putExtra("start", start);
                    intent.putExtra("end", end);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week.setTextColor(getResources().getColor(R.color.customBlue));
                month.setTextColor(getResources().getColor(R.color.darkgray));
                year.setTextColor(getResources().getColor(R.color.darkgray));
                start = new Date(TimeUtils.getWeekFirstMilliSeconds());
                end = new Date(TimeUtils.getWeekLastMilliSeconds());
                startDatePicker.setValue(start);
                endDatePicker.setValue(end);
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week.setTextColor(getResources().getColor(R.color.darkgray));
                month.setTextColor(getResources().getColor(R.color.customBlue));
                year.setTextColor(getResources().getColor(R.color.darkgray));
                start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
                end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
                startDatePicker.setValue(start);
                endDatePicker.setValue(end);
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week.setTextColor(getResources().getColor(R.color.darkgray));
                month.setTextColor(getResources().getColor(R.color.darkgray));
                year.setTextColor(getResources().getColor(R.color.customBlue));
                start = TimeUtils.getYearFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.YEAR));
                end = TimeUtils.getYearLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.YEAR));
                startDatePicker.setValue(start);
                endDatePicker.setValue(end);
            }
        });

        Intent intent = getIntent();
        start = (Date)intent.getSerializableExtra("start");
        end = (Date)intent.getSerializableExtra("end");
        backText.setText(intent.getStringExtra("back"));
        startDatePicker.setValue(start);
        endDatePicker.setValue(end);
    }

}
