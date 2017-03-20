package com.swjtu.huxin.accountmanagement.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swjtu.huxin.accountmanagement.R;

/**
 * Created by huxin on 2017/3/11.
 */

public class ChartTabTrendFragment extends Fragment
{
    private String mArgument;
    public static final String ARGUMENT = "argument";

    public static ChartTabTrendFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        ChartTabTrendFragment contentFragment = new ChartTabTrendFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart_tab_trend,container,false);
        return view;
    }
}
