package com.swjtu.huxin.accountmanagement.fragment;

/**
 * Created by huxin on 2017/2/24.
 */

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.activity.BudgetActivity;
import com.swjtu.huxin.accountmanagement.adapter.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.adapter.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.application.MyApplication;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.activity.AddItemActivity;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;
import com.swjtu.huxin.accountmanagement.view.CustomPtrHeader;
import com.swjtu.huxin.accountmanagement.view.WaveProgressView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class DetailFragment extends Fragment {

    private String mArgument;
    public static final String ARGUMENT = "argument";
    private boolean isRecyclerViewTop = true;
    private Date firstTime;

    private PtrFrameLayout mPtrFrameLayout;
    private WaveProgressView wpv;
    private TextView txtZhichu;
    private TextView txtShouru;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DetailRecyclerAdapter mRecyclerViewAdapter;
    private CustomPtrHeader mHeaderView;
    private FloatingActionButton mFloatingActionButton;
    private boolean isBudget;
    private BigDecimal totalMoney;
    private BigDecimal remainingMoney;
    private long nowHeaderTime = 0;
    private int lastOffset;
    private int lastPosition;
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
    public static DetailFragment newInstance(String argument) {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        DetailFragment contentFragment = new DetailFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
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


        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerViewAdapter.hideDeleteAndEidt();
                Intent intent = new Intent(getActivity(),AddItemActivity.class);
                intent.putExtra("isFloatingActionButton",true);
                startActivityForResult(intent,1, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "sharedView").toBundle());
            }
        });
        mFloatingActionButton.setVisibility(View.INVISIBLE);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(getContext());
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);
        //创建适配器，并且设置
        mRecyclerViewAdapter = new DetailRecyclerAdapter(getContext());

        View header = inflater.inflate(R.layout.item_recycler_detail_header, container, false);
        mRecyclerViewAdapter.setHeaderView(header);
        ImageView imgHeader = (ImageView)header.findViewById(R.id.item_icon);
        imgHeader.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mRecyclerViewAdapter.hideDeleteAndEidt();
                Intent intent = new Intent(getActivity(),AddItemActivity.class);
                //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "sharedView").toBundle());
                startActivityForResult(intent,1, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "sharedView").toBundle());
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
        View footer = inflater.inflate(R.layout.item_recycler_detail_footer, container, false);
        mRecyclerViewAdapter.setFooterView(footer);
        long firstTimeMilliSeconds = sharedPreferences.getLong("firstTime", System.currentTimeMillis());
        firstTime = new Date(firstTimeMilliSeconds);
        TextView textFooter = (TextView)footer.findViewById(R.id.text);
        textFooter.setText(new SimpleDateFormat("yyyy年MM月dd日").format(firstTime)+"\n你开启了记账之旅");

        initRecyclerViewData();//初始化列表数据

        txtShouru = (TextView) view.findViewById(R.id.shouru);
        txtZhichu = (TextView) view.findViewById(R.id.zhichu);
        if(nowHeaderTime != 0) {
            int headerMonth = TimeUtils.getTime(new Date(nowHeaderTime), TimeUtils.MONTH);
            int headerYear = TimeUtils.getTime(new Date(nowHeaderTime), TimeUtils.YEAR);
            AccountRecordService accountRecordService = new AccountRecordService();
            String[] money = accountRecordService.getMonthMoneyByTime(headerMonth, headerYear - TimeUtils.getTime(new Date(), TimeUtils.YEAR));
            txtShouru.setText(headerMonth + "月收入\n" + money[0]);
            txtZhichu.setText(headerMonth + "月支出\n" + money[1]);
        }
        else {
            txtShouru.setText(TimeUtils.getTime(new Date(),TimeUtils.MONTH) + "月收入\n0.00");
            txtZhichu.setText(TimeUtils.getTime(new Date(),TimeUtils.MONTH) + "月支出\n0.00");
        }

        AccountRecordService accountRecordService = new AccountRecordService();
        String[] nowMoney = accountRecordService.getMonthMoneyByTime(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0);
        totalMoney = new BigDecimal(sharedPreferences.getString("totalMoney","3000.00"));
        remainingMoney = totalMoney.subtract(new BigDecimal(nowMoney[1]));
        wpv = (WaveProgressView) view.findViewById(R.id.wpv);
        isBudget = sharedPreferences.getBoolean("isBudget",true);
        if(isBudget) {
            wpv.setMaxProgress(totalMoney.floatValue());
            wpv.setProgress(remainingMoney.floatValue());
        }
        else {
            wpv.setText(TimeUtils.getTime(new Date(),TimeUtils.MONTH)+"月");
            wpv.setMaxProgress(remainingMoney.floatValue());
            wpv.setProgress(remainingMoney.floatValue());
        }
        wpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerViewAdapter.hideDeleteAndEidt();
                Intent intent = new Intent(getActivity(),BudgetActivity.class);
                intent.putExtra("totalMoney",totalMoney);
                intent.putExtra("remainingMoney",remainingMoney);
                startActivityForResult(intent,3);
            }
        });

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果

        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos,String viewName) {
                mRecyclerViewAdapter.hideDeleteAndEidt();
                if("item_edit".equals(viewName)) {
                    Intent intent = new Intent(getActivity(), AddItemActivity.class);
                    intent.putExtra("edit", ((AccountRecord) mRecyclerViewAdapter.getDatas("records").get(pos)));
                    startActivityForResult(intent, 2);
                }
                if("item_delete".equals(viewName)){
                    AccountRecord removedRecord = mRecyclerViewAdapter.removeItem(pos);
                    AccountRecordService accountRecordService = new AccountRecordService();
                    accountRecordService.removeAccountRecord(removedRecord);

                    int day = TimeUtils.getTime(new Date(removedRecord.getRecordtime()),TimeUtils.DAY);
                    int month = TimeUtils.getTime(new Date(removedRecord.getRecordtime()),TimeUtils.MONTH);
                    int year = TimeUtils.getTime(new Date(removedRecord.getRecordtime()),TimeUtils.YEAR);
                    String[] Daymoney = accountRecordService.getDayMoneyByTime(day,month - TimeUtils.getTime(new Date(),TimeUtils.MONTH),year - TimeUtils.getTime(new Date(),TimeUtils.YEAR));
                    String[] Monthmoney = accountRecordService.getMonthMoneyByTime(month,year - TimeUtils.getTime(new Date(),TimeUtils.YEAR));
                    if(Daymoney[0].equals("0.00")&& Daymoney[1].equals("0.00")) {
                        mRecyclerViewAdapter.removeItem(pos - 1);
                    }
                    else{//该日还有其它记录
                        int i;
                        for(i = 1; i < pos; i++){
                            if("DAY".equals(((AccountRecord)mRecyclerViewAdapter.getDatas("records").get(pos - i)).getRecordname()))
                                break;
                        }
                        mRecyclerViewAdapter.updateItemMoney(pos - i,removedRecord.getMoney(),false);
                    }
                    if(Monthmoney[0].equals("0.00")&& Monthmoney[1].equals("0.00")) {
                        mRecyclerViewAdapter.removeItem(pos - 2);
                    }
                    updateHeader();
                    updateWAV();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.getLayoutManager() != null) {
                    getPositionAndOffset();
                }
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
                if (firstVisiblesItemPosition > visibleItemCount/2 && dy > 0) {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                }
                if (firstVisiblesItemPosition < visibleItemCount/2 && dy < 0) {
                    mFloatingActionButton.setVisibility(View.INVISIBLE);
                }

                if (mRecyclerViewAdapter.getDatas("records").size() > 0
                        && "MONTH".equals(((AccountRecord)mRecyclerViewAdapter.getDatas("records").get(firstVisiblesItemPosition)).getRecordname()) ) {
                    int headerMonth = ((AccountRecord)mRecyclerViewAdapter.getDatas("records").get(firstVisiblesItemPosition)).getId();
                    int headerYear = TimeUtils.getTime(new Date(((AccountRecord)mRecyclerViewAdapter.getDatas("records").get(firstVisiblesItemPosition)).getRecordtime()),TimeUtils.YEAR);
                    if (!(headerMonth + "").equals(txtShouru.getText().toString().substring(0,1))) {
                        AccountRecordService accountRecordService = new AccountRecordService();
                        String[] money = accountRecordService.getMonthMoneyByTime(headerMonth, headerYear - TimeUtils.getTime(new Date(),TimeUtils.YEAR));
                        txtShouru.setText(headerMonth + "月收入\n" + money[0]);
                        txtZhichu.setText(headerMonth + "月支出\n" + money[1]);
                        nowHeaderTime = TimeUtils.getMonthFirstMilliSeconds(headerMonth,headerYear);
                    }
                }
            }
        });
        return view;
    }

    private void initRecyclerViewData(){
        List<AccountRecord> records;
        AccountRecordService accountRecordService = new AccountRecordService();
        Date now = new Date();

        int indexMaxDay = TimeUtils.getTime(now,TimeUtils.DAY);//今天是几号
        boolean isShowMonth = false;
        for(int i = indexMaxDay; i >= 1; i--){
            long dayFirstMilliSeconds = TimeUtils.getDayFirstMilliSeconds(i,0,0);
            long dayLastMilliSeconds = TimeUtils.getDayLastMilliSeconds(i,0,0);
            records = accountRecordService.getAccountRecordListByTime(dayFirstMilliSeconds,dayLastMilliSeconds,null,null);

            if(records.size()>0) {//这一天有记录
                String[] money = accountRecordService.getDayMoneyByRecords(records);
                AccountRecord recordDay = new AccountRecord();
                recordDay.setRecordname("DAY");
                recordDay.setMoney(money[0]);
                recordDay.setRemark(money[1]);
                recordDay.setId(i);
                records.add(0, recordDay);

                if(!isShowMonth){ //是否插入过月份头
                    isShowMonth = true;
                    int month = TimeUtils.getTime(new Date(dayFirstMilliSeconds),TimeUtils.MONTH);
                    AccountRecord recordMonth = new AccountRecord();
                    recordMonth.setRecordname("MONTH");
                    recordMonth.setId(month);
                    recordMonth.setRecordtime(dayFirstMilliSeconds);
                    nowHeaderTime = dayFirstMilliSeconds;
                    records.add(0, recordMonth);
                }
            }
            mRecyclerViewAdapter.addItemList(mRecyclerViewAdapter.getDatas("records").size(), records);
        }
        indexMaxDay = TimeUtils.getMaxDay(now, -1,0);//上个月有多少天
        isShowMonth = false;
        for(int i = indexMaxDay; i >= 1; i--){
            long dayFirstMilliSeconds = TimeUtils.getDayFirstMilliSeconds(i,-1,0);
            long dayLastMilliSeconds = TimeUtils.getDayLastMilliSeconds(i,-1,0);
            records = accountRecordService.getAccountRecordListByTime(dayFirstMilliSeconds,dayLastMilliSeconds,null,null);

            if(records.size()>0) {
                String[] money = accountRecordService.getDayMoneyByRecords(records);
                AccountRecord recordDay = new AccountRecord();
                recordDay.setRecordname("DAY");
                recordDay.setMoney(money[0]);
                recordDay.setRemark(money[1]);
                recordDay.setId(i);
                records.add(0, recordDay);

                if(!isShowMonth){ //是否插入过月份头
                    isShowMonth = true;
                    int month = TimeUtils.getTime(new Date(dayFirstMilliSeconds),TimeUtils.MONTH);
                    AccountRecord recordMonth = new AccountRecord();
                    recordMonth.setRecordname("MONTH");
                    recordMonth.setId(month);
                    recordMonth.setRecordtime(dayFirstMilliSeconds);
                    if(nowHeaderTime == 0)nowHeaderTime = dayFirstMilliSeconds;
                    records.add(0, recordMonth);
                }
            }
            mRecyclerViewAdapter.addItemList(mRecyclerViewAdapter.getDatas("records").size(), records);
        }
    }

    /**
     * 记录RecyclerView当前位置
     */
    private void getPositionAndOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        //获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if(topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = layoutManager.getPosition(topView);
        }
    }

    /**
     * 让RecyclerView滚动到指定位置
     */
    private void scrollToPosition() {
        if(mRecyclerView.getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }

    private void updateWAV(){
        if(!isBudget) return;
        AccountRecordService accountRecordService = new AccountRecordService();
        String[] nowMoney = accountRecordService.getMonthMoneyByTime(TimeUtils.getTime(new Date(),TimeUtils.MONTH),0);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(wpv, "progress", remainingMoney.floatValue(),
                totalMoney.subtract(new BigDecimal(nowMoney[1])).floatValue());
        remainingMoney = totalMoney.subtract(new BigDecimal(nowMoney[1]));
        objectAnimator.setDuration(2000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    private void updateHeader(){
        int headerMonth = Integer.parseInt(txtShouru.getText().toString().substring(0,1));
        int headerYear = TimeUtils.getTime(new Date(nowHeaderTime),TimeUtils.YEAR);
        AccountRecordService accountRecordService = new AccountRecordService();
        String[] money = accountRecordService.getMonthMoneyByTime(headerMonth,headerYear - TimeUtils.getTime(new Date(),TimeUtils.YEAR));
        txtShouru.setText(headerMonth+"月收入\n"+money[0]);
        txtZhichu.setText(headerMonth+"月支出\n"+money[1]);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case 1://添加item
                if(resultCode == getActivity().RESULT_OK){
                    boolean isFloatingActionButton = intent.getBooleanExtra("isFloatingActionButton",false);
                    if(isFloatingActionButton) mRecyclerView.scrollToPosition(0);
                    AccountRecord record = (AccountRecord)intent.getSerializableExtra("data");
                    AccountRecordService accountRecordService = new AccountRecordService();

                    Date recordTime = new Date(record.getRecordtime());
                    int day = TimeUtils.getTime(recordTime,TimeUtils.DAY);
                    int month = TimeUtils.getTime(recordTime,TimeUtils.MONTH);
                    int year = TimeUtils.getTime(recordTime,TimeUtils.YEAR);
                    String[] Daymoney = accountRecordService.getDayMoneyByTime(day,month - TimeUtils.getTime(new Date(),TimeUtils.MONTH),year - TimeUtils.getTime(new Date(),TimeUtils.YEAR));
                    String[] Monthmoney = accountRecordService.getMonthMoneyByTime(month,year - TimeUtils.getTime(new Date(),TimeUtils.YEAR));

                    int id = (int)accountRecordService.addAccountRecord(record);
                    record.setId(id);

                    List<AccountRecord> records = new ArrayList<AccountRecord>();
                    AccountRecord recordMonth = new AccountRecord();
                    recordMonth.setRecordname("MONTH");
                    recordMonth.setId(TimeUtils.getTime(recordTime,TimeUtils.MONTH));
                    recordMonth.setRecordtime(record.getRecordtime());
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
                    recordDay.setId(TimeUtils.getTime(recordTime,TimeUtils.DAY));

                    if(mRecyclerViewAdapter.getDatas("records").size()>0){//不是第一次启动
                        Date firstDate = new Date(((AccountRecord)mRecyclerViewAdapter.getDatas("records").get(0)).getRecordtime());
                        if(TimeUtils.getTimeDistance(firstDate,recordTime) >=0 ) {//修改日期过多
                            if (Daymoney[0].equals("0.00") && Daymoney[1].equals("0.00")) {
                                if (Monthmoney[0].equals("0.00") && Monthmoney[1].equals("0.00")) {//跨月添加
                                    records.add(recordMonth);
                                    records.add(recordDay);
                                    records.add(record);
                                    mRecyclerViewAdapter.addItemList(0, records);
                                } else {//跨日添加
                                    records.add(recordDay);
                                    records.add(record);
                                    mRecyclerViewAdapter.addItemList(1, records);
                                }
                            } else {//没有跨日
                                mRecyclerViewAdapter.addItem(2, record);
                                mRecyclerViewAdapter.updateItemMoney(1, record.getMoney(),true);
                            }
                        }
                        else{
                            mRecyclerViewAdapter.addDatas("records",new ArrayList<AccountRecord>());//清空数据
                            initRecyclerViewData();//重新写入
                            mRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                    else{//第一次启动，没有添加月和日
                        records.add(recordMonth);
                        records.add(recordDay);
                        records.add(record);
                        mRecyclerViewAdapter.addItemList(0,records);
                    }

                    updateHeader();
                    updateWAV();
                }
                break;
            case 2://编辑Item
                if(resultCode == getActivity().RESULT_OK){
                    AccountRecord record = (AccountRecord)intent.getSerializableExtra("data");
                    AccountRecordService accountRecordService = new AccountRecordService();
                    accountRecordService.updateAccountRecord(record);
                    mRecyclerViewAdapter.addDatas("records",new ArrayList<AccountRecord>());//清空数据
                    initRecyclerViewData();//重新写入
                    mRecyclerViewAdapter.notifyDataSetChanged();

                    updateHeader();
                    updateWAV();
                    scrollToPosition();
                }
                break;
            case 3://设置预算
                if(resultCode == getActivity().RESULT_OK) {
                    totalMoney = (BigDecimal)intent.getSerializableExtra("totalMoney");
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);
                    isBudget = sharedPreferences.getBoolean("isBudget",true);
                    if(isBudget) {
                        wpv.setText(null);
                        wpv.setMaxProgress(totalMoney.floatValue());
                        updateWAV();
                    }
                    else {
                        wpv.setText(TimeUtils.getTime(new Date(),TimeUtils.MONTH)+"月");
                        wpv.setMaxProgress(remainingMoney.floatValue());
                        wpv.setProgress(remainingMoney.floatValue());
                    }
                }
                break;
        }
    }
}

class DetailRecyclerAdapter extends BaseRecyclerViewAdapter{
//    private boolean isFirstBindBottomItem = true;//是否第一次绑定底部的View
    public final static int TYPE_DAY = 3;
    public final static int TYPE_MONTH = 4;
    private boolean isDeleteAndEidtShow = false;
    private View delete;
    private View edit;
    private Context mContext;

    public DetailRecyclerAdapter(Context context){
        super(context);
        mContext = context;
        mDatas.put("records",new ArrayList<AccountRecord>());
    }

    @Override
    public int getItemViewType(int position) {
        if(mHeaderView != null && position == 0) return TYPE_HEADER;
        if(mFooterView != null && position == getItemCount() - 1)return TYPE_FOOTER;

        int pos = mHeaderView == null ? position : position - 1;
        if("MONTH".equals(((AccountRecord)mDatas.get("records").get(pos)).getRecordname()))
            return TYPE_MONTH;
        if("DAY".equals(((AccountRecord)mDatas.get("records").get(pos)).getRecordname()))
            return TYPE_DAY;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) return new Holder(mHeaderView, viewType);
        if(viewType == TYPE_FOOTER) return new Holder(mFooterView, viewType);
        if(viewType == TYPE_DAY) {
            View layout = mInflater.inflate(R.layout.item_recycler_detail_day, parent, false);
            return new Holder(layout, viewType);
        }
        if(viewType == TYPE_MONTH) {
            View layout = mInflater.inflate(R.layout.item_recycler_detail_month, parent, false);
            return new Holder(layout, viewType);
        }
        View layout = mInflater.inflate(R.layout.item_recycler_detail, parent, false);
        return new Holder(layout, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final Holder holder = (Holder)viewHolder;
        final int pos = getRealPosition(holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeleteAndEidtShow) {
                    MyApplication app = MyApplication.getApplication();
                    setAnimator(delete, app.getScreenWidth() / 2 * -0.8f, 0, 0, 90, -1);
                    setAnimator(edit, app.getScreenWidth() / 2 * 0.8f, 0, 0, -90, 1);
                    isDeleteAndEidtShow = false;
                }
            }
        });

        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) return;
        if(getItemViewType(position) == TYPE_NORMAL) {
            BigDecimal num = new BigDecimal(((AccountRecord) mDatas.get("records").get(pos)).getMoney());
            if (num.doubleValue() > 0) {//收入
                holder.item_left.setText(num + " " + ((AccountRecord) mDatas.get("records").get(pos)).getRecordname());
                holder.item_right.setText("");
            } else {
                holder.item_left.setText("");
                holder.item_right.setText(((AccountRecord) mDatas.get("records").get(pos)).getRecordname() + " " + num.negate());
            }
            int resID = mContent.getResources().getIdentifier(((AccountRecord) mDatas.get("records").get(pos)).getIcon(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
            holder.item_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext, "pos="+pos+" position="+position, Toast.LENGTH_SHORT).show();
                    MyApplication app = MyApplication.getApplication();
                    if (!isDeleteAndEidtShow) {
                        setAnimator(holder.item_delete, 0, app.getScreenWidth() / 2 * -0.8f, 90, 0, -1);
                        setAnimator(holder.item_edit, 0, app.getScreenWidth() / 2 * 0.8f, -90, 0, 1);
                        delete = holder.item_delete;
                        edit = holder.item_edit;
                        isDeleteAndEidtShow = true;
                    }
                    else{
                        setAnimator(delete, app.getScreenWidth() / 2 * -0.8f, 0, 0, 90, -1);
                        setAnimator(edit, app.getScreenWidth() / 2 * 0.8f, 0, 0, -90, 1);

                        if(delete != holder.item_delete) {
                            setAnimator(holder.item_delete, 0, app.getScreenWidth() / 2 * -0.8f, 90, 0, -1);
                            setAnimator(holder.item_edit, 0, app.getScreenWidth() / 2 * 0.8f, -90, 0, 1);
                            delete = holder.item_delete;
                            edit = holder.item_edit;
                            isDeleteAndEidtShow = true;
                        }
                        else{
                            isDeleteAndEidtShow = false;
                        }
                    }
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
            holder.item_left.setText(((AccountRecord)mDatas.get("records").get(pos)).getMoney()+" 收入");
            holder.item_right.setText("支出 "+((AccountRecord)mDatas.get("records").get(pos)).getRemark());
            holder.day_text.setText(((AccountRecord)mDatas.get("records").get(pos)).getId()+"日");
        }
        if(getItemViewType(position) == TYPE_MONTH) {
            holder.day_text.setText(((AccountRecord)mDatas.get("records").get(pos)).getId()+"月");
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

    public void hideDeleteAndEidt(){
        if (isDeleteAndEidtShow) {
            MyApplication app = MyApplication.getApplication();
            setAnimator(delete, app.getScreenWidth() / 2 * -0.8f, 0, 0, 90, -1);
            setAnimator(edit, app.getScreenWidth() / 2 * 0.8f, 0, 0, -90, 1);
            isDeleteAndEidtShow = false;
        }
    }

    private void setAnimator(View view, float XFrom, float XTo, float rotationFrom, float rotationTo, float direction){
        ObjectAnimator move1 = ObjectAnimator.ofFloat(view, "translationX", XFrom, XTo + 20 * direction);
        ObjectAnimator move2 = ObjectAnimator.ofFloat(view, "translationX", XTo + 20 * direction, XTo);
        ObjectAnimator rotate1 = ObjectAnimator.ofFloat(view, "rotation", rotationFrom, rotationTo + 45 * direction);
        ObjectAnimator rotate2 = ObjectAnimator.ofFloat(view, "rotation", rotationTo + 45 * direction, rotationTo);
        AnimatorSet animSet1 = new AnimatorSet();
        AnimatorSet animSet2 = new AnimatorSet();
        animSet1.play(rotate1).with(move1);
        animSet2.play(rotate2).with(move2).after(animSet1);
//        animSet1.setInterpolator(new DecelerateInterpolator());
//        animSet2.setInterpolator(new AccelerateInterpolator());
        animSet1.setDuration(500);
        animSet2.start();
    }

    @Override
    public int getItemCount() {
        if(mHeaderView == null && mFooterView == null) return  mDatas.get("records").size();
        if(mHeaderView != null && mFooterView != null) return  mDatas.get("records").size()+2;
        return mDatas.get("records").size()+1;
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView item_left;
        public TextView item_right;
        public ImageView item_icon;
        public ImageView item_delete;
        public ImageView item_edit;
        public TextView day_text;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                item_left = (TextView) itemView.findViewById(R.id.item_left);
                item_right = (TextView) itemView.findViewById(R.id.item_right);
                item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
                item_delete = (ImageView) itemView.findViewById(R.id.item_delete);
                item_edit = (ImageView) itemView.findViewById(R.id.item_edit);
            }
            if(viewType == TYPE_DAY) {
                item_left = (TextView) itemView.findViewById(R.id.item_left);
                item_right = (TextView) itemView.findViewById(R.id.item_right);
                day_text = (TextView) itemView.findViewById(R.id.text);
            }
            if(viewType == TYPE_MONTH) {
                day_text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }

    //更改钱数
    public void updateItemMoney(int pos, String Money,boolean isAdd) {
        BigDecimal num = new BigDecimal(Money);
        if(num.doubleValue() > 0){//收入
            String text = ((AccountRecord)mDatas.get("records").get(pos)).getMoney();
            if(isAdd)num = num.add(new BigDecimal(text));
            else num = new BigDecimal(text).subtract(num);
            ((AccountRecord)mDatas.get("records").get(pos)).setMoney(num.toString());
        }
        else{
            String text = ((AccountRecord)mDatas.get("records").get(pos)).getRemark();
            if(isAdd)num = num.negate().add(new BigDecimal(text));
            else num = new BigDecimal(text).subtract(num.negate());
            ((AccountRecord)mDatas.get("records").get(pos)).setRemark(num.toString());
        }
        int position = mHeaderView==null?pos:pos+1;
        notifyItemChanged(position);
    }

    //添加数据
    public void addItem(int pos, AccountRecord data) {
        mDatas.get("records").add(pos, data);
        int position = mHeaderView==null?pos:pos+1;
        int itemCount = mDatas.get("records").size() - pos;
        notifyItemInserted(position);//插入动画
        notifyItemRangeChanged(position, itemCount);
    }

    public void addItemList(int pos, List<AccountRecord> records) {
        mDatas.get("records").addAll(pos, records);
        int position = mHeaderView==null?pos:pos+1;
        int itemCount = mDatas.get("records").size() - pos;;
        notifyItemRangeInserted(position,records.size()); //多项插入动画
        notifyItemRangeChanged(position, itemCount);
    }

    //删除数据
    public AccountRecord removeItem(int pos) {
        AccountRecord record = (AccountRecord) mDatas.get("records").remove(pos);
        int position = mHeaderView==null?pos:pos+1;
        int itemCount = mDatas.get("records").size() - pos;
//        Log.i("removeItem: ",pos+"==="+position+"==="+itemCount+"==="+mDatas.get("records").size());
        notifyItemRemoved(position);//删除动画
        notifyItemRangeChanged(position, itemCount);
        return record;
    }
}
