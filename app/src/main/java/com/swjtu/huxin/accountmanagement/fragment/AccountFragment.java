package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.AccountDetailActivity;
import com.swjtu.huxin.accountmanagement.activity.AccountTransferActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.service.AccountService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";

    private TextView money;
    private TextView btnTransfer;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private AccountRecyclerAdapter mRecyclerViewAdapter;

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static AccountFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        AccountFragment contentFragment = new AccountFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null)
            mArgument = bundle.getString(ARGUMENT);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container,false);

        money = (TextView) view.findViewById(R.id.money);
        btnTransfer = (TextView) view.findViewById(R.id.btnTransfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountTransferActivity.class);
                startActivityForResult(intent,2);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new AccountRecyclerAdapter(getContext());
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_account);
        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                Account account = (Account) mRecyclerViewAdapter.getDatas("accounts").get(pos);
                Intent intent = new Intent(getActivity(), AccountDetailActivity.class);
                intent.putExtra("account", account);
                startActivityForResult(intent,1);
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果
        initRecyclerViewData();
        return view;
    }

    private void initRecyclerViewData(){
        MyApplication app = MyApplication.getApplication();
        List<Account> accounts = new ArrayList<Account>(app.getAccounts().values());
        BigDecimal totalJinE = new BigDecimal("0.00");
        for(int i = 0; i < accounts.size(); i++){
            totalJinE = totalJinE.add(new BigDecimal(accounts.get(i).getMoney()));
        }
        AccountRecordService accountRecordService = new AccountRecordService();
        String totalMoney = accountRecordService.getTotalMoneyByAccount(null);
        money.setText(totalJinE.add(new BigDecimal(totalMoney)).toString());

        mRecyclerViewAdapter.addDatas("accounts",accounts);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    Account account = (Account) intent.getSerializableExtra("account");
                    MyApplication app = MyApplication.getApplication();
                    app.getAccounts().put(account.getId(),account);
                    initRecyclerViewData();

                    AccountService accountService = new AccountService();
                    accountService.updateAccount(account);
                }
                break;
            case 2:
                if(resultCode == getActivity().RESULT_OK) {
                    initRecyclerViewData();
                }
                break;
        }
    }

}

class AccountRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public AccountRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("accounts").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("accounts").size() + 2;
        return mDatas.get("accounts").size() + 1;
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
            final Account account = (Account) mDatas.get("accounts").get(pos);

            AccountRecordService accountRecordService = new AccountRecordService();
            String totalMoney = accountRecordService.getTotalMoneyByAccount(account);
            holder.item_money.setText(new BigDecimal(account.getMoney()).add(new BigDecimal(totalMoney)).toString());

            holder.item_back.setCardBackgroundColor(Color.parseColor(account.getColor()));

            holder.item_icon.setBackgroundResource(getIconByType(account.getType()));
            holder.item_text.setText(account.getAccountname());
            holder.item_detail1.setText(getDetail1ByType(account.getType()));
            holder.item_detail2.setText(account.getAccountdetail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,pos,"itemView");
                }
            });
        }
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

    static class Holder extends RecyclerView.ViewHolder {
        public CardView item_back;
        public ImageView item_icon;
        public TextView item_text;
        public TextView item_money;
        public TextView item_detail1;
        public TextView item_detail2;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_back = (CardView) itemView.findViewById(R.id.item_back);
                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_text = (TextView) itemView.findViewById(R.id.item_text);
                item_money = (TextView) itemView.findViewById(R.id.item_money);
                item_detail1 = (TextView) itemView.findViewById(R.id.item_detail1);
                item_detail2 = (TextView) itemView.findViewById(R.id.item_detail2);
            }
        }
    }
}
