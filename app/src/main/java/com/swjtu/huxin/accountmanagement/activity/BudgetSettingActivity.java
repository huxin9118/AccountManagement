package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by huxin on 2017/3/11.
 */

public class BudgetSettingActivity extends BaseAppCompatActivity {

    private LinearLayout btnBack;
    private RelativeLayout btnEnter;
    private TextView textEnter;
    private TextView btnMoney;
    private SwitchCompat switchCompat;
    private String totalMoney;

    private Animation enterAnim;
    private Animation exitAnim;
    private LinearLayout keyboard;
    private TextView keyNum;
    private TextView keySymbol;
    private NumKeyboardView numKeyboardView;
    private ArrayList<String> valueList;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_setting);
        initAnim();
        initView();
    }

    private void initAnim() {
        enterAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        exitAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
    }

    private void initView() {

        btnBack = (LinearLayout) findViewById(R.id.back);
        btnEnter = (RelativeLayout) findViewById(R.id.enter);
        textEnter = (TextView) findViewById(R.id.enter_text);
        btnMoney = (TextView) findViewById(R.id.money);
        switchCompat = (SwitchCompat) findViewById(R.id.switchCompat);
        switchCompat.setSwitchMinWidth((int)(switchCompat.getSwitchMinWidth()*1.5));
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("isBudget",true));
        Intent intent = getIntent();
        totalMoney = intent.getStringExtra("totalMoney");
        btnMoney.setText(totalMoney);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                Intent intent = new Intent();
                intent.putExtra("isBudget",switchCompat.isChecked());
                intent.putExtra("totalMoney", btnMoney.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        keyNum = (TextView) findViewById(R.id.key_num);
        keySymbol = (TextView) findViewById(R.id.key_symbol);
        keySymbol.setVisibility(View.GONE);
        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        numKeyboardView = (NumKeyboardView) findViewById(R.id.numKeyboardView);
        keyboard.setVisibility(View.GONE);

        valueList = numKeyboardView.getValueList();
        gridView = numKeyboardView.getGridView();
        gridView.setOnItemClickListener(onItemClickListener);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyboard.getVisibility() !=  View.VISIBLE){//是否弹出键盘
                    keyNum.setText(totalMoney);
                    numKeyboardView.setFocusable(true);
                    numKeyboardView.setFocusableInTouchMode(true);
                    keyboard.startAnimation(enterAnim);
                    keyboard.setVisibility(View.VISIBLE);
                }
                else{
                    keyboard.startAnimation(exitAnim);
                    keyboard.setVisibility(View.GONE);
                }
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isBudget",isChecked);
                editor.apply();
                if(isChecked){
                    btnEnter.setEnabled(true);
                    btnEnter.setAlpha(1f);
                    textEnter.setAlpha(1f);
                }else {
                    btnEnter.setEnabled(false);
                    btnEnter.setAlpha(0.5f);
                    textEnter.setAlpha(0.5f);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        private boolean isInteger = true;//是否输入整数
        private int numDecimal = 0;//已输入的小数位数
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String amount;
            if(numKeyboardView.isZero()) {
                amount = "0.00";
                numKeyboardView.setZero(false);
            }
            else {
                amount = keyNum.getText().toString().trim();
            }
            if (position < 14 && position != 3 && position != 7 && position != 11 && position != 12) {    //点击0~9按钮
                if(isInteger) {
                    if(amount.charAt(0) == '0'){
                        amount = valueList.get(position) + amount.substring(1);
                    }
                    else{
                        amount = amount.substring(0,amount.length()-3) + valueList.get(position) + amount.substring(amount.length()-3);
                    }
                }
                else{
                    if(numDecimal == 0){
                        amount = amount.substring(0,amount.length()-2) + valueList.get(position) + "0";
                        numDecimal++;
                    }
                    else if(numDecimal == 1){
                        amount = amount.substring(0,amount.length()-1) + valueList.get(position);
                        numDecimal++;
                    }
                }
            }
            else {
                if (position == 7) {      //点击+
                    if(numKeyboardView.isAddSymbol()){
                        keySymbol.setVisibility(View.GONE);
                        numKeyboardView.changeBtnOK();
                        numKeyboardView.setAddSymbol(false);
                        numKeyboardView.setZero(false);
                    }
                    else {
                        keySymbol.setText("+");
                        keySymbol.setVisibility(View.VISIBLE);
                        numKeyboardView.changeBtnEqual();
                        numKeyboardView.setAddSymbol(true);
                        numKeyboardView.setZero(true);
                        numKeyboardView.setOldAmount(amount);
                    }
                    return;
                }
                if (position == 11) {      //点击—
                    if(numKeyboardView.isSubtractSymbol()){
                        keySymbol.setVisibility(View.GONE);
                        numKeyboardView.changeBtnOK();
                        numKeyboardView.setSubtractSymbol(false);
                        numKeyboardView.setZero(false);
                    }
                    else {
                        keySymbol.setText("-");
                        keySymbol.setVisibility(View.VISIBLE);
                        numKeyboardView.changeBtnEqual();
                        numKeyboardView.setSubtractSymbol(true);
                        numKeyboardView.setZero(true);
                        numKeyboardView.setOldAmount(amount);
                    }
                    return;
                }
                if (position == 14) {      //点击小数点
                    isInteger = isInteger==true?false:true;
                }
                if (position == 3) {      //点击退格键
                    if(isInteger){
                        if(amount.charAt(0) != '0'){
                            amount = amount.substring(0, amount.length() - 4) + amount.substring(amount.length() - 3);
                        }
                        if(amount.length() == 3){
                            amount = "0"+amount;
                        }
                        if(amount.length() == 4 && amount.charAt(0) == '0' && numDecimal != 0){
                            isInteger = false;
                        }
                    }
                    else{
                        if (numDecimal == 0) {
                            isInteger = true;
                        }
                        else if(numDecimal == 1){
                            amount = amount.substring(0,amount.length()-2) + "00";
                            numDecimal--;
                        }
                        else if(numDecimal == 2){
                            amount = amount.substring(0,amount.length()-1) + "0";
                            numDecimal--;
                        }
                    }
                }
                if (position == 12) {      //点击C
                    amount = "0.00";
                }
                if (position == 15) {      //点击确定
                    if(numKeyboardView.isAddSymbol()){
                        amount = (new BigDecimal(numKeyboardView.getOldAmount()).add(new BigDecimal(amount))).toString();
                        keySymbol.setVisibility(View.GONE);
                        numKeyboardView.changeBtnOK();
                        numKeyboardView.setAddSymbol(false);
                    }
                    else if(numKeyboardView.isSubtractSymbol()){
                        amount = (new BigDecimal(numKeyboardView.getOldAmount()).subtract(new BigDecimal(amount))).toString();
                        keySymbol.setVisibility(View.GONE);
                        numKeyboardView.changeBtnOK();
                        numKeyboardView.setSubtractSymbol(false);
                    }
                    else {
                        if (!"0.00".equals(keyNum.getText().toString())) {
                            btnMoney.setText(keyNum.getText());
                            keyboard.startAnimation(exitAnim);
                            keyboard.setVisibility(View.GONE);

                            SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("totalMoney", keyNum.getText().toString());
                            editor.apply();
                        } else {//输入金额为0
                            showToast("预算金额不能为0", Toast.LENGTH_SHORT);
                        }
                    }
                }
            }
            keyNum.setText(amount);
        }
    };


    public void onBackPressed() {
        cancelToast();
        if(keyboard.getVisibility() == View.VISIBLE) {
            keyboard.startAnimation(exitAnim);
            keyboard.setVisibility(View.GONE);
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("isBudget",switchCompat.isChecked());
            intent.putExtra("totalMoney", btnMoney.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
