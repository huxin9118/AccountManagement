package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.swjtu.huxin.accountmanagement.adapter.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.application.MyApplication;
import com.swjtu.huxin.accountmanagement.adapter.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.domain.AddItem;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by huxin on 2017/2/25.
 */

public class AddItemActivity extends AppCompatActivity{

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TextView btnShouru;
    private TextView btnZhichu;
    private ImageView btnBack;
    private TextView nameAddItem;
    private ImageView iconAddItem;
    private TextView numAddItem;
    private LinearLayout keyboard;
    private NumKeyboardView numKeyboardView;
    private GridView gridView;
    private Animation enterAnim;
    private Animation exitAnim;
    private ArrayList<String> valueList;

    private Toast mToast;
    private int tabPosition; //当前分页数
    private int indexAddItem = 0; //当前选取的项目数
    private Date timeAddItem;//当前的时间
    private AccountRecord editRecord; //编辑记录
    private SwitchDateTimeDialogFragment dateTimeFragment;

    private TextView btnAccount;
    private TextView btnDate;
    private TextView btnTime;
    private Button btnRemark;
    private Button btnMember;
    public static final String DATEPICKER_TAG = "datepicker";

    private boolean isFloatingActionButton;
//    public String[] nameShouru ={"工资","生活费","红包","零花钱","外快兼职","投资"};
//    public String[] nameZhichu ={"一般","餐饮","交通","酒水饮料","水果","零食"};
//    public int[] iconShouru ={R.drawable.icon_gongzi,R.drawable.icon_shenghuofei,R.drawable.icon_hongbao,
//            R.drawable.icon_linghuaqian,R.drawable.icon_waikuaijianzhi,R.drawable.icon_touzi};
//    public int[] iconZhichu ={R.drawable.icon_yiban,R.drawable.icon_canyin,R.drawable.icon_jiaotong,
//            R.drawable.icon_jiushui,R.drawable.icon_shuiguo,R.drawable.icon_lingshi};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        initAnim();
        setContentView(R.layout.activity_additem);
        initView();
    }

    private void initAnim() {
        enterAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        exitAnim = AnimationUtils.loadAnimation(this, R.anim.push_bottom_out);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.explode);
        //退出时使用
        getWindow().setExitTransition(explode);
        //第一次进入时使用
        getWindow().setEnterTransition(explode);
        //再次进入时使用
        getWindow().setReenterTransition(explode);
    }

    private void initView(){
        btnShouru = (TextView)findViewById(R.id.textShouru);
        btnShouru.setOnClickListener(mTabClickListener);

        btnZhichu = (TextView)findViewById(R.id.textZhichu);
        btnZhichu.setOnClickListener(mTabClickListener);

        btnBack = (ImageView)findViewById(R.id.back);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelToast();
                finishAfterTransition();//带动画的退出
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);


        nameAddItem = (TextView)findViewById(R.id.item_str);
        iconAddItem = (ImageView) findViewById(R.id.item_icon);
        numAddItem = (TextView) findViewById(R.id.item_num);

        keyboard = (LinearLayout)findViewById(R.id.keybord);
        numKeyboardView = (NumKeyboardView) findViewById(R.id.numKeyboardView);
//        numKeyboardView.getLayoutBack().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {//收回键盘
//                keyboard.startAnimation(exitAnim);
//                keyboard.setVisibility(View.GONE);
//            }
//        });

        btnAccount = (TextView) findViewById(R.id.account);
        btnDate = (TextView) findViewById(R.id.date);
        btnTime = (TextView) findViewById(R.id.time);
        btnRemark = (Button) findViewById(R.id.remark);
        btnMember = (Button) findViewById(R.id.member);

        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance("日期时间选择","确定","取消");
        }
        dateTimeFragment.set24HoursMode(true);
//        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
//        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
//        dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
//        try {
//            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MM dd", Locale.getDefault()));
//        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
//            e.printStackTrace();
//        }

        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                timeAddItem = date;
                updateBtnTime(date);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        btnDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        btnTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.startAtTimeView();
                dateTimeFragment.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        valueList = numKeyboardView.getValueList();
        gridView = numKeyboardView.getGridView();
        gridView.setOnItemClickListener(onItemClickListener);
        numAddItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyboard.getVisibility() !=  View.VISIBLE){//是否弹出键盘
                    numKeyboardView.setFocusable(true);
                    numKeyboardView.setFocusableInTouchMode(true);
                    keyboard.setVisibility(View.VISIBLE);
                    keyboard.startAnimation(enterAnim);
                }
                else{
                    keyboard.startAnimation(exitAnim);
                    keyboard.setVisibility(View.GONE);
                }
            }
        });

        Intent intent = getIntent();
        isFloatingActionButton = intent.getBooleanExtra("isFloatingActionButton",false);
        editRecord = (AccountRecord)intent.getSerializableExtra("edit");
        if(editRecord != null){
            nameAddItem.setText(editRecord.getRecordname());
            int resID = getResources().getIdentifier(editRecord.getIcon(), "drawable", getPackageName());
            iconAddItem.setBackgroundResource(resID);
            if(Double.parseDouble(editRecord.getMoney())>0){
                numAddItem.setText(editRecord.getMoney());
                ItemChange(0);
            }
            else{
                numAddItem.setText(editRecord.getMoney().substring(1));
                ItemChange(1);
            }
            timeAddItem = new Date(editRecord.getRecordtime());
            updateBtnTime(timeAddItem);
        }
        else {
            timeAddItem = new Date();
            updateBtnTime(timeAddItem);
            btnZhichu.performClick();//默认点击
        }
    }

    void updateBtnTime(Date time){
        btnDate.setText(new SimpleDateFormat("yy年MM月dd日").format(time));
        btnTime.setText(new SimpleDateFormat("HH:mm").format(time));
        dateTimeFragment.setDefaultDateTime(time);
    }

    private OnClickListener mTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View v){
            MyApplication app = MyApplication.getApplication();
            ArrayList<AddItem> shouruAddItems = app.getShouruAddItems();
            ArrayList<AddItem> zhichuAddItems = app.getZhichuAddItems();
            if (v == btnShouru){
                mViewPager.setCurrentItem(0);
                ItemChange(shouruAddItems.get(0).getNameAddItem(), shouruAddItems.get(0).getIconAddItem(),0);//显示默认item
            }
            else{
                mViewPager.setCurrentItem(1);
                ItemChange(zhichuAddItems.get(0).getNameAddItem(), zhichuAddItems.get(0).getIconAddItem(),1);//显示默认item
            }
        }
    };

    private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        @Override
        //页面跳转完后得到调用
        public void onPageSelected(int arg0){
            MyApplication app = MyApplication.getApplication();
            ArrayList<AddItem> shouruAddItems = app.getShouruAddItems();
            ArrayList<AddItem> zhichuAddItems = app.getZhichuAddItems();
            if(arg0 == 0){
                ItemChange(shouruAddItems.get(0).getNameAddItem(), shouruAddItems.get(0).getIconAddItem(),arg0);//显示默认item
            }
            else{
                ItemChange(zhichuAddItems.get(0).getNameAddItem(), zhichuAddItems.get(0).getIconAddItem(),arg0);//显示默认item
            }
        }
        @Override
        //当页面在滑动的时调用
        //arg0:当前页面，及你点击滑动的页面
        //arg1:当前页面偏移的百分比
        //arg2:当前页面偏移的像素位置
        public void onPageScrolled(int arg0, float arg1, int arg2){}
        @Override
        //状态改变时调用：arg0 ==1正在滑动，arg0==2滑动完毕，arg0==0什么都没做。当页面开始滑动的时候，三种状态的变化顺序为（1，2，0）
        public void onPageScrollStateChanged(int arg0){}
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        private boolean isInteger = true;//是否输入整数
        private int numDecimal = 0;//已输入的小数位数
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String amount = numAddItem.getText().toString().trim();
            if (position < 14 && position != 3 && position != 7 && position != 11 && position != 12) {    //点击0~9按钮
                if(isInteger) {
                    if(amount.charAt(0) == '0'){
                        amount = valueList.get(position) + amount.substring(1);
                    }
                    else{
                        amount = amount.substring(0,amount.length()-3) + valueList.get(position) + amount.substring(amount.length()-3);
                    }
                }
                else{
                    if(numDecimal == 0){
                        amount = amount.substring(0,amount.length()-2) + valueList.get(position) + "0";
                        numDecimal++;
                    }
                    else if(numDecimal == 1){
                        amount = amount.substring(0,amount.length()-1) + valueList.get(position);
                        numDecimal++;
                    }
                }
            }
            else {
                if (position == 14) {      //点击小数点
                    isInteger = isInteger==true?false:true;
                }
                if (position == 3) {      //点击退格键
                    if(isInteger){
                        if(amount.charAt(0) != '0'){
                            amount = amount.substring(0, amount.length() - 4) + amount.substring(amount.length() - 3);
                        }
                        if(amount.length() == 3){
                            amount = "0"+amount;
                        }
                        if(amount.length() == 4 && amount.charAt(0) == '0' && numDecimal != 0){
                            isInteger = false;
                        }
                    }
                    else{
                        if (numDecimal == 0) {
                            isInteger = true;
                        }
                        else if(numDecimal == 1){
                            amount = amount.substring(0,amount.length()-2) + "00";
                            numDecimal--;
                        }
                        else if(numDecimal == 2){
                            amount = amount.substring(0,amount.length()-1) + "0";
                            numDecimal--;
                        }
                    }
                }
                if (position == 12) {      //点击C
                    amount = "0.00";
                }
                if (position == 15) {      //点击确定
                    if(!"0.00".equals(numAddItem.getText().toString())) {
                        AccountRecord record;
                        if(editRecord != null)record = editRecord;
                        else record = new AccountRecord();

                        if(indexAddItem != -1){//编辑项目改变了Icon和Recordname
                            MyApplication app = MyApplication.getApplication();
                            ArrayList<AddItem> shouruAddItems = app.getShouruAddItems();
                            ArrayList<AddItem> zhichuAddItems = app.getZhichuAddItems();
                            if (tabPosition == 0) {//存储收入记录
                                record.setIcon(shouruAddItems.get(indexAddItem).getIconAddItem());
                                record.setRecordname(shouruAddItems.get(indexAddItem).getNameAddItem());
                            }
                            else{
                                record.setIcon(zhichuAddItems.get(indexAddItem).getIconAddItem());
                                record.setRecordname(zhichuAddItems.get(indexAddItem).getNameAddItem());
                            }
                        }

                        if (tabPosition == 0) {
                            record.setMoney(numAddItem.getText().toString());
                        } else {
                            record.setMoney("-" + numAddItem.getText().toString());
                        }
                        record.setRecordtime(timeAddItem.getTime());
                        record.setRemark("");
                        MyApplication app = MyApplication.getApplication();
                        record.setAccount(app.getAccounts().get(1));
                        record.setAccountbook(app.getAccountBooks().get(1));

                        Log.i("3: ",record.toString());

                        Intent intent = new Intent();
                        intent.putExtra("data", record);
                        intent.putExtra("isFloatingActionButton",isFloatingActionButton);
                        setResult(RESULT_OK, intent);
                        finishAfterTransition();//带动画的退出
                    }
                    else{//输入金额为0
                        showToast("收入/支出金额不能为0", Toast.LENGTH_SHORT);
                    }
                }
            }
            numAddItem.setText(amount);
        }
    };

    /**
     * 显示Toast，解决重复弹出问题
     */
    public void showToast(String text , int time) {
        if(mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), text, time);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 隐藏Toast
     */
    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
    }

    public void ItemChange(int position){
        this.tabPosition = position; //改变分页位置标记
        this.indexAddItem = -1;//初始化当前选取的项目数
        if(position == 0){
            btnShouru.setTextColor(getResources().getColor(R.color.customBlue));
            btnZhichu.setTextColor(getResources().getColor(R.color.darkgray));
        }
        else{
            btnShouru.setTextColor(getResources().getColor(R.color.darkgray));
            btnZhichu.setTextColor(getResources().getColor(R.color.customBlue));
        }
    }

    /**
     *
     * @param Text 标题栏文字
     * @param icon 标题栏ICON的资源id
     * @param position 当前分页数（收入 | 支出）
     */
    public void ItemChange(String Text,String icon,int position){
        this.tabPosition = position; //改变分页位置标记
        this.indexAddItem = 0;//初始化当前选取的项目数
        nameAddItem.setText(Text);
        int resID = getResources().getIdentifier(icon, "drawable", getPackageName());
        iconAddItem.setBackgroundResource(resID);
        if(position == 0){
            btnShouru.setTextColor(getResources().getColor(R.color.customBlue));
            btnZhichu.setTextColor(getResources().getColor(R.color.darkgray));
        }
        else{
            btnShouru.setTextColor(getResources().getColor(R.color.darkgray));
            btnZhichu.setTextColor(getResources().getColor(R.color.customBlue));
        }
    }
    /**
     *
     * @param Text 标题栏文字
     * @param icon 标题栏ICON的资源id
     */
    public void ItemChange(String Text,String icon){
        nameAddItem.setText(Text);
        int resID = getResources().getIdentifier(icon, "drawable", getPackageName());
        iconAddItem.setBackgroundResource(resID);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            if(position == 0) {
                return MyFragment.newInstance("shouru");
            }
            else {
                return MyFragment.newInstance("zhichu");
            }
        }
        @Override
        public int getCount()
        {
            return 2;
        }
    }

    public static class MyFragment extends Fragment
    {
        private String mArgument;
        public static final String ARGUMENT = "argument";

        private RecyclerView mRecyclerView;
        private GridLayoutManager mLayoutManager;
        private AddItemRecyclerAdapter mRecyclerViewAdapter;

        /**
         * 传入需要的参数，设置给arguments
         *
         * @param argument
         * @return
         */
        public static MyFragment newInstance(String argument) {
            Bundle bundle = new Bundle();
            bundle.putString(ARGUMENT, argument);
            MyFragment contentFragment = new MyFragment();
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_additem,container,false);
            mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
            //设置固定大小
            mRecyclerView.setHasFixedSize(true);
            //创建表格布局
            mLayoutManager = new GridLayoutManager(getContext(),5);
            mRecyclerView.setLayoutManager(mLayoutManager);
            //给RecyclerView设置布局管理器
            mRecyclerView.setLayoutManager(mLayoutManager);

            //创建适配器，并且设置
            mRecyclerViewAdapter = new AddItemRecyclerAdapter(getContext(),mArgument);
            mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_additem);

            MyApplication app = MyApplication.getApplication();
            final ArrayList<AddItem> shouruAddItems = app.getShouruAddItems();
            final ArrayList<AddItem> zhichuAddItems = app.getZhichuAddItems();
            mRecyclerViewAdapter.addDatas("shouru",shouruAddItems);
            mRecyclerViewAdapter.addDatas("zhichu",zhichuAddItems);

            mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener(){
                @Override
                public void onClick(View view,int pos,String viewName) {
                    if("itemView".equals(viewName)) {
                        ((AddItemActivity) getActivity()).indexAddItem = pos;
                        if ("shouru".equals(mArgument)) {
                            ((AddItemActivity) getActivity()).ItemChange(shouruAddItems.get(pos)
                                    .getNameAddItem(), (shouruAddItems.get(pos).getIconAddItem()));
                        } else {
                            ((AddItemActivity) getActivity()).ItemChange(zhichuAddItems.get(pos)
                                    .getNameAddItem(), (zhichuAddItems.get(pos).getIconAddItem()));
                        }
                    }
                }
            });

            mRecyclerView.setAdapter(mRecyclerViewAdapter);
            return view;
        }
    }
}

class AddItemRecyclerAdapter extends BaseRecyclerViewAdapter{
    private String mArgument;

    public AddItemRecyclerAdapter(Context context,String argument){
        super(context);
        this.mArgument = argument;
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
        if(getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) return;
        Holder holder = (Holder)viewHolder;
        final int pos = getRealPosition(holder);

        if("shouru".equals(mArgument)) {
            ArrayList<AddItem> list = (ArrayList<AddItem>)mDatas.get("shouru");
            holder.item_str.setText(list.get(pos).getNameAddItem());
            int resID = mContent.getResources().getIdentifier(list.get(pos).getIconAddItem(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
        }
        else{
            ArrayList<AddItem> list = (ArrayList<AddItem>)mDatas.get("zhichu");
            holder.item_str.setText(list.get(pos).getNameAddItem());
            int resID = mContent.getResources().getIdentifier(list.get(pos).getIconAddItem(), "drawable", mContent.getPackageName());
            holder.item_icon.setBackgroundResource(resID);
        }

        if( mOnItemClickListener!= null){//更换选中的项目
            holder.itemView.setOnClickListener( new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v, pos, "itemView");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if("shouru".equals(mArgument)) {
            return mDatas.get("shouru").size();
        }
        else{
            return mDatas.get("zhichu").size();
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public TextView item_str;
        public ImageView item_icon;
        public Holder(View view, int viewType) {
            super(view);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            item_str = (TextView)view.findViewById(R.id.item_str);
            item_icon = (ImageView)view.findViewById(R.id.item_icon);
        }
    }
}

