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
        View view = inflater.inflate(R.layout.fragment_tubiao_tab_chengyuan,container,false);

        left = (ImageView) view.findViewById(R.id.date_left);
        right = (ImageView) view.findViewById(R.id.date_right);
        datePicker = (TextView) view.findViewById(R.id.date_picker);

        start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DateRangePickerActivity.class);
                intent.putExtra("start", start);
                intent.putExtra("end",end);
                startActivityForResult(intent, 1);
            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK) {

                }
        }
    }
}
