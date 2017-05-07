package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.AddItem;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by huxin on 2017/3/9.
 */

public class BudgetActivity extends BaseAppCompatActivity {
    private BigDecimal totalMoney;
    private BigDecimal remainingMoney;
    private ArrayList<BigDecimal> totalMoneys;
    private ArrayList<BigDecimal> remainingMoneys;
    private ArrayList<String> recordnames;
    private int selectPositon = -1;

    private LinearLayout back;
    private LinearLayout setting;

    private TextView money_text;
        private TextView progress_end_text;
    private NumberProgressBar progressBar;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private BudgetRecyclerAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
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

    private void initView() {
        back = (LinearLayout) findViewById(R.id.back);
        setting = (LinearLayout) findViewById(R.id.setting);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("totalMoney", totalMoney);
                intent.putExtra("remainingMoney",remainingMoney);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetActivity.this, BudgetSettingActivity.class);
                intent.putExtra("totalMoney", totalMoney.toString());
                startActivityForResult(intent, 1);
            }
        });

        money_text = (TextView)findViewById(R.id.money);
        progress_end_text = (TextView)findViewById(R.id.progress_end);
        progressBar = (NumberProgressBar)findViewById(R.id.progress);

        Intent intent = getIntent();
        totalMoney = (BigDecimal)intent.getSerializableExtra("totalMoney");
        remainingMoney = (BigDecimal)intent.getSerializableExtra("remainingMoney");

        updateProgressBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new BudgetRecyclerAdapter(this);
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_budget);

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onClick(View view,int pos,String viewName) {
                if ("itemView".equals(viewName)) {
                    selectPositon = pos;
                    Intent intent = new Intent(BudgetActivity.this, BudgetSubItemSettingActivity.class);
                    intent.putExtra("totalMoney", totalMoneys.get(pos).toString());
                    intent.putExtra("recordname", recordnames.get(pos));
                    startActivityForResult(intent, 2);
                }
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        initRecyclerViewData();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    BigDecimal newTotalMoney = new BigDecimal(intent.getStringExtra("totalMoney"));
                    remainingMoney = remainingMoney.add(newTotalMoney.subtract(totalMoney));
                    totalMoney = newTotalMoney;//这三行顺序不能乱
                    updateProgressBar();
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    BigDecimal newTotalMoney = new BigDecimal(intent.getStringExtra("totalMoney"));
                    remainingMoneys.set(selectPositon, remainingMoneys.get(selectPositon).add(newTotalMoney.subtract(totalMoneys.get(selectPositon))));
                    totalMoneys.set(selectPositon ,newTotalMoney);//这三行顺序不能乱
                    mRecyclerViewAdapter.notifyItemChanged(selectPositon);
                }
                break;
        }
    }

    void updateProgressBar(){
        money_text.setText(remainingMoney.toString());
        progress_end_text.setText(totalMoney.toString());

        progressBar.setMax((int)(totalMoney.doubleValue()*100));
        if(remainingMoney.doubleValue() >= 0) {
            progressBar.setReachedBarColor(getResources().getColor(R.color.customBlue));
            progressBar.setProgress((int)(remainingMoney.doubleValue()*100));
        }
        else{
            progressBar.setReachedBarColor(getResources().getColor(R.color.orangered));
            if(remainingMoney.negate().doubleValue() < totalMoney.doubleValue())
                progressBar.setProgress((int)(remainingMoney.negate().doubleValue()*100));
            else
                progressBar.setProgress((int)(totalMoney.doubleValue()*100));
        }
    }

    void initRecyclerViewData(){
        Date start = new Date(TimeUtils.getMonthFirstMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        Date end = new Date(TimeUtils.getMonthLastMilliSeconds(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0));
        MyApplication app = MyApplication.getApplication();
        ArrayList<AddItem> zhichuAddItems = app.getZhichuAddItems();
        totalMoneys = new ArrayList<>();
        remainingMoneys = new ArrayList<>();
        recordnames = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("budgetData", Context.MODE_PRIVATE);
        for(int i = 0; i < zhichuAddItems.size(); i++){
            recordnames.add(zhichuAddItems.get(i).getNameAddItem());
            totalMoneys.add(new BigDecimal(sharedPreferences.getString("totalMoney_"+ recordnames.get(i),"0.00")));
            AccountRecordService accountRecordService = new AccountRecordService();
            String nowMoney = accountRecordService.getRangeTotalMoneyByRecordname(start, end, recordnames.get(i),false);
            remainingMoneys.add(totalMoneys.get(i).add(new BigDecimal(nowMoney)));
        }
        mRecyclerViewAdapter.addDatas("zhichu",zhichuAddItems);
        mRecyclerViewAdapter.addDatas("totalMoneys",totalMoneys);
        mRecyclerViewAdapter.addDatas("remainingMoneys",remainingMoneys);
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("totalMoney", totalMoney);
        intent.putExtra("remainingMoney",remainingMoney);
        setResult(RESULT_OK, intent);
        finish();
    }
}

class BudgetRecyclerAdapter extends BaseRecyclerViewAdapter{
    private Context mContext;

    public BudgetRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("zhichu").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("zhichu").size() + 2;
        return mDatas.get("zhichu").size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView != null && position == 0) return TYPE_HEADER;
        if(mFooterView != null && position == getItemCount() - 1)return TYPE_FOOTER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) return new Holder(mHeaderView, viewType);
        if(viewType == TYPE_FOOTER) return new Holder(mFooterView, viewType);
        View layout = mInflater.inflate(mCreateViewLayout, parent, false);
        return new Holder(layout, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Holder holder = (Holder)viewHolder;
        final int pos = getRealPosition(holder);
        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) return;
        if(getItemViewType(position) == TYPE_NORMAL) {
            ArrayList<AddItem> zhichuAddItems = (ArrayList<AddItem>)mDatas.get("zhichu");
            ArrayList<BigDecimal> totalMoneys = (ArrayList<BigDecimal>)mDatas.get("totalMoneys");
            ArrayList<BigDecimal> remainingMoneys = (ArrayList<BigDecimal>)mDatas.get("remainingMoneys");

            int resID = mContent.getResources().getIdentifier(zhichuAddItems.get(pos).getIconAddItem(), "drawable", mContent.getPackageName());
            Glide.with(mContent).load(resID).dontAnimate().into(holder.item_icon);
            holder.item_name.setText(zhichuAddItems.get(pos).getNameAddItem());
            holder.item_totalMoney.setText(totalMoneys.get(pos).toString());

            if(!"0.00".equals(totalMoneys.get(pos).toString())) {
                holder.item_progress.setMax((int)((totalMoneys.get(pos).doubleValue()*100)));
                if (remainingMoneys.get(pos).doubleValue() >= 0) {
                    holder.item_label2.setText("剩余");
                    holder.item_remainingMoney.setText(remainingMoneys.get(pos).toString());
                    holder.item_progress.setReachedBarColor(mContext.getResources().getColor(R.color.customBlue));
                    holder.item_progress.setProgress((int) (remainingMoneys.get(pos).doubleValue() * 100));
                } else {
                    holder.item_label2.setText("超支");
                    holder.item_remainingMoney.setText(remainingMoneys.get(pos).negate().toString());
                    holder.item_progress.setReachedBarColor(mContext.getResources().getColor(R.color.orangered));
                    if (remainingMoneys.get(pos).negate().doubleValue() < totalMoneys.get(pos).doubleValue())
                        holder.item_progress.setProgress((int) (remainingMoneys.get(pos).negate().doubleValue() * 100));
                    else
                        holder.item_progress.setProgress((int) (totalMoneys.get(pos).doubleValue() * 100));
                }
            }
            else{
                holder.item_progress.setMax(0);
                holder.item_progress.setProgress(0);
                holder.item_label1.setText("请点击设置预算");
                holder.item_label2.setText("");
                holder.item_totalMoney.setText("");
                holder.item_remainingMoney.setText("");
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v, pos, "itemView");
                }
            });
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public ImageView item_icon;
        public NumberProgressBar item_progress;
        public TextView item_name;
        public TextView item_totalMoney;
        public TextView item_remainingMoney;
        public TextView item_label1;
        public TextView item_label2;

        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_progress = (NumberProgressBar) itemView.findViewById(R.id.item_progress);

                item_name = (TextView) itemView.findViewById(R.id.item_name);
                item_totalMoney = (TextView) itemView.findViewById(R.id.item_totalMoney);
                item_remainingMoney = (TextView) itemView.findViewById(R.id.item_remainingMoney);
                item_label1 = (TextView) itemView.findViewById(R.id.item_label1);
                item_label2 = (TextView) itemView.findViewById(R.id.item_label2);
            }
        }
    }
}

