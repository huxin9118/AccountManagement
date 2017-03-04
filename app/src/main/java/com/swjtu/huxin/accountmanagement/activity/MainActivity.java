package com.swjtu.huxin.accountmanagement.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.fragment.GengDuoFragment;
import com.swjtu.huxin.accountmanagement.fragment.MingXiFragment;
import com.swjtu.huxin.accountmanagement.fragment.TuBiaoFragment;
import com.swjtu.huxin.accountmanagement.fragment.ZhangHuFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{
    private ArrayList<Fragment> fragments;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAnim();
        setContentView(R.layout.activity_main);
        initView();
        setDefaultFragment();
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

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);//设置模式
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);//设置背景色样式
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_mingxi, "明细").setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.drawable.ic_zhanghu, "账户").setActiveColorResource(R.color.teal))
                .addItem(new BottomNavigationItem(R.drawable.ic_tubiao, "图表").setActiveColorResource(R.color.blue))
                .addItem(new BottomNavigationItem(R.drawable.ic_gengduo, "更多").setActiveColorResource(R.color.brown))
                .setFirstSelectedPosition(0)
                .initialise();

        bottomNavigationBar.setTabSelectedListener(this);
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

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(MingXiFragment.newInstance("明细"));
        fragments.add(ZhangHuFragment.newInstance("账户"));
        fragments.add(TuBiaoFragment.newInstance("图表"));
        fragments.add(GengDuoFragment.newInstance("更多"));
        return fragments;
    }

    private void setDefaultFragment() {
        fragments = getFragments();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.layFrame, fragments.get(0));
        transaction.commit();
    }

    @Override
    //未选中 -> 选中
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.show(fragment);
                } else {
                    ft.add(R.id.layFrame, fragment);
                }
                ft.commit();
            }
        }

    }

    @Override
    //选中 -> 未选中
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.hide(fragment);
                ft.commit();
            }
        }
    }

    @Override
    //选中 -> 选中
    public void onTabReselected(int position) {

    }
}
