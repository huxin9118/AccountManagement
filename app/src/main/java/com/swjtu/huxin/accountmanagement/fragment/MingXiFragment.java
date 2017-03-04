package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.adapter.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.application.myApplication;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.AddItemActivity;
import com.swjtu.huxin.accountmanagement.dao.DatabaseHelper;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;
import com.swjtu.huxin.accountmanagement.view.CustomPtrHeader;
import com.swjtu.huxin.accountmanagement.view.PercentageBallView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MingXiFragment extends Fragment {

    private String mArgument;
    private DatabaseHelper dbHelper;
    public static final String ARGUMENT = "argument";
    private boolean isRecyclerViewTop = true;
    private Date firstTime;

    private PtrFrameLayout mPtrFrameLayout;
    private PercentageBallView pv;
    private TextView txtZhichu;
    private TextView txtShouru;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MingXiRecyclerAdapter mRecyclerViewAdapter;
    private CustomPtrHeader mHeaderView;
    private FloatingActionButton mFloatingActionButton;

    /**
     * 测试数据
     */
//    private String[] nameShouru = {"","","工资 500.00","","零花钱 22.00","外快兼职 66.00","投资 89.00","","",""};
//    private String[] nameZhichu = {"2000.00 电影","88.00 买菜","","99.63 衣服鞋包","","","","115.25 酒水饮料","12.52 水果","69.50 零食"};
//    private Integer[] iconItem = {R.drawable.icon_dianying,R.drawable.icon_maicai,R.drawable.icon_gongzi,R.drawable.icon_yifu,
//            R.drawable.icon_linghuaqian,R.drawable.icon_waikuaijianzhi,R.drawable.icon_touzi,
//            R.drawable.icon_jiushui,R.drawable.icon_shuiguo,R.drawable.icon_lingshi};

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param argument
     * @return
     */
    public static MingXiFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        MingXiFragment contentFragment = new MingXiFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null) mArgument = bundle.getString(ARGUMENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mingxi,container,false);

        //创建自定义刷新头部view
        mHeaderView = new CustomPtrHeader(getContext());
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
        //设置刷新头部view
        mPtrFrameLayout.setHeaderView(mHeaderView);
        //设置回调
        mPtrFrameLayout.addPtrUIHandler(mHeaderView);
        //设置下拉刷新监听
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            //需要加载数据时触发
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrFrameLayout.refreshComplete();
                    }
                }, 2000);

            }
            @Override
            //检查是否可以执行下来刷新
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                return isRecyclerViewTop;
            }
        });

        //启动时自动刷新
        mPtrFrameLayout.autoRefresh(false);
        //下拉时阻止事件分发
        mPtrFrameLayout.setInterceptEventWhileWorking(true);
        //横向滑动不刷新，和ViewPager配合使用
        //mPtrFrameLayout.disableWhenHorizontalMove(true);

        pv = (PercentageBallView) view.findViewById(R.id.ball);
        pv.setmWaterLevel(0.8f, "月预算\n3500");
        pv.startWave();
        txtShouru = (TextView)view.findViewById(R.id.shouru);
        txtZhichu = (TextView)view.findViewById(R.id.zhichu);
        int month = TimeUtils.getTime(new Date(),TimeUtils.MONTH);
        AccountRecordService accountRecordService = new AccountRecordService();
        String[] money = accountRecordService.getMonthMoneyByTime(month,0);
        txtShouru.setText(month+"月收入\n"+money[0]);
        txtZhichu.setText(month+"月支出\n"+money[1]);

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddItemActivity.class);
                startActivityForResult(intent,1, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "sharedView").toBundle());
            }
        });
        mFloatingActionButton.setVisibility(View.INVISIBLE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //设置固定大小
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(getContext());
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        //创建适配器，并且设置
        mRecyclerViewAdapter = new MingXiRecyclerAdapter(getContext());
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_mingxi);

        View header = inflater.inflate(R.layout.item_recycler_mingxi_header, container, false);
        mRecyclerViewAdapter.setHeaderView(header);
        ImageView imgHeader = (ImageView)header.findViewById(R.id.item_icon);
        imgHeader.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AddItemActivity.class);
                //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "sharedView").toBundle());
                startActivityForResult(intent,1, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "sharedView").toBundle());
            }
        });

        View footer = inflater.inflate(R.layout.item_recycler_mingxi_footer, container, false);
        mRecyclerViewAdapter.setFooterView(footer);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
        long firstTimeMilliSeconds = sharedPreferences.getLong("firstTime", System.currentTimeMillis());
        firstTime = new Date(firstTimeMilliSeconds);
        TextView textFooter = (TextView)footer.findViewById(R.id.text);
        textFooter.setText(TimeUtils.getTimeYMD(firstTime)+"\n你开启了记账之旅");

        initRecyclerViewData();

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //RecyclerView.canScrollVertically(1) 的值表示 从底部看是否有Y偏移
                //RecyclerView.canScrollVertically(-1) 的值表示 从顶部看是否有Y偏移
                isRecyclerViewTop = !recyclerView.canScrollVertically(-1);
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int firstVisiblesItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisiblesItemPosition > visibleItemCount && dy > 0) {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                }
                if (firstVisiblesItemPosition < visibleItemCount && dy < 0) {
                    mFloatingActionButton.setVisibility(View.INVISIBLE);
                }

//                int month = 0;
                if (mRecyclerViewAdapter.getmDatas("shouru").size() > 0
                        && mRecyclerViewAdapter.getmDatas("shouru").get(firstVisiblesItemPosition).equals("")
                        && mRecyclerViewAdapter.getmDatas("zhichu").get(firstVisiblesItemPosition).equals("")) {
                    int month = (int) mRecyclerViewAdapter.getmDatas("iconItem").get(firstVisiblesItemPosition);
//                }
//                else{
//                    month = 0;
//                }
                    if (!(month + "").equals(txtShouru.getText().toString().substring(0, 1))) {
                        Log.i(firstVisiblesItemPosition + "****", txtShouru.getText().toString().substring(0, 1) + "====" + month);
                        AccountRecordService accountRecordService = new AccountRecordService();
                        String[] money = accountRecordService.getMonthMoneyByTime(month, 0);
                        txtShouru.setText(month + "月收入\n" + money[0]);
                        txtZhichu.setText(month + "月支出\n" + money[1]);
                    }
                }
            }
        });

        return view;
    }

    public void initRecyclerViewData(){
        List<AccountRecord> records;
        AccountRecordService accountRecordService = new AccountRecordService();
        Date presentTime = new Date();

        int indexMaxDay = TimeUtils.getTime(presentTime,TimeUtils.DAY);//今天是几号
        boolean isShowMonth = false;
        for(int i = indexMaxDay; i >= 1; i--){
            long dayFirstMilliSeconds = TimeUtils.getDayFirstMilliSeconds(i,0,0);
            long dayLastMilliSeconds = TimeUtils.getDayLastMilliSeconds(i,0,0);
            records = accountRecordService.getAccountRecordListByTime(dayFirstMilliSeconds,dayLastMilliSeconds);

            if(records.size()>0) {//这一天有记录
                String[] money = accountRecordService.getDayMoneyByRecords(records);
                AccountRecord recordDay = new AccountRecord();
                recordDay.setRecordname("DAY");
                recordDay.setMoney(money[0]);
                recordDay.setRemark(money[1]);
                recordDay.setIcon(i);
                records.add(0, recordDay);

                if(!isShowMonth){ //是否插入过月份头
                    isShowMonth = true;
                    int month = TimeUtils.getTime(new Date(dayFirstMilliSeconds),TimeUtils.MONTH);
                    AccountRecord recordMonth = new AccountRecord();
                    recordMonth.setRecordname("MONTH");
                    recordMonth.setIcon(month);
                    records.add(0, recordMonth);
                }
            }
            mRecyclerViewAdapter.addItemList(mRecyclerViewAdapter.getmDatas("shouru").size(), records);
        }
        indexMaxDay = TimeUtils.getMaxDay(presentTime, -1, 0);//上个月有多少天
        isShowMonth = false;
        for(int i = indexMaxDay; i >= 1; i--){
            long dayFirstMilliSeconds = TimeUtils.getDayFirstMilliSeconds(i,-1,0);
            long dayLastMilliSeconds = TimeUtils.getDayLastMilliSeconds(i,-1,0);
            records = accountRecordService.getAccountRecordListByTime(dayFirstMilliSeconds,dayLastMilliSeconds);

            if(records.size()>0) {
                String[] money = accountRecordService.getDayMoneyByRecords(records);
                AccountRecord recordDay = new AccountRecord();
                recordDay.setRecordname("DAY");
                recordDay.setMoney(money[0]);
                recordDay.setRemark(money[1]);
                recordDay.setIcon(i);
                records.add(0, recordDay);

                if(!isShowMonth){ //是否插入过月份头
                    isShowMonth = true;
                    int month = TimeUtils.getTime(new Date(dayFirstMilliSeconds),TimeUtils.MONTH);
                    AccountRecord recordMonth = new AccountRecord();
                    recordMonth.setRecordname("MONTH");
                    recordMonth.setIcon(month);
                    records.add(0, recordMonth);
                }
            }
            mRecyclerViewAdapter.addItemList(mRecyclerViewAdapter.getmDatas("shouru").size(), records);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case 1:
                if(resultCode == getActivity().RESULT_OK){
                    AccountRecord record = (AccountRecord)intent.getSerializableExtra("data");
                    record.setRemark("");
                    myApplication app = myApplication.getApplication();
                    record.setAccount(app.getAccounts().get(1));
                    record.setAccountbook(app.getAccountBooks().get(1));
                    record.setRecordtime(TimeUtils.getDayFirstMilliSeconds(3,0,0));//添加测试数据
//                    record.setRecordtime(new Date().getTime());
                    AccountRecordService accountRecordService = new AccountRecordService();
                    accountRecordService.addAccountRecord(record);

//                    if(mRecyclerViewAdapter.getmDatas("shouru").get(0).equals("") && mRecyclerViewAdapter.getmDatas("zhichu").get(0).equals("")){

                    List<AccountRecord> records = new ArrayList<AccountRecord>();
                    AccountRecord recordMonth = new AccountRecord();
                    recordMonth.setRecordname("MONTH");
                    recordMonth.setIcon(TimeUtils.getTime(new Date(),TimeUtils.MONTH));
                    AccountRecord recordDay = new AccountRecord();
                    recordDay.setRecordname("DAY");
                    if(Double.parseDouble(record.getMoney())>0) {
                        recordDay.setMoney(record.getMoney());
                        recordDay.setRemark("0.00");
                    }
                    else{
                        recordDay.setMoney("0.00");
                        recordDay.setRemark(record.getMoney().substring(1,record.getMoney().length()));
                    }
                    recordDay.setIcon(TimeUtils.getTime(new Date(),TimeUtils.DAY));

                    if(mRecyclerViewAdapter.getmDatas("iconItem").size()>0){
                        int month = (int)mRecyclerViewAdapter.getmDatas("iconItem").get(0);
                        int day = (int)mRecyclerViewAdapter.getmDatas("iconItem").get(1);
//                        Log.i(month+"***"+day, TimeUtils.getTime(new Date(),TimeUtils.MONTH)+"==="+TimeUtils.getTime(new Date(),TimeUtils.DAY)+"****"+record.getMoney());

                        if(TimeUtils.getTime(new Date(),TimeUtils.MONTH) <= month){ //是否跨月添加
                            if(TimeUtils.getTime(new Date(),TimeUtils.DAY) <= day){ //是否跨日添加
                                mRecyclerViewAdapter.addItem(2,record);
                                mRecyclerViewAdapter.updateItemMoney(1,record.getMoney());
                            }
                            else{//跨日添加
                                records.add(recordDay);
                                records.add(record);
                                mRecyclerViewAdapter.addItemList(1,records);
                            }
                        }
                        else{//跨月添加
                            records.add(recordMonth);
                            records.add(recordDay);
                            records.add(record);
                            mRecyclerViewAdapter.addItemList(0,records);
                        }
                    }
                    else{//第一次启动，没有添加月和日
                        records.add(recordMonth);
                        records.add(recordDay);
                        records.add(record);
                        mRecyclerViewAdapter.addItemList(0,records);
                    }
                    int month = TimeUtils.getTime(new Date(),TimeUtils.MONTH);
                    accountRecordService = new AccountRecordService();
                    String[] money = accountRecordService.getMonthMoneyByTime(month,0);
                    txtShouru.setText(month+"月收入\n"+money[0]);
                    txtZhichu.setText(month+"月支出\n"+money[1]);
                }
                break;
        }
    }


}

class MingXiRecyclerAdapter extends BaseRecyclerViewAdapter{
//    private boolean isFirstBindBottomItem = true;//是否第一次绑定底部的View
    private final static int TYPE_DAY = 3;
    private final static int TYPE_MONTH = 4;

    public MingXiRecyclerAdapter(Context context){
        super(context);
        mDatas.put("shouru",new ArrayList());
        mDatas.put("zhichu",new ArrayList());
        mDatas.put("iconItem",new ArrayList());
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView != null && position == 0) return TYPE_HEADER;
        if(mFooterView != null && position == getItemCount() - 1)return TYPE_FOOTER;

        int pos;
        if(mHeaderView == null) pos = position;
        else pos = position - 1;
        if(mDatas.get("shouru").get(pos).equals("") && mDatas.get("zhichu").get(pos).equals(""))
            return TYPE_MONTH;
        if(!mDatas.get("shouru").get(pos).equals("") && !mDatas.get("zhichu").get(pos).equals(""))
            return TYPE_DAY;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) return new Holder(mHeaderView, viewType);
        if(viewType == TYPE_FOOTER) return new Holder(mFooterView, viewType);
        if(viewType == TYPE_DAY) {
            View layout = mInflater.inflate(R.layout.item_recycler_mingxi_day, parent, false);
            return new Holder(layout, viewType);
        }
        if(viewType == TYPE_MONTH) {
            View layout = mInflater.inflate(R.layout.item_recycler_mingxi_month, parent, false);
            return new Holder(layout, viewType);
        }
        View layout = mInflater.inflate(mCreateViewLayout, parent, false);
        return new Holder(layout, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) return;
        Holder holder = (Holder)viewHolder;
        final int pos = getRealPosition(holder);

        if(getItemViewType(position) == TYPE_NORMAL) {
            holder.item_left.setText((String) (mDatas.get("shouru").get(pos)));
            holder.item_right.setText((String) (mDatas.get("zhichu").get(pos)));
            holder.item_icon.setBackgroundResource((int) (mDatas.get("iconItem").get(pos)));
            if (mOnItemClickListener != null) { //点击项目删除/编辑
                holder.item_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onClick(v, pos);
                    }
                });
            }
        }
        if(getItemViewType(position) == TYPE_DAY) {
            holder.item_left.setText((String) (mDatas.get("shouru").get(pos)));
            holder.item_right.setText((String) (mDatas.get("zhichu").get(pos)));
            holder.day_text.setText(mDatas.get("iconItem").get(pos)+"日");
        }
        if(getItemViewType(position) == TYPE_MONTH) {
            holder.day_text.setText(mDatas.get("iconItem").get(pos)+"月");
        }
//        if (position == getItemCount() - 1) {//修改最后一项的背景线长度
//            if (isFirstBindBottomItem) {
//                isFirstBindBottomItem = false;
//                ViewGroup.LayoutParams params = holder.item_icon.getLayoutParams();
//                ViewGroup.LayoutParams params2 = holder.line.getLayoutParams();
//                params2.height = (params2.height - params.width) / 2;
//            }
//        }
    }

    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null) return  mDatas.get("shouru").size();
        if(mHeaderView != null && mFooterView != null) return  mDatas.get("shouru").size()+2;
        return mDatas.get("shouru").size()+1;
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView item_left;
        public TextView item_right;
        public ImageView item_icon;
        public TextView day_text;
        public Holder(View view, int viewType) {
            super(view);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_left = (TextView) view.findViewById(R.id.item_left);
                item_right = (TextView) view.findViewById(R.id.item_right);
                item_icon = (ImageView) view.findViewById(R.id.item_icon);
            }
            if(viewType == TYPE_DAY) {
                item_left = (TextView) view.findViewById(R.id.item_left);
                item_right = (TextView) view.findViewById(R.id.item_right);
                day_text = (TextView) view.findViewById(R.id.text);
            }
            if(viewType == TYPE_MONTH) {
                day_text = (TextView) view.findViewById(R.id.text);
            }
        }
    }

    //添加数据
    public void addItem(int position, AccountRecord data) {
        BigDecimal num = new BigDecimal(data.getMoney());
        Log.i(mDatas.get("shouru").size()+"addItemList: ",mDatas.get("zhichu").size()+"===="+mDatas.get("iconItem").size());
        if(num.doubleValue() > 0){//收入
            mDatas.get("shouru").add(position, num+" "+data.getRecordname());
            mDatas.get("zhichu").add(position, "");
            mDatas.get("iconItem").add(position, data.getIcon());
        }
        else{
            mDatas.get("shouru").add(position, "");
            mDatas.get("zhichu").add(position, data.getRecordname()+" "+num.negate());//num的负数
            mDatas.get("iconItem").add(position, data.getIcon());
        }
        Log.i(mDatas.get("shouru").size()+"addItemList: ",mDatas.get("zhichu").size()+"!!!!!"+mDatas.get("iconItem").size());
        notifyItemInserted(mHeaderView==null?position:position+1);
    }

    //更改数据
    public void updateItemMoney(int position, String Money) {
        BigDecimal num = new BigDecimal(Money);
        if(num.doubleValue() > 0){//收入
            String text = mDatas.get("shouru").get(position).toString();
            num = num.add(new BigDecimal(text.substring(0,text.length()-3)));
            mDatas.get("shouru").set(position, num+" "+"收入");
        }
        else{
            String text = mDatas.get("zhichu").get(position).toString();
            num = num.negate().add(new BigDecimal(text.substring(3)));
            mDatas.get("zhichu").set(position, "支出"+" "+num);
        }
        notifyItemChanged(mHeaderView==null?position:position+1);
    }

    public void addItemList(int position, List<AccountRecord> records) {
        for(int i = 0; i < records.size(); i++) {
            if(records.get(i).getRecordname().equals("MONTH")){
                mDatas.get("shouru").add(position + i,"");
                mDatas.get("zhichu").add(position + i,"");
                mDatas.get("iconItem").add(position + i,records.get(i).getIcon());
            }
            else if(records.get(i).getRecordname().equals("DAY")){
                mDatas.get("shouru").add(position + i,records.get(i).getMoney()+" "+"收入");
                mDatas.get("zhichu").add(position + i,"支出"+" "+records.get(i).getRemark());
                mDatas.get("iconItem").add(position + i,records.get(i).getIcon());
            }
            else {
                BigDecimal num = new BigDecimal(records.get(i).getMoney());
                if (num.doubleValue() > 0) {//收入
                    mDatas.get("shouru").add(position + i, num + " " + records.get(i).getRecordname());
                    mDatas.get("zhichu").add(position + i, "");
                    mDatas.get("iconItem").add(position + i, records.get(i).getIcon());
                } else {
                    mDatas.get("shouru").add(position + i, "");
                    mDatas.get("zhichu").add(position + i, records.get(i).getRecordname() + " " + num.negate());//num的负数
                    mDatas.get("iconItem").add(position + i, records.get(i).getIcon());
                }
            }
        }
        notifyItemRangeInserted(mHeaderView==null?position:position+1,records.size()); //刷新整个界面
    }

    //删除数据
    public void removeItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(mHeaderView==null?position:position+1);
    }
}
