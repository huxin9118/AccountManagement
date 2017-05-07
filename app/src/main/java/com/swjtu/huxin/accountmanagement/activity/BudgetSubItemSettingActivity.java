package com.swjtu.huxin.accountmanagement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.base.OnNumKeyboardItemClickListener;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huxin on 2017/3/11.
 */

public class BudgetSubItemSettingActivity extends BaseAppCompatActivity {

    private TextView title;
    private LinearLayout btnBack;
    private RelativeLayout btnEnter;
    private TextView textName;
    private TextView textMoney;
    private String recordname;
    private String totalMoney;

    private Animation enterAnim;
    private Animation exitAnim;
    private LinearLayout keyboard;
    private TextView keyNum;
    private TextView keySymbol;
    private NumKeyboardView numKeyboardView;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_sub_item_setting);
        initAnim();
        initBackground();
        initView();
    }

    private void initAnim() {
        enterAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        exitAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);
    }

    void initBackground(){
        ImageView background = (ImageView)findViewById(R.id.background);
        int[] attrsArray1 = { R.attr.mainBackgrount };
        TypedArray typedArray1 = obtainStyledAttributes(attrsArray1);
        int imgResID = typedArray1.getResourceId(0,-1);
        typedArray1.recycle();
        int[] attrsArray2 = { R.attr.theme_alpha };
        TypedArray typedArray2 = obtainStyledAttributes(attrsArray2);
        int alpha = typedArray2.getInteger(0,8);
        typedArray2.recycle();
        Glide.with(this).load(imgResID).dontAnimate().bitmapTransform(new BlurTransformation(this, alpha)).into(background);
    }

    private void initView() {

        title =  (TextView) findViewById(R.id.title);
        btnBack = (LinearLayout) findViewById(R.id.back);
        btnEnter = (RelativeLayout) findViewById(R.id.enter);
        textName = (TextView) findViewById(R.id.name);
        textMoney = (TextView) findViewById(R.id.money);

        Intent intent = getIntent();
        recordname = intent.getStringExtra("recordname");
        totalMoney = intent.getStringExtra("totalMoney");
        title.setText(recordname+"预算");
        textName.setText(recordname);
        textMoney.setText(totalMoney);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                Intent intent = new Intent();
                intent.putExtra("recordname", textName.getText().toString());
                intent.putExtra("totalMoney", textMoney.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        keyNum = (TextView) findViewById(R.id.key_num);
        keySymbol = (TextView) findViewById(R.id.key_symbol);
        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        numKeyboardView = (NumKeyboardView) findViewById(R.id.numKeyboardView);
        keyboard.setVisibility(View.GONE);

        gridView = numKeyboardView.getGridView();
        gridView.setOnItemClickListener(new OnNumKeyboardItemClickListener(numKeyboardView, keyNum, keySymbol, new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if (!"0.00".equals(keyNum.getText().toString())) {
                    totalMoney = keyNum.getText().toString();
                    textMoney.setText(totalMoney);
                    keyboard.startAnimation(exitAnim);
                    keyboard.setVisibility(View.GONE);

                    SharedPreferences sharedPreferences = getSharedPreferences("budgetData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("totalMoney_"+ recordname, totalMoney);
                    editor.apply();
                } else {//输入金额为0
                    showToast("预算金额必须大于0", Toast.LENGTH_SHORT);
                }
            }
        }));
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
                    keySymbol.setVisibility(View.GONE);
                    numKeyboardView.changeBtnOK();
                    numKeyboardView.setAddSymbol(false);
                    numKeyboardView.setSubtractSymbol(false);
                    numKeyboardView.setZero(false);

                    keyboard.startAnimation(exitAnim);
                    keyboard.setVisibility(View.GONE);
                }
            }
        });

    }

    public void onBackPressed() {
        cancelToast();
        if(keyboard.getVisibility() == View.VISIBLE) {
            keyboard.startAnimation(exitAnim);
            keyboard.setVisibility(View.GONE);
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("recordname", textName.getText().toString());
            intent.putExtra("totalMoney", textMoney.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
