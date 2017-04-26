package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.MoreExportActivity;
import com.swjtu.huxin.accountmanagement.activity.MoreSummaryActivity;

public class MoreFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";
    private RelativeLayout btnSummary;
    private RelativeLayout btnExport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null)
            mArgument = bundle.getString(ARGUMENT);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more,container,false);
        btnSummary = (RelativeLayout) view.findViewById(R.id.btnSummary);
        btnSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreSummaryActivity.class);
                startActivity(intent);
            }
        });

        btnExport = (RelativeLayout) view.findViewById(R.id.btnExport);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreExportActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static MoreFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        MoreFragment contentFragment = new MoreFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }
}
