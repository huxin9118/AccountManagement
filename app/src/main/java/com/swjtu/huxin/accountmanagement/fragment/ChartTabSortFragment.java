package com.swjtu.huxin.accountmanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.ChartDetailActivity;
import com.swjtu.huxin.accountmanagement.activity.DateRangePickerActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by huxin on 2017/3/11.
 */

public class ChartTabSortFragment extends Fragment implements Observer
{
    private String mArgument;
    public static final String ARGUMENT = "argument";

    private DataChangeThread dataChangeThread;
    private Handler dataChangeHandler;

    private ImageView left;
    private ImageView right;
    private TextView datePicker;
    private Date start;
    private Date end;

    private RelativeLayout chart;
    private PieChartView pieChart;
    private PieChartData data;
    private ImageView btnSwitch;
    private boolean isShouru = false;

    private boolean hasCenterText1 = true; // 圆中是否含有内容1
    private boolean hasCenterText2 = true; // 圆中是否含有内容2
    private boolean isExploded = true; // 是否爆破形式

    private LinearLayout empty;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChartTabSortRecyclerAdapter mRecyclerViewAdapter;
    private double totalMoney;
    List<AccountRecord> records;

    public static ChartTabSortFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        ChartTabSortFragment contentFragment = new ChartTabSortFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getApplication().getDataChangeObservable().addObserver(this);
        dataChangeHandler = new DataChangeHandler(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getApplication().getDataChangeObservable().deleteObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart_tab_sort,container,false);

        left = (ImageView) view.findViewById(R.id.date_left);
        right = (ImageView) view.findViewById(R.id.date_right);
        datePicker = (TextView) view.findViewById(R.id.date_picker);

        start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        updateDatePickerText();
        updateDateRangeChange();

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DateRangePickerActivity.class);
                intent.putExtra("back", "分类");
                intent.putExtra("start", start);
                intent.putExtra("end",end);
                startActivityForResult(intent, 1);
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,-1,0);
                end = TimeUtils.getIndexDate(end,0,-1,0);
                end = TimeUtils.getMaxDayDate(end);
                updateDatePickerText();
                updateDateRangeChange();
                new DataChangeThread().start();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,1,0);
                end = TimeUtils.getIndexDate(end,0,1,0);
                end = TimeUtils.getMaxDayDate(end);
                updateDatePickerText();
                updateDateRangeChange();
                new DataChangeThread().start();
            }
        });

        int[] attrsArray = { R.attr.half_transparent_contrast };
        TypedArray typedArray = getContext().obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        left.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        left.invalidate();
        right.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        right.invalidate();

        btnSwitch = (ImageView)view.findViewById(R.id.btnSwitch);
        chart = (RelativeLayout) view.findViewById(R.id.chart);
        pieChart = (PieChartView) view.findViewById(R.id.piechart);

        final Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_anim_switch);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataChangeThread == null||(dataChangeThread != null && !dataChangeThread.isAlive())) {
//                    btnSwitch.setAnimation(rotate);
//                    btnSwitch.startAnimation(rotate);
                    isShouru = isShouru ? false : true;
                    dataChangeThread = new DataChangeThread();
                    dataChangeThread.start();
                }
            }
        });

        empty = (LinearLayout) view.findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new ChartTabSortRecyclerAdapter(getContext());
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_chart_tab_sort);

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onClick(View view,int pos,String viewName) {
                if ("itemView".equals(viewName)) {
                    Intent intent = new Intent(getActivity(), ChartDetailActivity.class);
                    intent.putExtra("back", "分类");
                    intent.putExtra("start", start);
                    intent.putExtra("end",end);
                    intent.putExtra("record",(AccountRecord)mRecyclerViewAdapter.getDatas("records").get(pos));
                    startActivityForResult(intent,2);
                }
            }
        });

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果
        new DataChangeThread().start();

        return view;
    }

    class DataChangeThread extends Thread{
        @Override
        public void run() {
            updateData();
            dataChangeHandler.sendEmptyMessageDelayed(0, 0);
        }
    }

    static class DataChangeHandler extends Handler {
        WeakReference<ChartTabSortFragment> mFragmentReference;
        DataChangeHandler(ChartTabSortFragment fragment) {
            mFragmentReference= new WeakReference(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            final ChartTabSortFragment fragment = mFragmentReference.get();
            if (fragment != null) {
//                fragment.btnSwitch.clearAnimation();
                if(fragment.records.size() == 0){
                    fragment.empty.setVisibility(View.VISIBLE);
                }
                else{
                    fragment.empty.setVisibility(View.GONE);
                }
                fragment.initRecyclerViewData();
                fragment.initPieChartView();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        new DataChangeThread().start();
    }

    private void initPieChartView() {
        ViewGroup.LayoutParams pieChartLayoutParams = pieChart.getLayoutParams();
        chart.removeView(btnSwitch);
        chart.removeView(pieChart);
        pieChart = new PieChartView(getContext());
        chart.addView(pieChart,pieChartLayoutParams);
        chart.addView(btnSwitch);
        pieChart.setViewportCalculationEnabled(true);//设置饼图自动适应大小
        pieChart.setChartRotationEnabled(true);//设置饼图是否可以手动旋转
        pieChart.setCircleFillRatio(1);//设置饼图外圈缩放的比例
        pieChart.setChartRotation(180,true);//设置饼图旋转角度，且是否为动画
        pieChart.setChartRotationEnabled(true);//设置饼图是否可以手动旋转
//        pieChart.setCircleOval(RectF orginCircleOval);//设置饼图成椭圆形
        initPieChartData();
    }

    private void initPieChartData() {
        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < records.size(); i++) {
            String color = "#";
            try {
                if(isShouru)color = ItemXmlPullParserUtils.parseIconColor(getContext(), "shouru.xml", records.get(i).getIcon());
                else color = ItemXmlPullParserUtils.parseIconColor(getContext(), "zhichu.xml", records.get(i).getIcon());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            float money;
            if(isShouru)money= Float.parseFloat(records.get(i).getMoney());
            else money= Float.parseFloat(records.get(i).getMoney())*-1;
            SliceValue sliceValue = new SliceValue(money, Color.parseColor(color));
            values.add(sliceValue);
        }
        data = new PieChartData();
        data.setHasLabels(true);// 是否显示数据
        data.setHasLabelsOnlyForSelected(false);// 是否点击时才显示数据，一般为false
        data.setHasLabelsOutside(false);// 数据是否显示在外面
        data.setHasCenterCircle(true);// 设置饼图中间是否含有中间圈，显示Text这个必须为true

        int[] attrsArray1 = { R.attr.listItem };
        TypedArray typedArray1 = getContext().obtainStyledAttributes(attrsArray1);
        int color1 = typedArray1.getColor(0,-1);
        typedArray1.recycle();
        data.setCenterCircleColor(color1);//设置饼图中间圈的颜色
//        data.setCenterCircleScale(float centerCircleScale);//设置中间圈的大小比例
        data.setValueLabelBackgroundEnabled(false);// 设置是否显示数据的背景颜色

        if (isExploded) { //爆炸显示
            data.setSlicesSpacing(1);//设置数据间的间隙
        }

        data.setValues(values);//为饼图添加数据

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltxh.ttf");
        int[] attrsArray2 = { R.attr.textSecondaryColor };
        TypedArray typedArray2 = getContext().obtainStyledAttributes(attrsArray2);
        int color2 = typedArray2.getColor(0,-1);
        typedArray2.recycle();
        if (hasCenterText1) {
            if(isShouru)data.setCenterText1("总收入");
            else data.setCenterText1("总支出");
//            Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Italic.ttf");
//            data.setCenterText1Typeface(tf);//设置文本字体
//            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
//                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
            data.setCenterText1Typeface(font);//设置文本字体
            data.setCenterText1FontSize(16);//设置文本大小
            data.setCenterText1Color(color2);//设置文本颜色
        }

        if (hasCenterText2) {
            if(isShouru)data.setCenterText2(new DecimalFormat("0.00").format(totalMoney));
            else data.setCenterText2(new DecimalFormat("0.00").format(totalMoney*-1));
            data.setCenterText2Typeface(font);//设置文本字体
            data.setCenterText2FontSize(22);
            data.setCenterText2Color(color2);
        }

        if(records.size() == 0){
            SliceValue sliceValue = new SliceValue(100, getResources().getColor(R.color.lightgray));
            values.add(sliceValue);
            data.setHasLabels(false);
            data.setCenterText2("0.00");
        }

        pieChart.setPieChartData(data);
    }

    private void updateDateRangePickerReturn() {
        if (TimeUtils.getTime(start, TimeUtils.YEAR) == TimeUtils.getTime(end, TimeUtils.YEAR)
                && TimeUtils.getTime(start, TimeUtils.MONTH) == TimeUtils.getTime(end, TimeUtils.MONTH)
                && TimeUtils.getTime(start, TimeUtils.DAY) == 1
                && TimeUtils.getTime(end, TimeUtils.DAY) == TimeUtils.getMaxDay(end, 0, 0)) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
        } else {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);
        }
    }
    private void updateDateRangeChange(){
        if(TimeUtils.getTime(start,TimeUtils.YEAR) == 1970 && TimeUtils.getTime(start,TimeUtils.MONTH) == 1){
            left.setVisibility(View.INVISIBLE);
        }
        if(TimeUtils.getTime(start,TimeUtils.YEAR) == 2100 && TimeUtils.getTime(start,TimeUtils.MONTH) == 12){
            right.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDatePickerText(){
        if(TimeUtils.getTime(start,TimeUtils.YEAR) != TimeUtils.getTime(end,TimeUtils.YEAR)) {
            datePicker.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("yyyy年MM月dd日").format(end));
        }
        else{
            if(TimeUtils.getTime(start,TimeUtils.YEAR) == TimeUtils.getTime(new Date(),TimeUtils.YEAR)){
                if (TimeUtils.getTime(start, TimeUtils.MONTH) != TimeUtils.getTime(end, TimeUtils.MONTH)) {
                    datePicker.setText(new SimpleDateFormat("MM月dd日").format(start) + "~" + new SimpleDateFormat("MM月dd日").format(end));
                }
                else{
                    if (TimeUtils.getTime(start, TimeUtils.DAY) != TimeUtils.getTime(end, TimeUtils.DAY)) {
                        datePicker.setText(new SimpleDateFormat("MM月dd日").format(start) + "~" + new SimpleDateFormat("dd日").format(end));
                    }
                    else{
                        datePicker.setText(new SimpleDateFormat("MM月dd日").format(start));
                    }
                }
            }
            else {
                if (TimeUtils.getTime(start, TimeUtils.MONTH) != TimeUtils.getTime(end, TimeUtils.MONTH)) {
                    datePicker.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("MM月dd日").format(end));
                }
                else{
                    datePicker.setText(new SimpleDateFormat("yyyy年MM月dd日").format(start) + "~" + new SimpleDateFormat("dd日").format(end));
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK) {
                    start = (Date)intent.getSerializableExtra("start");
                    end = (Date)intent.getSerializableExtra("end");
                    updateDatePickerText();
                    updateDateRangePickerReturn();
                    new DataChangeThread().start();
                }
                break;
            case 2:
                if(resultCode == getActivity().RESULT_OK) {
                    new DataChangeThread().start();
                }
                break;
        }
    }

    private void initRecyclerViewData(){
        List moneyList = new ArrayList();
        moneyList.add(totalMoney);
        mRecyclerViewAdapter.addDatas("totalMoney",moneyList);
        mRecyclerViewAdapter.addDatas("records",records);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void updateData(){
        AccountRecordService accountRecordService = new AccountRecordService();
        records = accountRecordService.getAccountRecordListGroupByRecordname(start,end,isShouru);
        totalMoney = accountRecordService.getRangeTotalMoneyByTime(start,end,isShouru).doubleValue();
    }
}

class ChartTabSortRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public ChartTabSortRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
        mDatas.put("totalMoney",new ArrayList());
        mDatas.put("records",new ArrayList<AccountRecord>());
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
                holder.item_money.setText(new DecimalFormat("0.00").format(num));
                holder.item_percent.setText(new DecimalFormat("0.0%").format(num/totalMoney));
                try {
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.
                            parseIconColor(mContext, "shouru.xml", record.getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                holder.item_money.setText(new DecimalFormat("0.00").format(num*-1));
                holder.item_percent.setText(new DecimalFormat("0.0%").format(num/totalMoney));
                try {
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.
                            parseIconColor(mContext, "zhichu.xml", record.getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            int resID = mContent.getResources().getIdentifier(record.getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_name.setText(record.getRecordname());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,pos,"itemView");
                }
            });
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_percent;
        public TextView item_money;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_name = (TextView) itemView.findViewById(R.id.item_name);
                item_percent = (TextView) itemView.findViewById(R.id.item_percent);
                item_money = (TextView) itemView.findViewById(R.id.item_money);
            }
        }
    }
}
