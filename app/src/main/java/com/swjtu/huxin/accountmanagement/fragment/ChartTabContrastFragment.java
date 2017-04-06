package com.swjtu.huxin.accountmanagement.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by huxin on 2017/3/11.
 */

public class ChartTabContrastFragment extends Fragment
{
    private String mArgument;
    public static final String ARGUMENT = "argument";

    private ImageView left;
    private ImageView right;
    private TextView year;
    private int date;

    private LineChartView lineChart;
    private LineChartData lineChartData;

    private boolean isShouru = false;
    private ImageView btnSwitch;
    private List<AccountRecord> records;
    private List<BigDecimal> money;

    private LinearLayout empty;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChartTabContrastRecyclerAdapter mRecyclerViewAdapter;

    public static ChartTabContrastFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        ChartTabContrastFragment contentFragment = new ChartTabContrastFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart_tab_contrast,container,false);

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

        btnSwitch = (ImageView) view.findViewById(R.id.btnSwitch);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShouru = isShouru?false:true;
                updateData();
            }
        });

        empty = (LinearLayout) view.findViewById(R.id.empty);
        lineChart = (LineChartView) view.findViewById(R.id.linechart);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(getContext(),2);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new ChartTabContrastRecyclerAdapter(getContext());
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_chart_tab_contrast);
        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if("itemView".equals(viewName)) {
//                    try {
//                        int color = Color.parseColor(ItemXmlPullParserUtils.parseIconColor(getContext(),
//                                "zhichu.xml", records.get(pos).getIcon()));
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
                    AccountRecordService accountRecordService = new AccountRecordService();
                    money = new ArrayList<BigDecimal>();
                    for(int i = 1;i <= 12;i++){
                        Date start = new Date(TimeUtils.getMonthFirstMilliSeconds(i,date - TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
                        Date end = new Date(TimeUtils.getMonthLastMilliSeconds(i,date - TimeUtils.getTime(new Date(),TimeUtils.YEAR)));
                        if(isShouru)
                            money.add(new BigDecimal(accountRecordService.getRangeTotalMoneyByRecordname(start,end,records.get(pos).getRecordname())));
                        else
                            money.add(new BigDecimal(accountRecordService.getRangeTotalMoneyByRecordname(start,end,records.get(pos).getRecordname())).negate());
                    }
                    if(isShouru){
                        try {
                            generateLineData(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(getContext(),
                                    "shouru.xml", records.get(pos).getIcon())),money);
                            Viewport v = new Viewport(0, 3000, 11, -100);
                            lineChart.setMaximumViewport(v);
                            lineChart.setCurrentViewport(v);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            generateLineData(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(getContext(),
                                    "zhichu.xml", records.get(pos).getIcon())),money);
                            Viewport v = new Viewport(0, 3000, 11, -100);
                            lineChart.setMaximumViewport(v);
                            lineChart.setCurrentViewport(v);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果

        updateData();
        return view;
    }

    private void updateData(){
        AccountRecordService accountRecordService = new AccountRecordService();
        records = accountRecordService.getAccountRecordListGroupByRecordname(TimeUtils.getYearFirstMilliSeconds(date),
                TimeUtils.getYearLastMilliSeconds(date),isShouru);
        if(records.size() == 0){
            empty.setVisibility(View.VISIBLE);
        }
        else {
            empty.setVisibility(View.GONE);
            initRecyclerViewData();
            initLineChart();
        }
    }

    private void initRecyclerViewData(){
        mRecyclerViewAdapter.addDatas("records",records);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void initLineChart(){
        Axis axisY = new Axis();// Y轴属性
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
        List<PointValue> moneyValues = new ArrayList<PointValue>();// 节点数据结合
        for (int i = 0; i < 12; i++) {//循环为节点、X、Y轴添加数据
            moneyValues.add(new PointValue(i, 0));// 添加节点数据
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

        Line lineMoney = new Line(moneyValues);//将值设置给折线
//        lineMoney.setColor();// 设置折线颜色
        lineMoney.setStrokeWidth(2);// 设置折线宽度
        lineMoney.setFilled(false);// 设置折线覆盖区域是否填充
        lineMoney.setCubic(false);// 是否设置为平滑线
//        lineMoney.setPointColor();// 设置节点颜色
        lineMoney.setPointRadius(4);// 设置节点半径
        lineMoney.setHasLabels(true);// 是否显示节点数据
        lineMoney.setHasLabelsOnlyForSelected(false);// 隐藏节点数据，触摸可以显示
        lineMoney.setHasLines(true);// 是否显示折线
        lineMoney.setHasPoints(true);// 是否显示节点
        lineMoney.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形

        lines.add(lineMoney);// 将数据集合添加线

        lineChartData = new LineChartData(lines);//将线的集合设置为折线图的数据
        lineChartData.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        lineChartData.setAxisXBottom(axisX);// 将X轴属性设置到底部
//        lineChartData.setAxisYRight(axisYRight);//设置右边显示的轴
//        lineChartData.setAxisXTop(axisXTop);//设置顶部显示的轴
        lineChartData.setBaseValue(0);// 设置正负基准，默认为0
        lineChartData.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
//        lineChartData.setValueLabelBackgroundAuto(true);// 设置数据背景是否跟随节点颜色
//        lineChartData.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
//        lineChartData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
//        lineChartData.setValueLabelTextSize(15);// 设置数据文字大小
//        lineChartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式

        lineChart.setZoomEnabled(false);//设置是否支持缩放
//        lineChart.setZoomType(ZoomType.HORIZONTAL);
//        lineChart.setOnValueTouchListener(LineChartOnValueSelectListener touchListener);//为图表设置值得触摸事件
        lineChart.setInteractive(false);//设置图表是否可以与用户互动
        lineChart.setValueSelectionEnabled(false);//设置图表数据是否被选中高亮
        lineChart.setLineChartData(lineChartData);//为图表设置数据，数据类型为LineChartData

        Viewport v = new Viewport(0, 3000, 11, 0);
        lineChart.setMaximumViewport(v);
        lineChart.setCurrentViewport(v);
    }

    private void generateLineData(int color, List<BigDecimal> money) {
        // Cancel last animation if not finished.
        lineChart.cancelDataAnimation();
        // Modify data targets
        Line line = lineChartData.getLines().get(0);// For this example there is
        // always only one line.
        line.setColor(color);
        for (int i = 0; i < 12; i++) {//循环为节点、X、Y轴添加数据
            line.getValues().get(i).setTarget(line.getValues().get(i).getX(), money.get(i).floatValue());
        }
        lineChartData.setValueLabelsTextColor(color);
        lineChart.setLineChartData(lineChartData);
        // Start new data animation with 300ms duration;
        lineChart.startDataAnimation(300);
    }
}

class ChartTabContrastRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public ChartTabContrastRecyclerAdapter(Context context) {
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
            double num = Double.parseDouble(((AccountRecord) mDatas.get("records").get(pos)).getMoney());
            int count = ((AccountRecord) mDatas.get("records").get(pos)).getId();
            if (num > 0) {//收入
                holder.item_money.setText(new DecimalFormat("0.00").format(num));
                holder.item_money.setText(new DecimalFormat("0.00").format(num/count));
                try {
                    holder.item_back.setCardBackgroundColor(Color.parseColor(ItemXmlPullParserUtils.
                            parseIconColor(mContext, "shouru.xml", ((AccountRecord) mDatas.get("records").get(pos)).getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                holder.item_label1.setText("累计收入金额");
                holder.item_label2.setText("累计收入次数");
                holder.item_label3.setText("月均收入");
            }
            else {
                holder.item_money.setText(new DecimalFormat("0.00").format(num*-1));
                holder.item_money.setText(new DecimalFormat("0.00").format(num*-1/count));
                try {
                    holder.item_back.setCardBackgroundColor(Color.parseColor(ItemXmlPullParserUtils.
                            parseIconColor(mContext, "zhichu.xml", ((AccountRecord) mDatas.get("records").get(pos)).getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                holder.item_label1.setText("累计消费金额");
                holder.item_label2.setText("累计消费次数");
                holder.item_label3.setText("月均消费");
            }
            int resID = mContent.getResources().getIdentifier(((AccountRecord)mDatas.get("records").get(pos)).getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_count.setText(count+"");
            holder.item_name.setText(((AccountRecord) mDatas.get("records").get(pos)).getRecordname());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,pos,"itemView");
                }
            });
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public CardView item_back;
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_money;
        public TextView item_avg_money;
        public TextView item_count;
        public TextView item_label1;
        public TextView item_label2;
        public TextView item_label3;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_back = (CardView) itemView.findViewById(R.id.item_back);
                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_name = (TextView) itemView.findViewById(R.id.item_name);
                item_money = (TextView) itemView.findViewById(R.id.item_money);
                item_avg_money = (TextView) itemView.findViewById(R.id.item_avg_money);
                item_count = (TextView) itemView.findViewById(R.id.item_count);
                item_label1 = (TextView) itemView.findViewById(R.id.item_label1);
                item_label2 = (TextView) itemView.findViewById(R.id.item_label2);
                item_label3 = (TextView) itemView.findViewById(R.id.item_label3);
            }
        }
    }
}