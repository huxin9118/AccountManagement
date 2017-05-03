package com.swjtu.huxin.accountmanagement.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by huxin on 2017/3/11.
 */

public class ChartTabTrendFragment extends Fragment
{
    private String mArgument;
    public static final String ARGUMENT = "argument";

    private ImageView left;
    private ImageView right;
    private TextView year;
    private int date;

    private TextView btnShouru;
    private TextView btnZhichu;
    private TextView btnJieyu;
    private boolean isBtnShouruPressed = true;
    private boolean isBtnZhichuPressed = true;
    private boolean isBtnJieyuPressed = true;

    private LineChartView lineChart;
    private LineChartData chartData;

    private LinearLayout empty;
    private LinearLayout content;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChartTabTrendRecyclerAdapter mRecyclerViewAdapter;
    private List<BigDecimal> numShouru;
    private List<BigDecimal> numZhichu;
    private List<BigDecimal> numJieyu;
    private double maxNum;
    private double minNum;

    public static ChartTabTrendFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        ChartTabTrendFragment contentFragment = new ChartTabTrendFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart_tab_trend,container,false);

        left = (ImageView) view.findViewById(R.id.date_left);
        right = (ImageView) view.findViewById(R.id.date_right);
        year = (TextView) view.findViewById(R.id.date_picker);
//        start = new Date(TimeUtils.getYearFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
//        end = new Date(TimeUtils.getYearLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
        date = TimeUtils.getTime(new Date(),TimeUtils.YEAR);
        year.setText(date+"年");

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date--;
                year.setText(date+"年");
                updateData();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date++;
                year.setText(date+"年");
                updateData();
            }
        });

        btnShouru = (TextView) view.findViewById(R.id.btnShouru);
        btnZhichu = (TextView) view.findViewById(R.id.btnZhichu);
        btnJieyu = (TextView) view.findViewById(R.id.btnJieyu);
        btnShouru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBtnShouruPressed){
                    isBtnShouruPressed = false;
                    btnShouru.setBackgroundResource(R.drawable.shape_button_chart_trend_unpressed);
                    btnShouru.setTextColor(getResources().getColor(R.color.gray));
                }
                else {
                    isBtnShouruPressed = true;
                    btnShouru.setBackgroundResource(R.drawable.shape_button_chart_trend_shouru);
                    btnShouru.setTextColor(getResources().getColor(R.color.white));
                }
                updateData();
            }
        });
        btnZhichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBtnZhichuPressed){
                    isBtnZhichuPressed = false;
                    btnZhichu.setBackgroundResource(R.drawable.shape_button_chart_trend_unpressed);
                    btnZhichu.setTextColor(getResources().getColor(R.color.gray));
                }
                else {
                    isBtnZhichuPressed = true;
                    btnZhichu.setBackgroundResource(R.drawable.shape_button_chart_trend_zhichu);
                    btnZhichu.setTextColor(getResources().getColor(R.color.white));
                }
                updateData();
            }
        });
        btnJieyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBtnJieyuPressed){
                    isBtnJieyuPressed = false;
                    btnJieyu.setBackgroundResource(R.drawable.shape_button_chart_trend_unpressed);
                    btnJieyu.setTextColor(getResources().getColor(R.color.gray));
                }
                else {
                    isBtnJieyuPressed = true;
                    btnJieyu.setBackgroundResource(R.drawable.shape_button_chart_trend_jieyu);
                    btnJieyu.setTextColor(getResources().getColor(R.color.white));
                }
                updateData();
            }
        });

        empty = (LinearLayout) view.findViewById(R.id.empty);
        content = (LinearLayout) view.findViewById(R.id.content);
        lineChart = (LineChartView) view.findViewById(R.id.linechart);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new ChartTabTrendRecyclerAdapter(getContext());
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_chart_tab_trend);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果


        updateData();
        return view;
    }

    private void updateData(){
        maxNum = 0;
        minNum = 0;
        numShouru = new ArrayList<BigDecimal>();
        numZhichu = new ArrayList<BigDecimal>();
        numJieyu = new ArrayList<BigDecimal>();
        AccountRecordService accountRecordService = new AccountRecordService();
        for(int i = 1;i <= 12;i++){
            Date start = new Date(TimeUtils.getMonthFirstMilliSeconds(i,date - TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
            Date end = new Date(TimeUtils.getMonthLastMilliSeconds(i,date - TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
            BigDecimal shouru = accountRecordService.getRangeTotalMoneyByTime(start,end,true);
            BigDecimal zhichu = accountRecordService.getRangeTotalMoneyByTime(start,end,false).negate();
            numShouru.add(shouru);
            numZhichu.add(zhichu);
            numJieyu.add(shouru.subtract(zhichu));
            maxNum = shouru.doubleValue() > maxNum  ? shouru.doubleValue() : maxNum;
            maxNum = zhichu.doubleValue() > maxNum  ? zhichu.doubleValue() : maxNum;
            minNum = shouru.subtract(zhichu).doubleValue() < minNum  ? shouru.subtract(zhichu).doubleValue() : minNum;
        }
        if(maxNum == 0 && minNum == 0){
            empty.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
        else{
            empty.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            initRecyclerViewData();
            initLineChart();
        }
    }

    private void initRecyclerViewData(){
        mRecyclerViewAdapter.addDatas("month",new ArrayList<String>());
        mRecyclerViewAdapter.addDatas("numShouru",new ArrayList<BigDecimal>());
        mRecyclerViewAdapter.addDatas("numZhichu",new ArrayList<BigDecimal>());
        mRecyclerViewAdapter.addDatas("numJieyu",new ArrayList<BigDecimal>());
        BigDecimal totalShouru = new BigDecimal("0.00");
        BigDecimal totalZhichu = new BigDecimal("0.00");
        BigDecimal totalJieyu = new BigDecimal("0.00");
        for(int i = 0;i < numShouru.size();i++) {
            if (numShouru.get(i).doubleValue() != 0 || numZhichu.get(i).doubleValue() != 0 || numJieyu.get(i).doubleValue() != 0) {
                mRecyclerViewAdapter.getDatas("month").add(i+1+"月");
                mRecyclerViewAdapter.getDatas("numShouru").add(numShouru.get(i));
                mRecyclerViewAdapter.getDatas("numZhichu").add(numZhichu.get(i));
                mRecyclerViewAdapter.getDatas("numJieyu").add(numJieyu.get(i));
                totalShouru = totalShouru.add(numShouru.get(i));
                totalZhichu = totalZhichu.add(numZhichu.get(i));
                totalJieyu = totalJieyu.add(numJieyu.get(i));
            }
        }
        mRecyclerViewAdapter.getDatas("month").add("总计");
        mRecyclerViewAdapter.getDatas("numShouru").add(totalShouru);
        mRecyclerViewAdapter.getDatas("numZhichu").add(totalZhichu);
        mRecyclerViewAdapter.getDatas("numJieyu").add(totalJieyu);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initLineChart(){
        Axis axisY = new Axis().setHasLines(true);// Y轴属性
        Axis axisX = new Axis();// X轴属性
//        axisY.setName(String yName);//设置Y轴显示名称
//        axisX.setName(String xName);//设置X轴显示名称
//        axisX.setTextColor(Color color);// 设置X轴文字颜色
//        axisY.setTextColor(Color color);// 设置Y轴文字颜色
//        axisX.setTextSize(14);// 设置X轴文字大小
//        axisX.setTypeface(Typeface.DEFAULT);// 设置文字样式，此处为默认
//        axisX.setHasTiltedLabels(false);// 设置X轴文字向左旋转45度
//        axisX.setInside(false);// 设置X轴文字是否在X轴内部
        axisX.setLineColor(getResources().getColor(R.color.lightgray));// 设置X轴轴线颜色
        axisY.setLineColor(getResources().getColor(R.color.lightgray));// 设置Y轴轴线颜色
        axisX.setHasLines(false);// 是否显示X轴网格线
        axisY.setHasLines(true);// 是否显示Y轴网格线
        axisX.setHasSeparationLine(true);// 设置是否有轴线
        axisY.setHasSeparationLine(false);// 设置是否有轴线

        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();//定义X轴刻度值的数据集合
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();//定义Y轴刻度值的数据集合
//        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
        List<PointValue> shouruValues = new ArrayList<PointValue>();// 节点数据结合
        List<PointValue> zhichuValues = new ArrayList<PointValue>();// 节点数据结合
        List<PointValue> jieyuValues = new ArrayList<PointValue>();// 节点数据结合
        for (int i = 0; i < numShouru.size(); i++) {//循环为节点、X、Y轴添加数据
            shouruValues.add(new PointValue(i, numShouru.get(i).floatValue()));// 添加节点数据
            zhichuValues.add(new PointValue(i, numZhichu.get(i).floatValue()));// 添加节点数据
            jieyuValues.add(new PointValue(i, numJieyu.get(i).floatValue()));// 添加节点数据
//            axisValuesY.add(new AxisValue(j).setValue(j));// 添加Y轴显示的刻度值
            axisValuesX.add(new AxisValue(i).setLabel(i+1+"月"));// 添加X轴显示的刻度值
        }
        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
        axisY.setMaxLabelChars(5);
//        for(int i = (int)(minNum / 1000) - 1; i <= (int)(maxNum / 1000) + 1; i++){
//            axisValuesY.add(new AxisValue(i*1000).setLabel(i+"k"));
//        }
//        axisY.setValues(axisValuesY);
//        Log.i("222", minNum+"==="+((int)(minNum / 1000) - 1));
//        Log.i("222", maxNum+"==="+((int)(maxNum / 1000) + 1));

        List<Line> lines = new ArrayList<Line>();//定义线的集合

        Line lineShouru = new Line(shouruValues);//将值设置给折线
        lineShouru.setColor(getResources().getColor(R.color.shouru));// 设置折线颜色
        lineShouru.setStrokeWidth(2);// 设置折线宽度
        lineShouru.setFilled(true);// 设置折线覆盖区域是否填充
        lineShouru.setCubic(false);// 是否设置为平滑线
        lineShouru.setPointColor(getResources().getColor(R.color.shouru));// 设置节点颜色
        lineShouru.setPointRadius(4);// 设置节点半径
        lineShouru.setHasLabels(false);// 是否显示节点数据
        lineShouru.setHasLabelsOnlyForSelected(false);// 隐藏节点数据，触摸可以显示
        lineShouru.setHasLines(true);// 是否显示折线
        lineShouru.setHasPoints(true);// 是否显示节点
        lineShouru.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
        if(isBtnShouruPressed)
            lines.add(lineShouru);// 将数据集合添加线

        Line lineZhichu = new Line(zhichuValues);//将值设置给折线
        lineZhichu.setColor(getResources().getColor(R.color.zhichu));// 设置折线颜色
        lineZhichu.setStrokeWidth(2);// 设置折线宽度
        lineZhichu.setFilled(true);// 设置折线覆盖区域是否填充
        lineZhichu.setCubic(false);// 是否设置为平滑线
        lineZhichu.setPointColor(getResources().getColor(R.color.zhichu));// 设置节点颜色
        lineZhichu.setPointRadius(4);// 设置节点半径
        lineZhichu.setHasLabels(false);// 是否显示节点数据
        lineZhichu.setHasLabelsOnlyForSelected(false);// 隐藏节点数据，触摸可以显示
        lineZhichu.setHasLines(true);// 是否显示折线
        lineZhichu.setHasPoints(true);// 是否显示节点
        lineZhichu.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
        if(isBtnZhichuPressed)
            lines.add(lineZhichu);// 将数据集合添加线

        Line lineJieyu = new Line(jieyuValues);//将值设置给折线
        lineJieyu.setColor(getResources().getColor(R.color.jieyu));// 设置折线颜色
        lineJieyu.setStrokeWidth(2);// 设置折线宽度
        lineJieyu.setFilled(true);// 设置折线覆盖区域是否填充
        lineJieyu.setCubic(false);// 是否设置为平滑线
        lineJieyu.setPointColor(getResources().getColor(R.color.jieyu));// 设置节点颜色
        lineJieyu.setPointRadius(4);// 设置节点半径
        lineJieyu.setHasLabels(false);// 是否显示节点数据
        lineJieyu.setHasLabelsOnlyForSelected(false);// 隐藏节点数据，触摸可以显示
        lineJieyu.setHasLines(true);// 是否显示折线
        lineJieyu.setHasPoints(true);// 是否显示节点
        lineJieyu.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
        if(isBtnJieyuPressed)
            lines.add(lineJieyu);// 将数据集合添加线

        chartData = new LineChartData(lines);//将线的集合设置为折线图的数据
        chartData.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        chartData.setAxisXBottom(axisX);// 将X轴属性设置到底部
//        chartData.setAxisYRight(axisYRight);//设置右边显示的轴
//        chartData.setAxisXTop(axisXTop);//设置顶部显示的轴
        chartData.setBaseValue(0);// 设置正负基准，默认为0
        chartData.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
//        chartData.setValueLabelBackgroundAuto(true);// 设置数据背景是否跟随节点颜色
//        chartData.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
//        chartData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
//        chartData.setValueLabelTextSize(15);// 设置数据文字大小
//        chartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        lineChart.setZoomEnabled(false);//设置是否支持缩放
//        lineChart.setOnValueTouchListener(LineChartOnValueSelectListener touchListener);//为图表设置值得触摸事件
        lineChart.setInteractive(false);//设置图表是否可以与用户互动
        lineChart.setValueSelectionEnabled(false);//设置图表数据是否选中进行显示
        lineChart.setLineChartData(chartData);//为图表设置数据，数据类型为LineChartData
    }
}

class ChartTabTrendRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public ChartTabTrendRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("numShouru").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("numShouru").size() + 2;
        return mDatas.get("numShouru").size() + 1;
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
            holder.item_month.setText(((String) (mDatas.get("month").get(pos))));
            holder.item_Shouru.setText(((BigDecimal) (mDatas.get("numShouru").get(pos))).toString());
            holder.item_Zhichu.setText(((BigDecimal) (mDatas.get("numZhichu").get(pos))).toString());
            holder.item_Jieyu.setText(((BigDecimal) (mDatas.get("numJieyu").get(pos))).toString());
            if(((BigDecimal) (mDatas.get("numJieyu").get(pos))).doubleValue() >= 0){
                holder.item_Jieyu.setTextColor(mContext.getResources().getColor(R.color.customBlue));
            }
            else{
                holder.item_Jieyu.setTextColor(mContext.getResources().getColor(R.color.orangered));
            }
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView item_month;
        public TextView item_Shouru;
        public TextView item_Zhichu;
        public TextView item_Jieyu;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_month = (TextView) itemView.findViewById(R.id.item_month);
                item_Shouru = (TextView) itemView.findViewById(R.id.item_Shouru);
                item_Zhichu = (TextView) itemView.findViewById(R.id.item_Zhichu);
                item_Jieyu = (TextView) itemView.findViewById(R.id.item_Jieyu);
            }
        }
    }
}
