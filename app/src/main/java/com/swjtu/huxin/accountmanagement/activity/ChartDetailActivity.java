package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;
import com.swjtu.huxin.accountmanagement.view.ItemSwipeHelpter;
import com.swjtu.huxin.accountmanagement.view.ItemSwipeLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huxin on 2017/3/19.
 */

public class ChartDetailActivity extends BaseAppCompatActivity {
    private LinearLayout background;

    private LinearLayout back;
    private TextView backText;
    private TextView title;

    private ImageView left;
    private ImageView right;
    private TextView datePicker;
    private Date start;
    private Date end;

    private TextView text;
    private TextView money;

    private AccountRecord record;

    private LinearLayout empty;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ChartDetailRecyclerAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_detail);
        initView();
    }

    private void initView(){
        background = (LinearLayout) findViewById(R.id.background);

        back = (LinearLayout) findViewById(R.id.back);
        backText = (TextView) findViewById(R.id.back_text);
        title = (TextView) findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        left = (ImageView) findViewById(R.id.date_left);
        right = (ImageView) findViewById(R.id.date_right);
        datePicker = (TextView) findViewById(R.id.date_picker);

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,-1,0);
                end = TimeUtils.getIndexDate(end,0,-1,0);
                end = TimeUtils.getMaxDayDate(end);
                updateDatePickerText();
                updateDateRangeChange();
                updateData();
                mRecyclerViewAdapter.getmItemSwipeHelpter().close();//关闭Item侧滑菜单
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
                updateData();
                mRecyclerViewAdapter.getmItemSwipeHelpter().close();
            }
        });

        text = (TextView) findViewById(R.id.text);
        money = (TextView) findViewById(R.id.money);

        Intent intent = getIntent();
        start = (Date)intent.getSerializableExtra("start");
        end = (Date)intent.getSerializableExtra("end");
        updateDatePickerText();
        updateDateRangeChange();
        backText.setText(intent.getStringExtra("back"));
        record = (AccountRecord) intent.getSerializableExtra("record");
        if("tab_member".equals(intent.getStringExtra("from"))) {
            String member = record.getMember();
            if("".equals(member))member = "无成员";
            title.setText(member + "明细");
        }
        else {
            title.setText(record.getRecordname() + "明细");
        }
        try {
            if (Double.parseDouble(record.getMoney()) > 0) {
                text.setText("收入");
                money.setText(new DecimalFormat("0.00").format(Double.parseDouble(record.getMoney())));
                if("tab_member".equals(intent.getStringExtra("from")))
                    background.setBackgroundColor(Color.parseColor(getColorByMember(record.getMember())));
                else
                    background.setBackgroundColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(this, "shouru.xml", record.getIcon())));
            } else {
                text.setText("支出");
                money.setText(new DecimalFormat("0.00").format(Double.parseDouble(record.getMoney())*-1));
                if("tab_member".equals(intent.getStringExtra("from")))
                    background.setBackgroundColor(Color.parseColor(getColorByMember(record.getMember())));
                else
                    background.setBackgroundColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(this, "zhichu.xml", record.getIcon())));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        empty = (LinearLayout) findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new ChartDetailRecyclerAdapter(this);
        initRecyclerViewData(false);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果
        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos,String viewName) {
                mRecyclerViewAdapter.getmItemSwipeHelpter().close();
                if("item_edit".equals(viewName)) {
                    Intent intent = new Intent(ChartDetailActivity.this, AddItemActivity.class);
                    intent.putExtra("edit", ((AccountRecord) mRecyclerViewAdapter.getDatas("records").get(pos)));
                    startActivityForResult(intent, 1);
                }
                if("item_delete".equals(viewName)) {
                    AccountRecord removedRecord = (AccountRecord) mRecyclerViewAdapter.getDatas("records").get(pos);
                    AccountRecordService accountRecordService = new AccountRecordService();
                    accountRecordService.removeAccountRecord(removedRecord);
                    updateData();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1 :
                if(resultCode == RESULT_OK){
                    AccountRecord record = (AccountRecord)intent.getSerializableExtra("data");
                    AccountRecordService accountRecordService = new AccountRecordService();
                    accountRecordService.updateAccountRecord(record);
                    mRecyclerViewAdapter.addDatas("records",new ArrayList<AccountRecord>());//清空数据
                    updateData();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public String getColorByMember(String Member){
        switch (Member) {
            case "":return ConstantUtils.ACCOUNT_COLOR[4];
            case ConstantUtils.ACCOUNT_RECORD_MEMBER_ME:return ConstantUtils.ACCOUNT_COLOR[5];
            case ConstantUtils.ACCOUNT_RECORD_MEMBER_FATHER:return ConstantUtils.ACCOUNT_COLOR[6];
            case ConstantUtils.ACCOUNT_RECORD_MEMBER_MOTHER:return ConstantUtils.ACCOUNT_COLOR[7];
            default:return "#";
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

    private void updateDateRangeChange(){
        if(TimeUtils.getTime(start,TimeUtils.YEAR) == 1970 && TimeUtils.getTime(start,TimeUtils.MONTH) == 1){
            left.setVisibility(View.INVISIBLE);
        }
        if(TimeUtils.getTime(start,TimeUtils.YEAR) == 2100 && TimeUtils.getTime(start,TimeUtils.MONTH) == 12){
            right.setVisibility(View.INVISIBLE);
        }
    }

    private void initRecyclerViewData(boolean isEmpty){
        mRecyclerViewAdapter.addDatas("records",new ArrayList<AccountRecord>());//清空原有数据
        if(!isEmpty) {
            List<AccountRecord> records;
            AccountRecordService accountRecordService = new AccountRecordService();
            Date time = end;
            long dayFirstMilliSeconds = TimeUtils.getDateFirstMilliSeconds(time);
            long dayLastMilliSeconds = TimeUtils.getDateLastMilliSeconds(time);
            while (dayFirstMilliSeconds >= start.getTime()) {
                records = accountRecordService.getAccountRecordListByTime(dayFirstMilliSeconds, dayLastMilliSeconds, record.getRecordname(),null,record.getMember());
                if (records.size() > 0) {//这一天有记录
                    AccountRecord recordDay = new AccountRecord();
                    recordDay.setRecordname("DAY");
                    recordDay.setRecordtime(dayFirstMilliSeconds);
                    records.add(0, recordDay);
                }
                mRecyclerViewAdapter.getDatas("records").addAll(mRecyclerViewAdapter.getDatas("records").size(), records);//添加单日数据
                time = TimeUtils.getIndexDate(time, 0, 0, -1);
                dayFirstMilliSeconds = TimeUtils.getDateFirstMilliSeconds(time);
                dayLastMilliSeconds = TimeUtils.getDateLastMilliSeconds(time);
            }
        }
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void updateData(){
        AccountRecordService accountRecordService = new AccountRecordService();
        String totalMoney = accountRecordService.getRangeTotalMoneyByRecordname(start, end, record.getRecordname());
        if("0.00".equals(totalMoney)) {
            empty.setVisibility(View.VISIBLE);
            money.setText("0.00");
            initRecyclerViewData(true);
        }
        else {
            empty.setVisibility(View.GONE);
            money.setText(totalMoney);
            initRecyclerViewData(false);
        }
    }
}

class ChartDetailRecyclerAdapter extends BaseRecyclerViewAdapter  implements ItemSwipeHelpter.Callback{
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemSwipeHelpter mItemSwipeHelpter;
    public final static int TYPE_DAY = 3;

    public ChartDetailRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public ItemSwipeLayout getSwipLayout(float x, float y) {
        if(mRecyclerView.findChildViewUnder(x,y) instanceof ItemSwipeLayout)
            return (ItemSwipeLayout)mRecyclerView.findChildViewUnder(x,y);
        return null;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mItemSwipeHelpter = new ItemSwipeHelpter(mContext,this);
        recyclerView.addOnItemTouchListener(mItemSwipeHelpter);
    }

    public ItemSwipeHelpter getmItemSwipeHelpter() {
        return mItemSwipeHelpter;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("records").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("records").size() + 2;
        return mDatas.get("records").size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView != null && position == 0) return TYPE_HEADER;
        if(mFooterView != null && position == getItemCount() - 1)return TYPE_FOOTER;

        int pos = mHeaderView == null ? position : position - 1;
        if("DAY".equals(((AccountRecord)mDatas.get("records").get(pos)).getRecordname()))
            return TYPE_DAY;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) return new Holder(mHeaderView, viewType);
        if(viewType == TYPE_FOOTER) return new Holder(mFooterView, viewType);
        if(viewType == TYPE_DAY) {
            View layout = mInflater.inflate(R.layout.item_recycler_chart_detail_day, parent, false);
            return new Holder(layout, viewType);
        }
        View layout = mInflater.inflate(R.layout.item_recycler_chart_detail, parent, false);
        return new Holder(layout, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Holder holder = (Holder)viewHolder;
        final int pos = getRealPosition(holder);
        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) return;
        if(getItemViewType(position) == TYPE_NORMAL) {
            AccountRecord record = (AccountRecord) mDatas.get("records").get(pos);
            double num = Double.parseDouble(record.getMoney());
            if (num > 0) {//收入
                holder.item_money.setText("+"+new DecimalFormat("0.00").format(num));
                try {
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(mContext, "shouru.xml", record.getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                holder.item_money.setText("-"+new DecimalFormat("0.00").format(num*-1));
                try {
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(mContext, "zhichu.xml", record.getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            int resID = mContent.getResources().getIdentifier(record.getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_name.setText(record.getRecordname());
            holder.item_remark.setText(record.getRemark());
            if("".equals(holder.item_remark.getText().toString()))
                holder.item_remark.setVisibility(View.GONE);
            else
                holder.item_remark.setVisibility(View.VISIBLE);

            holder.item_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemSwipeHelpter.isExpanded())
                        mItemSwipeHelpter.close();
                }
            });
            holder.item_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v, pos,"item_edit");
                }
            });
            holder.item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v, pos,"item_delete");
                }
            });
        }
        if(getItemViewType(position) == TYPE_DAY) {
            AccountRecord record = (AccountRecord) mDatas.get("records").get(pos);
            String dayText = (new SimpleDateFormat("M月d日").format(new Date(record.getRecordtime())));
            holder.day_text.setText(dayText);
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public RelativeLayout item_content;
        public TextView item_delete;
        public TextView item_edit;
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_remark;
        public TextView item_money;
        public TextView day_text;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_content = (RelativeLayout)itemView.findViewById(R.id.item_content);
                item_delete = (TextView) itemView.findViewById(R.id.item_delete);
                item_edit = (TextView) itemView.findViewById(R.id.item_edit);

                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_name = (TextView) itemView.findViewById(R.id.item_name);
                item_remark = (TextView) itemView.findViewById(R.id.item_remark);
                item_money = (TextView) itemView.findViewById(R.id.item_money);
            }
            if(viewType == TYPE_DAY) {
                day_text = (TextView) itemView.findViewById(R.id.day_text);
            }
        }
    }
}