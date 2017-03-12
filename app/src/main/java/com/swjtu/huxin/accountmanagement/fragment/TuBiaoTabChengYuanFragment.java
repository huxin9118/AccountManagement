package com.swjtu.huxin.accountmanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.DateRangePickerActivity;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by huxin on 2017/3/11.
 */

public class TuBiaoTabChengYuanFragment extends Fragment
{
    private String mArgument;
    public static final String ARGUMENT = "argument";

    private ImageView left;
    private ImageView right;
    private TextView datePicker;
    private Date start;
    private Date end;

    public static TuBiaoTabChengYuanFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        TuBiaoTabChengYuanFragment contentFragment = new TuBiaoTabChengYuanFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tubiao_tab_fenlei,container,false);

        left = (ImageView) view.findViewById(R.id.date_left);
        right = (ImageView) view.findViewById(R.id.date_right);
        datePicker = (TextView) view.findViewById(R.id.date_picker);

        start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        updateDatePickerText();

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DateRangePickerActivity.class);
                intent.putExtra("back", "成员");
                intent.putExtra("start", start);
                intent.putExtra("end",end);
                startActivityForResult(intent, 1);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,-1,0);
                end = TimeUtils.getIndexDate(end,0,-1,0);
                end = TimeUtils.getMaxDayDate(end);
                updateDatePickerText();
                updateDateRangeChange();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,1,0);
                end = TimeUtils.getIndexDate(end,0,1,0);
                end = TimeUtils.getMaxDayDate(end);
                updateDatePickerText();
                updateDateRangeChange();
            }
        });



        return view;
    }

    private void updateDateRangePickerReturn() {
        if (TimeUtils.getTime(start, TimeUtils.YEAR) == TimeUtils.getTime(end, TimeUtils.YEAR)
                && TimeUtils.getTime(start, TimeUtils.MONTH) == TimeUtils.getTime(end, TimeUtils.MONTH)
                && TimeUtils.getTime(start, TimeUtils.DAY) == 1
                && TimeUtils.getTime(end, TimeUtils.DAY) == TimeUtils.getMaxDay(end, 0, 0)) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
        }
    }
    private void updateDateRangeChange(){
        if(TimeUtils.getTime(start,TimeUtils.YEAR) == 1970 && TimeUtils.getTime(start,TimeUtils.MONTH) == 1){
            left.setVisibility(View.INVISIBLE);
        }
        if(TimeUtils.getTime(start,TimeUtils.YEAR) == 2100 && TimeUtils.getTime(start,TimeUtils.MONTH) == 12){
            right.setVisibility(View.INVISIBLE);
        }
    }



    private void updateDatePickerText(){
        if(TimeUtils.getTime(start,TimeUtils.YEAR) != TimeUtils.getTime(end,TimeUtils.YEAR)) {
            datePicker.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("yyyy年MM月dd日").format(end));
        }
        else{
            if(TimeUtils.getTime(start,TimeUtils.YEAR) == TimeUtils.getTime(new Date(),TimeUtils.YEAR)){
                if (TimeUtils.getTime(start, TimeUtils.MONTH) != TimeUtils.getTime(end, TimeUtils.MONTH)) {
                    datePicker.setText(new SimpleDateFormat("MM月dd日").format(start) + "~" + new SimpleDateFormat("MM月dd日").format(end));
                }
                else{
                    if (TimeUtils.getTime(start, TimeUtils.DAY) != TimeUtils.getTime(end, TimeUtils.DAY)) {
                        datePicker.setText(new SimpleDateFormat("MM月dd日").format(start) + "~" + new SimpleDateFormat("dd日").format(end));
                    }
                    else{
                        datePicker.setText(new SimpleDateFormat("MM月dd日").format(start));
                    }
                }
            }
            else {
                if (TimeUtils.getTime(start, TimeUtils.MONTH) != TimeUtils.getTime(end, TimeUtils.MONTH)) {
                    datePicker.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("MM月dd日").format(end));
                }
                else{
                    datePicker.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("dd日").format(end));
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK) {
                    start = (Date)intent.getSerializableExtra("start");
                    end = (Date)intent.getSerializableExtra("end");
                    updateDatePickerText();
                    updateDateRangePickerReturn();
                }
        }
    }
}
