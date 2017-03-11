package com.swjtu.huxin.accountmanagement.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.BudgetSettingActivity;

import java.math.BigDecimal;

/**
 * Created by huxin on 2017/3/10.
 */

public class BudgetFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";

    private TextView money;
    private TextView end;
    private NumberProgressBar progressBar;
    private BigDecimal totalMoney;
    private BigDecimal remainingMoney;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
            totalMoney = (BigDecimal) bundle.getSerializable("totalMoney");
            remainingMoney = (BigDecimal) bundle.getSerializable("remainingMoney");
        }

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if("positive".equals(mArgument))
            view = inflater.inflate(R.layout.fragment_budget_positive,container,false);
        else
            view = inflater.inflate(R.layout.fragment_budget_negative,container,false);

        money = (TextView)view.findViewById(R.id.money);
        end = (TextView)view.findViewById(R.id.progress_end);
        progressBar = (NumberProgressBar)view.findViewById(R.id.progress);

        money.setText(remainingMoney.toString());
        end.setText(totalMoney.toString());
        progressBar.setMax((int)(totalMoney.doubleValue()*100));

        if("positive".equals(mArgument))
            progressBar.setProgress((int)(remainingMoney.doubleValue()*100));
        else
            progressBar.setProgress((int)(remainingMoney.negate().doubleValue()*100));

        return view;
    }

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static BudgetFragment newInstance(String argument,BigDecimal totalMoney,BigDecimal remainingMoney ) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        bundle.putSerializable("totalMoney",totalMoney);
        bundle.putSerializable("remainingMoney",remainingMoney);
        BudgetFragment contentFragment = new BudgetFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

}
