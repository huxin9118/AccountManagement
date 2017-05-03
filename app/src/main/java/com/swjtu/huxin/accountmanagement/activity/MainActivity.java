package com.swjtu.huxin.accountmanagement.activity;


import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.fragment.MoreFragment;
import com.swjtu.huxin.accountmanagement.fragment.DetailFragment;
import com.swjtu.huxin.accountmanagement.fragment.ChartFragment;
import com.swjtu.huxin.accountmanagement.fragment.AccountFragment;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MainActivity extends BaseAppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
//    private ArrayList<Fragment> fragments;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initAnim();
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initAnim() {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.explode);
        //退出时使用
        getWindow().setExitTransition(explode);
        //第一次进入时使用
        getWindow().setEnterTransition(explode);
        //再次进入时使用
        getWindow().setReenterTransition(explode);
    }

    private void initView(){
        /**
         *添加标签的消息数量
        mHomeNumberBadgeItem = new BadgeItem()
                .setBorderWidth(2)
                .setBackgroundColor(Color.RED)
                .setText("99+")
                .setHideOnSelect(false); //控制便签被点击时 消失|不消失
        */

        Intent intent = getIntent();
        setDefaultFragment(intent.getIntExtra("defaultPosition",0));

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);//设置模式
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);//设置背景色样式
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_mingxi, "明细").setActiveColorResource(R.color.orange).setInActiveColorResource(R.color.gray))
                .addItem(new BottomNavigationItem(R.drawable.ic_zhanghu, "账户").setActiveColorResource(R.color.teal).setInActiveColorResource(R.color.gray))
                .addItem(new BottomNavigationItem(R.drawable.ic_tubiao, "图表").setActiveColorResource(R.color.customBlue).setInActiveColorResource(R.color.gray))
                .addItem(new BottomNavigationItem(R.drawable.ic_gengduo, "更多").setActiveColorResource(R.color.brown).setInActiveColorResource(R.color.gray))
                .setFirstSelectedPosition(intent.getIntExtra("defaultPosition",0))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);

        ImageView background = (ImageView)findViewById(R.id.background);
        int[] attrsArray = { R.attr.mainBackgrount };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int imgResID = typedArray.getResourceId(0,-1);
        typedArray.recycle();
        Glide.with(this).load(imgResID).placeholder(R.drawable.ic_loading1).dontAnimate()
                .bitmapTransform(new BlurTransformation(this, 8)).into(background);
    }

    public void onBackPressed() {
        if((System.currentTimeMillis()-exitTime) > 2000){
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finishAfterTransition();
            System.exit(0);
        }
    }

//    private ArrayList<Fragment> getFragments() {
//        ArrayList<Fragment> fragments = new ArrayList<>();
//        fragments.add(DetailFragment.newInstance("明细"));
//        fragments.add(AccountFragment.newInstance("账户"));
//        fragments.add(ChartFragment.newInstance("图表"));
//        fragments.add(MoreFragment.newInstance("更多"));
//        return fragments;
//    }
//
    private void setDefaultFragment(int defaultPosition) {
//        fragments = getFragments();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (defaultPosition){
            case 0:transaction.replace(R.id.layFrame, DetailFragment.newInstance("明细"));break;
            case 1:transaction.replace(R.id.layFrame, AccountFragment.newInstance("账户"));break;
            case 2:transaction.replace(R.id.layFrame, ChartFragment.newInstance("图表"));break;
            case 3:transaction.replace(R.id.layFrame, MoreFragment.newInstance("更多"));break;
        }
        transaction.commit();
    }

    private Fragment getFragment(int position) {
        switch (position){
            case 0:return DetailFragment.newInstance("明细");
            case 1:return AccountFragment.newInstance("账户");
            case 2:return ChartFragment.newInstance("图表");
            case 3:return MoreFragment.newInstance("更多");
            default:return null;
        }
    }

    @Override
    //未选中 -> 选中
    public void onTabSelected(int position) {
//        if (fragments != null) {
//            if (position < fragments.size()) {
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                Fragment fragment = fragments.get(position);
//                if (fragment.isAdded()) {
//                    ft.show(fragment);
//                } else {
//                    ft.add(R.id.layFrame, fragment);
//                }
//                ft.commit();
//            }
//        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        Fragment fragment = getFragment(position);
        ft.replace(R.id.layFrame, fragment);
        ft.commit();
    }

    @Override
    //选中 -> 未选中
    public void onTabUnselected(int position) {
//        if (fragments != null) {
//            if (position < fragments.size()) {
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                Fragment fragment = fragments.get(position);
//                ft.hide(fragment);
//                ft.commit();
//            }
//        }
    }

    @Override
    //选中 -> 选中
    public void onTabReselected(int position) {

    }
}
