package com.swjtu.huxin.accountmanagement.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.util.Date;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.adapter.OnDatePickerChangedListener;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

/**
 * Created by huxin on 2017/2/25.
 */

public class DatePickerView extends LinearLayout {

    private Context context;
    private Date date;
    private CustomNumberPicker[] time = new CustomNumberPicker[3];
    private OnDatePickerChangedListener onDatePickerChangedListener;


    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = View.inflate(context, R.layout.layout_date_picker, null);
        date = new Date();
        time[0] = (CustomNumberPicker)view.findViewById(R.id.year);
        time[1] = (CustomNumberPicker)view.findViewById(R.id.month);
        time[2] = (CustomNumberPicker)view.findViewById(R.id.day);
        time[0].setWrapSelectorWheel(false);//屏蔽循环滚动
        time[0].setNumberPickerDivider();
        time[1].setNumberPickerDivider();
        time[2].setNumberPickerDivider();
        setDatePickerDisplayedValues(time[0],1970,2100,"年");
        setDatePickerDisplayedValues(time[1],1,12,"月");
        setDatePickerDisplayedValues(time[2],1,TimeUtils.getMaxDay(date,0,0),"日");
        time[0].setValue(TimeUtils.getTime(date,TimeUtils.YEAR));
        time[1].setValue(TimeUtils.getTime(date,TimeUtils.MONTH));
        time[2].setValue(TimeUtils.getTime(date,TimeUtils.DAY));
        time[0].setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onDatePickerChangedListener.onValueChange(picker,oldVal,newVal);
                setDayPickerDisplayedValues(time[2],TimeUtils.getMaxDay(date,time[1].getValue() - TimeUtils.getTime(date,TimeUtils.MONTH),newVal - TimeUtils.getTime(date,TimeUtils.YEAR)));
            }
        });
        time[1].setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onDatePickerChangedListener.onValueChange(picker,oldVal,newVal);
                setDayPickerDisplayedValues(time[2],TimeUtils.getMaxDay(date,newVal - TimeUtils.getTime(date,TimeUtils.MONTH),time[0].getValue()-TimeUtils.getTime(date,TimeUtils.YEAR)));
            }
        });
        time[2].setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onDatePickerChangedListener.onValueChange(picker,oldVal,newVal);
            }
        });

        addView(view,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private void setDatePickerDisplayedValues(CustomNumberPicker picker, int min, int max, String str){
        picker.setMinValue(min);
        picker.setMaxValue(max);
        String[] display = new String[max - min + 1];
        for(int i = min; i <= max; i++) {
            display[i - min] = i+str;
        }
        picker.setDisplayedValues(display);
    }

    private void setDayPickerDisplayedValues(CustomNumberPicker picker, int max){
        picker.setDisplayedValues(null);
        String[] display = new String[max - 1 + 1];
        for(int i = 1; i <= max; i++) {
            display[i - 1] = i+"日";
        }
        picker.setMaxValue(max);
        picker.setDisplayedValues(display);
    }

    public void setValue(Date date){
        time[0].setValue(TimeUtils.getTime(date,TimeUtils.YEAR));
        time[1].setValue(TimeUtils.getTime(date,TimeUtils.MONTH));
        time[2].setValue(TimeUtils.getTime(date,TimeUtils.DAY));
    }

    public Date getValue() {
        return TimeUtils.getDate(time[0].getValue(),time[1].getValue(),time[2].getValue());
    }

    public void setOnDatePickerChangedListener(OnDatePickerChangedListener onDatePickerChangedListener) {
        this.onDatePickerChangedListener = onDatePickerChangedListener;
    }
}

