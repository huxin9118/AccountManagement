package com.swjtu.huxin.accountmanagement.activity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import jp.wasabeef.glide.transformations.BlurTransformation;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by huxin on 2017/3/11.
 */

public class MoreSummaryActivity extends BaseAppCompatActivity {

    private LinearLayout btnBack;
    private VerticalViewPager mViewPager;
    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_summary);
        initBackground();
        initView();
    }
    void initBackground(){
        ImageView background = (ImageView)findViewById(R.id.background);
        Glide.with(this).load(R.drawable.ic_summary_back).dontAnimate().into(background);
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

        mViewPager = (VerticalViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            return MoreSummaryActivity.MyFragment.newInstance(position);
        }
        @Override
        public int getCount()
        {
            return 5;
        }
    }

    public static class MyFragment extends Fragment
    {
        private int mArgument;
        public static final String ARGUMENT = "argument";
        private View view;
        private TextView summary0_line;
        private TextView summary0_title;
        private TextView summary0_text1;
        private TextView summary0_text2;
        private TextView summary0_text3;
        private TextView summary0_text4;
        private TextView summary0_text5;
        private TextView summary1_time;
        private TextView summary1_money;
        private TextView summary1_sumMoney;
        private TextView summary2_time;
        private TextView summary2_money;
        private TextView summary2_sumMoney;
        private TextView summary3_time;
        private TextView summary3_money;
        private TextView summary3_sumMoney;
        private TextView summary4_name;
        private TextView summary4_sumMoney;
        private GifImageView btnShare;
        /**
         * 传入需要的参数，设置给arguments
         *
         * @param argument
         * @return
         */
        public static MyFragment newInstance(int argument) {
            Bundle bundle = new Bundle();
            bundle.putInt(ARGUMENT, argument);
            MyFragment contentFragment = new MyFragment();
            contentFragment.setArguments(bundle);
            return contentFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
            Bundle bundle = getArguments();
            if (bundle != null)
                mArgument = bundle.getInt(ARGUMENT);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            switch (mArgument) {
                case 0:
                    view = inflater.inflate(R.layout.fragment_summary_0, container, false);
                    initView0(view);
                    initAnim0();
                    break;
                case 1:
                    view = inflater.inflate(R.layout.fragment_summary_1, container, false);
                    initView1(view);
                    break;
                case 2:
                    view = inflater.inflate(R.layout.fragment_summary_2, container, false);
                    initView2(view);
                    break;
                case 3:
                    view = inflater.inflate(R.layout.fragment_summary_3, container, false);
                    initView3(view);
                    break;
                case 4:
                    view = inflater.inflate(R.layout.fragment_summary_4, container, false);
                    initView4(view);
                    break;
            }
            return view;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if(view != null && isVisibleToUser){
                switch (mArgument) {
                    case 0:initAnim0();break;
                    case 1:initAnim1();break;
                    case 2:initAnim2();break;
                    case 3:initAnim3();break;
                    case 4:initAnim4();break;
                }
            }
        }

        private void initView0(View view) {
            summary0_line = (TextView) view.findViewById(R.id.summary0_line);
            summary0_title = (TextView) view.findViewById(R.id.summary0_title);
            summary0_text1 = (TextView) view.findViewById(R.id.summary0_text1);
            summary0_text1.setText(TimeUtils.getTime(new Date(),TimeUtils.YEAR)+"年");
            summary0_text2 = (TextView) view.findViewById(R.id.summary0_text2);
            summary0_text3 = (TextView) view.findViewById(R.id.summary0_text3);
            summary0_text4 = (TextView) view.findViewById(R.id.summary0_text4);
            summary0_text5 = (TextView) view.findViewById(R.id.summary0_text5);
        }

        private void initAnim0() {
            ValueAnimator animator0 = ValueAnimator.ofInt(0, summary0_line.getLayoutParams().width).setDuration(2500);
            animator0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    summary0_line.getLayoutParams().width = (int)animation.getAnimatedValue();
                    summary0_line.requestLayout();
                }
            });
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(summary0_title,"alpha",0f,1f).setDuration(1000);
            Animator animator2 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary0_text);
            animator2.setTarget(summary0_text1);
            animator2.setStartDelay(500);
            Animator animator3 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary0_text);
            animator3.setTarget(summary0_text2);
            animator3.setStartDelay(1500);
            Animator animator4 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary0_text);
            animator4.setTarget(summary0_text3);
            animator4.setStartDelay(2500);
            Animator animator5 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary0_text);
            animator5.setTarget(summary0_text4);
            animator5.setStartDelay(3500);
            Animator animator6 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary0_text);
            animator6.setTarget(summary0_text5);
            animator6.setStartDelay(4000);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator1,animator2,animator3,animator4,animator5,animator6);
            set.play(animator1).after(animator0);
            set.start();
        }

        private void initView1(View view) {
            summary1_time = (TextView) view.findViewById(R.id.summary1_time);
            summary1_money = (TextView) view.findViewById(R.id.summary1_money);
            summary1_sumMoney = (TextView) view.findViewById(R.id.summary1_sumMoney);
            int year = TimeUtils.getTime(new Date(),TimeUtils.YEAR);
            int month = -1;
            AccountRecordService accountRecordService = new AccountRecordService();
            BigDecimal maxMoney = new BigDecimal("0.00");
            BigDecimal sumMoney = new BigDecimal("0.00");
            for(int i = 1;i <= 12;i++){
                Date start = new Date(TimeUtils.getMonthFirstMilliSeconds(i,year - TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
                Date end = new Date(TimeUtils.getMonthLastMilliSeconds(i,year - TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
                BigDecimal money = new BigDecimal(accountRecordService.getRangeTotalMoneyByRecordname(start,end,"话费",false)).negate();
                sumMoney = sumMoney.add(money);
                if(maxMoney.doubleValue() < money.doubleValue()) {
                    maxMoney = money;
                    month = i;
                }
            }
            summary1_time.setText("『"+year+"年"+month+"月』");
            summary1_money.setText(maxMoney.toString());
            summary1_sumMoney.setText(sumMoney.toString());
        }

        private void initAnim1() {
//            ObjectAnimator animator0 = ObjectAnimator.ofFloat(summary1_time,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(summary1_money,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator2 = ObjectAnimator.ofFloat(summary1_sumMoney,"alpha",0f,1f).setDuration(1000);
            Animator animator0 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator0.setTarget(summary1_time);
            animator0.setStartDelay(0);
            Animator animator1 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator1.setTarget(summary1_money);
            animator1.setStartDelay(500);
            Animator animator2 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator2.setTarget(summary1_sumMoney);
            animator2.setStartDelay(1500);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator0,animator1,animator2);
            set.start();
        }

        private void initView2(View view) {
            summary2_time = (TextView) view.findViewById(R.id.summary2_time);
            summary2_money = (TextView) view.findViewById(R.id.summary2_money);
            summary2_sumMoney = (TextView) view.findViewById(R.id.summary2_sumMoney);
            int year = TimeUtils.getTime(new Date(),TimeUtils.YEAR);
            long time = 0;
            AccountRecordService accountRecordService = new AccountRecordService();
            long yearFirstMilliSeconds = TimeUtils.getYearFirstMilliSeconds(year).getTime();
            long yearLastMilliSeconds = TimeUtils.getYearLastMilliSeconds(year).getTime();
            List<AccountRecord> records = accountRecordService.getAccountRecordListByTime(
                    yearFirstMilliSeconds,yearLastMilliSeconds,"网购",null,null);
            BigDecimal maxMoney = new BigDecimal("0.00");
            BigDecimal sumMoney = new BigDecimal("0.00");
            for(int i = 0; i < records.size(); i++){
                BigDecimal money = new BigDecimal(records.get(i).getMoney()).negate();
                sumMoney = sumMoney.add(money);
                if(maxMoney.doubleValue() < money.doubleValue()) {
                    maxMoney = money;
                    time = records.get(i).getRecordtime();
                }
            }
            summary2_time.setText(new SimpleDateFormat("『yyyy年MM月dd日』").format(new Date(time)));
            summary2_money.setText(maxMoney.toString());
            summary2_sumMoney.setText(sumMoney.toString());
        }

        private void initAnim2() {
//            ObjectAnimator animator0 = ObjectAnimator.ofFloat(summary2_time,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(summary2_money,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator2 = ObjectAnimator.ofFloat(summary2_sumMoney,"alpha",0f,1f).setDuration(1000);
            Animator animator0 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator0.setTarget(summary2_time);
            animator0.setStartDelay(0);
            Animator animator1 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator1.setTarget(summary2_money);
            animator1.setStartDelay(500);
            Animator animator2 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator2.setTarget(summary2_sumMoney);
            animator2.setStartDelay(1500);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator0,animator1,animator2);
            set.start();
        }

        private void initView3(View view) {
            summary3_time = (TextView) view.findViewById(R.id.summary3_time);
            summary3_money = (TextView) view.findViewById(R.id.summary3_money);
            summary3_sumMoney = (TextView) view.findViewById(R.id.summary3_sumMoney);
            int year = TimeUtils.getTime(new Date(),TimeUtils.YEAR);
            long time = 0;
            AccountRecordService accountRecordService = new AccountRecordService();
            long yearFirstMilliSeconds = TimeUtils.getYearFirstMilliSeconds(year).getTime();
            long yearLastMilliSeconds = TimeUtils.getYearLastMilliSeconds(year).getTime();
            List<AccountRecord> records = accountRecordService.getAccountRecordListByTime(
                    yearFirstMilliSeconds,yearLastMilliSeconds,"餐饮",null,null);
            BigDecimal maxMoney = new BigDecimal("0.00");
            BigDecimal sumMoney = new BigDecimal("0.00");
            for(int i = 0; i < records.size(); i++){
                BigDecimal money = new BigDecimal(records.get(i).getMoney()).negate();
                sumMoney = sumMoney.add(money);
                if(maxMoney.doubleValue() < money.doubleValue()) {
                    maxMoney = money;
                    time = records.get(i).getRecordtime();
                }
            }
            summary3_time.setText(new SimpleDateFormat("『yyyy年MM月dd日』").format(new Date(time)));
            summary3_money.setText(maxMoney.toString());
            summary3_sumMoney.setText(sumMoney.toString());
        }

        private void initAnim3() {
//            ObjectAnimator animator0 = ObjectAnimator.ofFloat(summary3_time,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(summary3_money,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator2 = ObjectAnimator.ofFloat(summary3_sumMoney,"alpha",0f,1f).setDuration(1000);
            Animator animator0 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator0.setTarget(summary3_time);
            animator0.setStartDelay(0);
            Animator animator1 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator1.setTarget(summary3_money);
            animator1.setStartDelay(500);
            Animator animator2 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator2.setTarget(summary3_sumMoney);
            animator2.setStartDelay(1500);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator0,animator1,animator2);
            set.start();
        }

        private void initView4(View view) {
            PieChartView pieChart = (PieChartView) view.findViewById(R.id.piechart);
            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
            MoreSummaryRecyclerAdapter mRecyclerViewAdapter = new MoreSummaryRecyclerAdapter(getContext());
            mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_more_summary);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            AccountRecordService accountRecordService = new AccountRecordService();
            int year = TimeUtils.getTime(new Date(),TimeUtils.YEAR);
            Date start = new Date(TimeUtils.getYearFirstMilliSeconds(year).getTime());
            Date end = new Date(TimeUtils.getYearLastMilliSeconds(year).getTime());
            List<AccountRecord> records = accountRecordService.getAccountRecordListGroupByRecordname(start,end,false);
            int block = 6;
            BigDecimal otherMoney = new BigDecimal(records.get(block - 1).getMoney());
            if(records.size() > block) {
                for (int i = block; i < records.size(); i++) {
                    BigDecimal money = new BigDecimal(records.get(i).getMoney());
                    otherMoney = otherMoney.add(money);
                }
                records = records.subList(0,block);
                records.get(block - 1).setMoney(otherMoney.toString());
                records.get(block - 1).setRecordname("其它");
                records.get(block - 1).setIcon("icon_qita");
            }
            double totalMoney = accountRecordService.getRangeTotalMoneyByTime(start,end,false).doubleValue();
            List moneyList = new ArrayList();
            moneyList.add(totalMoney);
            mRecyclerViewAdapter.addDatas("totalMoney",moneyList);
            mRecyclerViewAdapter.addDatas("records",records);
            mRecyclerViewAdapter.notifyDataSetChanged();

            pieChart.setViewportCalculationEnabled(true);//设置饼图自动适应大小
            pieChart.setChartRotationEnabled(true);//设置饼图是否可以手动旋转
            pieChart.setCircleFillRatio(1);//设置饼图外圈缩放的比例

            List<SliceValue> values = new ArrayList<SliceValue>();
            for (int i = 0; i < records.size(); i++) {
                String color = "#";
                if(i == records.size() - 1) color = "#3ea6d6";
                else {
                    try {
                        color = ItemXmlPullParserUtils.parseIconColor(getContext(), "zhichu.xml", records.get(i).getIcon());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                float money = Float.parseFloat(records.get(i).getMoney())*-1;
                SliceValue sliceValue = new SliceValue(money, Color.parseColor(color));
                values.add(sliceValue);
            }
            PieChartData data = new PieChartData();
            data.setHasLabels(true);// 是否显示数据
            data.setSlicesSpacing(0);
            data.setHasLabelsOnlyForSelected(false);// 是否点击时才显示数据，一般为false
            data.setHasLabelsOutside(false);// 数据是否显示在外面
            data.setHasCenterCircle(true);// 设置饼图中间是否含有中间圈，显示Text这个必须为true
//        data.setCenterCircleColor(int centerCircleColor);//设置饼图中间圈的颜色
//        data.setCenterCircleScale(float centerCircleScale);//设置中间圈的大小比例
            data.setValueLabelBackgroundEnabled(false);// 设置是否显示数据的背景颜色

            data.setValues(values);//为饼图添加数据

            if(records.size() == 0){
                SliceValue sliceValue = new SliceValue(100, getResources().getColor(R.color.lightgray));
                values.add(sliceValue);
                data.setHasLabels(false);
            }
            pieChart.setPieChartData(data);

            summary4_name = (TextView) view.findViewById(R.id.summary4_name);
            summary4_sumMoney = (TextView) view.findViewById(R.id.summary4_sumMoney);
            summary4_name.setText(records.get(0).getRecordname());
            BigDecimal sumMoney = accountRecordService.getRangeTotalMoneyByTime(start,end,false).negate();
            summary4_sumMoney.setText(sumMoney.toString());

            btnShare = (GifImageView) view.findViewById(R.id.btnShare);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewScreen = getActivity().getWindow().getDecorView();
                    viewScreen.setDrawingCacheEnabled(true);
                    viewScreen.buildDrawingCache();
                    Bitmap bitmap = viewScreen.getDrawingCache();
                    String filePath = getActivity().getExternalCacheDir().getPath()  + File.separator + "screenshot.png";
                    File file = new File(filePath);
                    if (bitmap != null)
                    {
                        try {
                            FileOutputStream os = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.flush();
                            os.close();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    Uri imageUri = Uri.fromFile(file);//由文件得到uri
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "分享到"));
                }
            });
        }

        private void initAnim4() {
//            ObjectAnimator animator0 = ObjectAnimator.ofFloat(summary4_name,"alpha",0f,1f).setDuration(1000);
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(summary4_sumMoney,"alpha",0f,1f).setDuration(1000);
            Animator animator0 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator0.setTarget(summary4_name);
            animator0.setStartDelay(0);
            Animator animator1 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary_other);
            animator1.setTarget(summary4_sumMoney);
            animator1.setStartDelay(500);
            Animator animator2 = AnimatorInflater.loadAnimator(getContext(), R.animator.rotate_animator_summary0_text);
            animator2.setTarget(btnShare);
            animator2.setStartDelay(1500);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(animator0,animator1,animator2);
            set.start();
        }

    }
}

class MoreSummaryRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public MoreSummaryRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("records").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("records").size() + 2;
        return mDatas.get("records").size() + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new Holder(mHeaderView, viewType);
        if (mFooterView != null && viewType == TYPE_FOOTER)
            return new Holder(mFooterView, viewType);
        View layout = mInflater.inflate(mCreateViewLayout, parent, false);
        return new Holder(layout, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Holder holder = (Holder)viewHolder;
        final int pos = getRealPosition(holder);
        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) return;
        if(getItemViewType(position) == TYPE_NORMAL) {
            AccountRecord record = (AccountRecord) mDatas.get("records").get(pos);
            double totalMoney = (double)mDatas.get("totalMoney").get(0);
            double num = Double.parseDouble(record.getMoney());
            if (num > 0) {//收入
                holder.item_percent.setText(new DecimalFormat("0.0%").format(num/totalMoney));
            }
            else {
                holder.item_percent.setText(new DecimalFormat("0.0%").format(num/totalMoney));
            }
            int resID = mContent.getResources().getIdentifier(record.getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_name.setText(record.getRecordname());
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_percent;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_name = (TextView) itemView.findViewById(R.id.item_name);
                item_percent = (TextView) itemView.findViewById(R.id.item_percent);
            }
        }
    }
}