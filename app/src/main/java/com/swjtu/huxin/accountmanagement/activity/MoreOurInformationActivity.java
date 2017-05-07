package com.swjtu.huxin.accountmanagement.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import jp.wasabeef.glide.transformations.BlurTransformation;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by huxin on 2017/3/11.
 */

public class MoreOurInformationActivity extends BaseAppCompatActivity {

    private LinearLayout btnBack;
    private RelativeLayout btnWeb;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_our_information);
        initBackground();
        initView();
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
        btnBack = (LinearLayout) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                finish();
            }
        });

        btnWeb = (RelativeLayout) findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoreOurInformationActivity.this, MoreOpenSourceActivity.class);
                startActivity(intent);
            }
        });

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);

        int[] attrsArray = { R.attr.more_half_transparent_contrast };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        img1.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        img1.invalidate();
        img2.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        img2.invalidate();
        img3.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        img3.invalidate();
        img4.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        img4.invalidate();
    }
}

