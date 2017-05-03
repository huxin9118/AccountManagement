package com.swjtu.huxin.accountmanagement.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by huxin on 2017/4/4.
 */

public class BaseAppCompatActivity extends AppCompatActivity {
    protected String TAG = this.getClass().getSimpleName();
    protected Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int myTheme = MyApplication.getApplication().getMyTheme();
        setTheme(myTheme);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * 显示Toast，解决重复弹出问题
     */
    public void showToast(String text , int time) {
        if(mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), text, time);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 隐藏Toast
     */
    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }

    public void showLog(String msg){
        Log.i(TAG, msg);
    }

}
