package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huxin on 2017/3/19.
 */

public class AccountDetailActivity extends BaseAppCompatActivity {
    private LinearLayout background;

    private LinearLayout back;
    private TextView title;
    private ImageView setting;

    private ImageView left;
    private ImageView right;
    private TextView month;
    private TextView dateRange;
    private Date start;
    private Date end;

    private TextView numShouru;
    private TextView numZhichu;
    private TextView numJieyu;

    private Account account;

    private LinearLayout empty;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AccountDetailRecyclerAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        initView();
    }

    private void initView(){
        background = (LinearLayout) findViewById(R.id.background);

        back = (LinearLayout) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("account", account);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        setting = (ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(AccountDetailActivity.this,AccountSettingActivity.class);
            intent.putExtra("account",account);
            startActivityForResult(intent,1);
            }
        });

        left = (ImageView) findViewById(R.id.date_left);
        right = (ImageView) findViewById(R.id.date_right);
        month = (TextView) findViewById(R.id.date_picker);
        dateRange = (TextView) findViewById(R.id.date_Range);
        start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        month.setText(new SimpleDateFormat("MM").format(start));
        dateRange.setText(new SimpleDateFormat("yyyy.M.d").format(start) + "~" + new SimpleDateFormat("M.d").format(end));
        updateDateRangeChange();

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,-1,0);
                end = TimeUtils.getIndexDate(end,0,-1,0);
                end = TimeUtils.getMaxDayDate(end);
                month.setText(new SimpleDateFormat("MM").format(start));
                dateRange.setText(new SimpleDateFormat("yyyy.M.d").format(start) + "~" + new SimpleDateFormat("M.d").format(end));
                updateDateRangeChange();
                updateData();
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = TimeUtils.getIndexDate(start,0,1,0);
                end = TimeUtils.getIndexDate(end,0,1,0);
                end = TimeUtils.getMaxDayDate(end);
                month.setText(new SimpleDateFormat("MM").format(start));
                dateRange.setText(new SimpleDateFormat("yyyy.M.d").format(start) + "~" + new SimpleDateFormat("M.d").format(end));
                updateDateRangeChange();
                updateData();
            }
        });

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        title.setText(getTypeTextByType(account.getType()));
        background.setBackgroundColor(Color.parseColor(account.getColor()));

        numShouru = (TextView) findViewById(R.id.numShouru);
        numZhichu = (TextView) findViewById(R.id.numZhichu);
        numJieyu = (TextView) findViewById(R.id.numJieyu);
        AccountRecordService accountRecordService = new AccountRecordService();
        String totalMoney = accountRecordService.getTotalMoneyByAccount(account);
        numJieyu.setText(new BigDecimal(account.getMoney()).add(new BigDecimal(totalMoney)).toString());

        empty = (LinearLayout) findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new AccountDetailRecyclerAdapter(this);
        initRecyclerViewData(false);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if("itemView".equals(viewName)) {
//                    Intent intent = new Intent(ChartDetailActivity.this, AddItem.class);
//                    intent.putExtra("back", "明细");
//                    intent.putExtra("record", (AccountRecord)mRecyclerViewAdapter.getDatas("records").get(pos));
//                    startActivityForResult(intent, 1);
                }
            }
        });
        updateData();
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
                records = accountRecordService.getAccountRecordListByTime(dayFirstMilliSeconds, dayLastMilliSeconds, null, account,null);
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
        String shouru = new DecimalFormat("0.00").format(Double.valueOf(accountRecordService.getRangeTotalMoneyByAccount(start,end,account,true)));
        String zhichu = new DecimalFormat("0.00").format(Double.valueOf(accountRecordService.getRangeTotalMoneyByAccount(start,end,account,false)));
        if("0.00".equals(shouru) && "0.00".equals(zhichu)) {
            numShouru.setText("0.00");
            numZhichu.setText("0.00");
            empty.setVisibility(View.VISIBLE);
            initRecyclerViewData(true);
        }
        else {
            numShouru.setText(shouru);
            numZhichu.setText(zhichu.substring(1));
            empty.setVisibility(View.GONE);
            initRecyclerViewData(false);
        }
    }

    private String getTypeTextByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return "现金";
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return "储蓄卡";
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return "信用卡";
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return "支付宝";
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return "微信钱包";
            default:return "";
        }
    }

    public void onBackPressed() {
        Log.i("000", "onActivityResult: "+"asfafafasfs");
        cancelToast();
        Intent intent = new Intent();
        intent.putExtra("account", account);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    account = (Account) intent.getSerializableExtra("account");
                    background.setBackgroundColor(Color.parseColor(account.getColor()));
                    AccountRecordService accountRecordService = new AccountRecordService();
                    String totalMoney = accountRecordService.getTotalMoneyByAccount(account);
                    numJieyu.setText(new BigDecimal(account.getMoney()).add(new BigDecimal(totalMoney)).toString());
                }
        }
    }
}

class AccountDetailRecyclerAdapter extends BaseRecyclerViewAdapter{
    private Context mContext;
    public final static int TYPE_DAY = 3;

    public AccountDetailRecyclerAdapter(Context context) {
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
            double num = Double.parseDouble(((AccountRecord) mDatas.get("records").get(pos)).getMoney());
            if (num > 0) {//收入
                holder.item_money.setText("+"+new DecimalFormat("0.00").format(num));
                try {
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(mContext, "shouru.xml", ((AccountRecord) mDatas.get("records").get(pos)).getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                holder.item_money.setText("-"+new DecimalFormat("0.00").format(num*-1));
                try {
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(mContext, "zhichu.xml", ((AccountRecord) mDatas.get("records").get(pos)).getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            int resID = mContent.getResources().getIdentifier(((AccountRecord)mDatas.get("records").get(pos)).getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_name.setText(((AccountRecord) mDatas.get("records").get(pos)).getRecordname());
            holder.item_remark.setText(((AccountRecord) mDatas.get("records").get(pos)).getRemark());
            if("".equals(holder.item_remark.getText().toString()))
                holder.item_remark.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,pos,"itemView");
                }
            });
        }
        if(getItemViewType(position) == TYPE_DAY) {
            String dayText = (new SimpleDateFormat("M月d日").format(new Date(((AccountRecord)mDatas.get("records").get(pos)).getRecordtime())));
            holder.day_text.setText(dayText);
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public ImageView item_icon;
        public TextView item_name;
        public TextView item_remark;
        public TextView item_money;
        public TextView day_text;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
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
