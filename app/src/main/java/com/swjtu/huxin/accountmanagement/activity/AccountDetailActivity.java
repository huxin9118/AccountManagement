package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;
import com.swjtu.huxin.accountmanagement.view.ItemSwipeHelpter;
import com.swjtu.huxin.accountmanagement.view.ItemSwipeLayout;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huxin on 2017/3/19.
 */

public class AccountDetailActivity extends BaseAppCompatActivity {
    private LinearLayout background_color;

    private LinearLayout back;
    private TextView title;
    private LinearLayout setting;

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

    private void initView(){
        background_color = (LinearLayout) findViewById(R.id.background_color);

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
        setting = (LinearLayout) findViewById(R.id.setting);
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
                mRecyclerViewAdapter.getmItemSwipeHelpter().close();
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
                mRecyclerViewAdapter.getmItemSwipeHelpter().close();
            }
        });

        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        title.setText(getTypeTextByType(account.getType()));
        background_color.setBackgroundColor(Color.parseColor(account.getColor()));

        numShouru = (TextView) findViewById(R.id.numShouru);
        numZhichu = (TextView) findViewById(R.id.numZhichu);
        numJieyu = (TextView) findViewById(R.id.numJieyu);

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
            public void onClick(View view,final int pos,String viewName) {
                mRecyclerViewAdapter.getmItemSwipeHelpter().close();
                if("item_edit".equals(viewName)) {
                    Intent intent = new Intent(AccountDetailActivity.this, AddItemActivity.class);
                    intent.putExtra("edit", ((AccountRecord) mRecyclerViewAdapter.getDatas("records").get(pos)));
                    startActivityForResult(intent, 2);
                }
                if("item_delete".equals(viewName)) {
                    new MaterialDialog.Builder(AccountDetailActivity.this).title("提示").content("确定删除该账目？")
                            .positiveText("是").negativeText("否")
                            .backgroundColorAttr( R.attr.popupwindow_backgound)
                            .contentColorAttr(R.attr.textSecondaryColor).titleColorAttr(R.attr.textColor)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    AccountRecord removedRecord = (AccountRecord) mRecyclerViewAdapter.getDatas("records").get(pos);
                                    AccountRecordService accountRecordService = new AccountRecordService();
                                    accountRecordService.removeAccountRecord(removedRecord);
                                    updateData();
                                    MyApplication.getApplication().getDataChangeObservable().dataChange();
                                }})
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).show();
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
            long dayFirstMilliSeconds = TimeUtils.getDateDayFirstMilliSeconds(time);
            long dayLastMilliSeconds = TimeUtils.getDateDayLastMilliSeconds(time);
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
                dayFirstMilliSeconds = TimeUtils.getDateDayFirstMilliSeconds(time);
                dayLastMilliSeconds = TimeUtils.getDateDayLastMilliSeconds(time);
            }
        }
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void updateData(){
        AccountRecordService accountRecordService = new AccountRecordService();
        String totalMoney = accountRecordService.getTotalMoneyByAccount(account);
        numJieyu.setText(new BigDecimal(account.getMoney()).add(new BigDecimal(totalMoney)).toString());

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
        cancelToast();
        Intent intent = new Intent();
        intent.putExtra("account", account);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1://账户设置
                if(resultCode == RESULT_OK){
                    account = (Account) intent.getSerializableExtra("account");
                    background_color.setBackgroundColor(Color.parseColor(account.getColor()));
                    AccountRecordService accountRecordService = new AccountRecordService();
                    String totalMoney = accountRecordService.getTotalMoneyByAccount(account);
                    numJieyu.setText(new BigDecimal(account.getMoney()).add(new BigDecimal(totalMoney)).toString());
                }
                break;
            case 2 ://编辑
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
}

class AccountDetailRecyclerAdapter extends BaseRecyclerViewAdapter  implements ItemSwipeHelpter.Callback{
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemSwipeHelpter mItemSwipeHelpter;
    public final static int TYPE_DAY = 3;

    public AccountDetailRecyclerAdapter(Context context) {
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
                    holder.item_money.setTextColor(Color.parseColor(ItemXmlPullParserUtils.parseIconColor(mContext, "zhichu.xml",record.getIcon())));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            int resID = mContent.getResources().getIdentifier(record.getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_name.setText(record.getRecordname());
            if(record.getAccountbook() == null)
                holder.item_remark.setText(record.getMember()+record.getRemark());
            else
                holder.item_remark.setText(record.getRemark());
            if("".equals(holder.item_remark.getText().toString()))
                holder.item_remark.setVisibility(View.GONE);

            if(record.getAccountbook() == null){
                holder.item_edit.setVisibility(View.GONE);
            }
            else{
                holder.item_edit.setVisibility(View.VISIBLE);
            }

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
