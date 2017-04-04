package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
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
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by huxin on 2017/3/11.
 */

public class AccountSelectColorActivity extends BaseAppCompatActivity {

    private LinearLayout btnBack;
    
    private Account account;

    private CardView item_back;
    private ImageView item_icon;
    private TextView item_text;
    private TextView item_money;
    private TextView item_detail1;
    private TextView item_detail2;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AccountSelectColorRecyclerAdapter mRecyclerViewAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_select_color);
        initView();
    }

    private void initView() {
        btnBack = (LinearLayout) findViewById(R.id.back);
        item_back = (CardView) findViewById(R.id.item_back);
        item_icon = (ImageView) findViewById(R.id.item_icon);
        item_text = (TextView) findViewById(R.id.item_text);
        item_money = (TextView) findViewById(R.id.item_money);
        item_detail1 = (TextView) findViewById(R.id.item_detail1);
        item_detail2 = (TextView) findViewById(R.id.item_detail2);
        
        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        AccountRecordService accountRecordService = new AccountRecordService();
        String totalMoney = accountRecordService.getTotalMoneyByAccount(account);
        item_money.setText(new BigDecimal(account.getMoney()).add(new BigDecimal(totalMoney)).toString());
        item_back.setCardBackgroundColor(Color.parseColor(account.getColor()));
        item_icon.setBackgroundResource(getIconByType(account.getType()));
        item_text.setText(account.getAccountname());
        item_detail1.setText(getDetail1ByType(account.getType()));
        item_detail2.setText(account.getAccountdetail());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                Intent intent = new Intent();
                intent.putExtra("account",account);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(this,7);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new AccountSelectColorRecyclerAdapter(this);
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_account_select_color);
        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if("itemView".equals(viewName)) {
                    item_back.setCardBackgroundColor(Color.parseColor(ConstantUtils.ACCOUNT_COLOR[pos]));
                    account.setColor(ConstantUtils.ACCOUNT_COLOR[pos]);
                    mRecyclerViewAdapter.getDatas("selectedColor").set(0,ConstantUtils.ACCOUNT_COLOR[pos]);
                    mRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果
        initRecyclerViewData();
    }

    private void initRecyclerViewData(){
        List selectedColor = new ArrayList();
        selectedColor.add(account.getColor());
        mRecyclerViewAdapter.addDatas("selectedColor",selectedColor);
        mRecyclerViewAdapter.addDatas("colors",new ArrayList(Arrays.asList(ConstantUtils.ACCOUNT_COLOR)));
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    private int getIconByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return R.drawable.ic_cash;
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return R.drawable.ic_bank_card;
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return R.drawable.ic_credit_card;
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return R.drawable.ic_alipay;
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return R.drawable.ic_wechat;
            default:return -1;
        }
    }

    private String getDetail1ByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return "现金类型：";
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return "发卡行：";
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return "发卡行：";
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return "账号：";
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return "账号：";
            default:return "";
        }
    }

    @Override
    public void onBackPressed() {
        cancelToast();
        Intent intent = new Intent();
        intent.putExtra("account",account);
        setResult(RESULT_OK, intent);
        finish();
    }
}

class AccountSelectColorRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public AccountSelectColorRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("colors").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("colors").size() + 2;
        return mDatas.get("colors").size() + 1;
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
            String color = (String) mDatas.get("colors").get(pos);
            String selectedColor = (String) mDatas.get("selectedColor").get(0);
            if(color.equals(selectedColor))
                holder.item_selector.setVisibility(View.VISIBLE);
            else
                holder.item_selector.setVisibility(View.GONE);
            holder.item_back.setCardBackgroundColor(Color.parseColor(color));
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
        public ImageView item_selector;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_back = (CardView) itemView.findViewById(R.id.item_back);
                item_selector = (ImageView) itemView.findViewById(R.id.item_selector);
            }
        }
    }
}
