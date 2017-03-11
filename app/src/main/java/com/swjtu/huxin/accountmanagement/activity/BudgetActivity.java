package com.swjtu.huxin.accountmanagement.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.fragment.BudgetFragment;

import java.math.BigDecimal;

/**
 * Created by huxin on 2017/3/9.
 */

public class BudgetActivity extends AppCompatActivity {
    private BigDecimal totalMoney;
    private BigDecimal remainingMoney;

    private LinearLayout back;
    private ImageView setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        initView();
    }

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        setting = (ImageView) findViewById(R.id.setting);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("totalMoney", totalMoney);
                intent.putExtra("remainingMoney",remainingMoney);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetActivity.this, BudgetSettingActivity.class);
                intent.putExtra("totalMoney", totalMoney.toString());
                startActivityForResult(intent, 1);
            }
        });

        Intent intent = getIntent();
        totalMoney = (BigDecimal)intent.getSerializableExtra("totalMoney");
        remainingMoney = (BigDecimal)intent.getSerializableExtra("remainingMoney");
        if(remainingMoney.doubleValue() >= 0) setDefaultFragment("positive");
        else setDefaultFragment("negative");
    }

    private void setDefaultFragment(String index) {
        BudgetFragment fragment = BudgetFragment.newInstance(index,totalMoney,remainingMoney);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    remainingMoney = remainingMoney.add(new BigDecimal(intent.getStringExtra("totalMoney")).subtract(totalMoney));
                    totalMoney = new BigDecimal(intent.getStringExtra("totalMoney"));
                    if(remainingMoney.doubleValue() >= 0) setDefaultFragment("positive");
                    else setDefaultFragment("negative");
//                    end.setText(totalMoney.toString());
//                    progressBar.setMax((int)(totalMoney.doubleValue()*100));
                }
        }
    }
}

