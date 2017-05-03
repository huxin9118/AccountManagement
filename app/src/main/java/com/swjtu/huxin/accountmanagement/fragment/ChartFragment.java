package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;

public class ChartFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TextView[] btnTab = new TextView[4];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null)
            mArgument = bundle.getString(ARGUMENT);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart,container,false);

        btnTab[0] = (TextView)view.findViewById(R.id.textFenlei);
        btnTab[1] = (TextView)view.findViewById(R.id.textQushi);
        btnTab[2] = (TextView)view.findViewById(R.id.textDuibi);
        btnTab[3] = (TextView)view.findViewById(R.id.textChengyuan);

        btnTab[0].setOnClickListener(mTabClickListener);
        btnTab[1].setOnClickListener(mTabClickListener);
        btnTab[2].setOnClickListener(mTabClickListener);
        btnTab[3].setOnClickListener(mTabClickListener);

        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);

        btnTab[0].performClick();
        return view;
    }

    private void updateTab(int position){
        for(int i = 0; i < 4; i++){
            if(i == position)
                btnTab[i].setTextColor(getResources().getColor(R.color.customBlue));
            else {
                btnTab[i].setTextColor(getResources().getColor(R.color.darkgray));
            }
        }
    }

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btnTab[0]) {
                updateTab(0);
                mViewPager.setCurrentItem(0);
            }
            else if (v == btnTab[1]) {
                updateTab(1);
                mViewPager.setCurrentItem(1);
            }
            else if (v == btnTab[2]) {
                updateTab(2);
                mViewPager.setCurrentItem(2);
            }
            else if (v == btnTab[3]) {
                updateTab(3);
                mViewPager.setCurrentItem(3);
            }
        }
    };

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            btnTab[arg0].performClick();
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}
        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0:
                    return ChartTabSortFragment.newInstance("");
                case 1:
                    return ChartTabTrendFragment.newInstance("");
                case 2:
                    return ChartTabContrastFragment.newInstance("");
                default:
                    return ChartTabMemberFragment.newInstance("");
            }
        }
        @Override
        public int getCount()
        {
            return 4;
        }
    }

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static ChartFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        ChartFragment contentFragment = new ChartFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }
}
