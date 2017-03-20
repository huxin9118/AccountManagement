package com.swjtu.huxin.accountmanagement.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.util.Date;

/**
 * Created by huxin on 2017/3/11.
 */

public class ChartTabContrastFragment extends Fragment
{
    private String mArgument;
    public static final String ARGUMENT = "argument";

    private ImageView left;
    private ImageView right;
    private TextView datePicker;
    private Date start;
    private Date end;

    public static ChartTabContrastFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        ChartTabContrastFragment contentFragment = new ChartTabContrastFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart_tab_contrast,container,false);

        left = (ImageView) view.findViewById(R.id.date_left);
        right = (ImageView) view.findViewById(R.id.date_right);
        datePicker = (TextView) view.findViewById(R.id.date_picker);

        start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));

//        datePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), DateRangePickerActivity.class);
//                intent.putExtra("start", start);
//                intent.putExtra("end",end);
//                startActivityForResult(intent, 1);
//            }
//        });

        return view;
    }
}
