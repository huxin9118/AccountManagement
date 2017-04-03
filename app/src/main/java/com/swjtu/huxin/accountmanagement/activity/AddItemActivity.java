package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.swjtu.huxin.accountmanagement.adapter.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.application.MyApplication;
import com.swjtu.huxin.accountmanagement.adapter.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.domain.AddItem;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    private int selectAccount;
    private String selectMember;
    private String remark;

    private PopupWindow accountPopupWindow;
    private PopupWindow remarkPopupWindow;
    private PopupWindow memberPopupWindow;

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
        btnAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(accountPopupWindow);
            }
        });
        btnRemark = (Button) findViewById(R.id.remark);
        btnRemark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(remarkPopupWindow);
            }
        });
        btnMember = (Button) findViewById(R.id.member);
        btnMember.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(memberPopupWindow);
            }
        });


        btnDate = (TextView) findViewById(R.id.date);
        btnTime = (TextView) findViewById(R.id.time);

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
                if(date.getTime() > new Date().getTime()){
                    showToast("不能选取未来的时间哦~~",Toast.LENGTH_SHORT);
                }
                else {
                    timeAddItem = date;
                    updateBtnDateAndTime(date);
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
            }
        });

        btnDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.setDefaultYear(TimeUtils.getTime(timeAddItem,TimeUtils.YEAR));
                dateTimeFragment.setDefaultMonth(TimeUtils.getTime(timeAddItem,TimeUtils.MONTH)-1);
                dateTimeFragment.setDefaultDay(TimeUtils.getTime(timeAddItem,TimeUtils.DAY));
                dateTimeFragment.setDefaultHourOfDay(TimeUtils.getTime(timeAddItem,TimeUtils.HOUR));
                dateTimeFragment.setDefaultMinute(TimeUtils.getTime(timeAddItem,TimeUtils.MINUTE));
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        btnTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.setDefaultYear(TimeUtils.getTime(timeAddItem,TimeUtils.YEAR));
                dateTimeFragment.setDefaultMonth(TimeUtils.getTime(timeAddItem,TimeUtils.MONTH));
                dateTimeFragment.setDefaultDay(TimeUtils.getTime(timeAddItem,TimeUtils.DAY));
                dateTimeFragment.setDefaultHourOfDay(TimeUtils.getTime(timeAddItem,TimeUtils.HOUR));
                dateTimeFragment.setDefaultMinute(TimeUtils.getTime(timeAddItem,TimeUtils.MINUTE));
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
            updateBtnDateAndTime(timeAddItem);
            Account account = editRecord.getAccount();
            btnAccount.setText(account.getAccountname());
            selectAccount = account.getId();

            selectMember = editRecord.getMember();
            remark = editRecord.getRemark();
            updateBtnMember();
            updateBtnRemark();
        }
        else {
            timeAddItem = new Date();
            updateBtnDateAndTime(timeAddItem);

            SharedPreferences sharedPreferences = this.getSharedPreferences("userData", MODE_PRIVATE);
            selectAccount = sharedPreferences.getInt("defaultAccount", 1);
            MyApplication app = MyApplication.getApplication();
            Account account = app.getAccounts().get(selectAccount);
            btnAccount.setText(account.getAccountname());

            selectMember = "";
            remark = "";
            updateBtnMember();
            updateBtnRemark();

            btnZhichu.performClick();//默认点击
        }
        dateTimeFragment.setDefaultDateTime(timeAddItem);
        initAccountPopupWindow();
        initMemberPopupWindow();
        initRemarkPopupWindow();
    }

    private void updateBtnDateAndTime(Date time){
        btnDate.setText(new SimpleDateFormat("yy年MM月dd日").format(time));
        btnTime.setText(new SimpleDateFormat("HH:mm").format(time));
    }

    private void updateBtnMember(){
        if("".equals(selectMember))
            btnMember.setBackgroundResource(R.drawable.ic_chengyuan);
        else
            btnMember.setBackgroundResource(R.drawable.ic_chengyuan_blue);
    }

    private void updateBtnRemark(){
        if("".equals(remark))
            btnRemark.setBackgroundResource(R.drawable.ic_beizhu);
        else
            btnRemark.setBackgroundResource(R.drawable.ic_beizhu_blue);
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
                        record.setRemark(remark);
                        record.setMember(selectMember);
                        MyApplication app = MyApplication.getApplication();
                        record.setAccount(app.getAccounts().get(selectAccount));
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

    //一定要在selectAccount完成初始化后再调用该方法
    private void initAccountPopupWindow() {
        View contentView = LayoutInflater.from(AddItemActivity.this).inflate(R.layout.popupwindow_account, null);
        accountPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        ListView list = (ListView)contentView.findViewById(R.id.list);

        AccountRecordService accountRecordService = new AccountRecordService();
        MyApplication app = MyApplication.getApplication();
        final List<Account> accounts = new ArrayList<Account>(app.getAccounts().values());
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < accounts.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item_icon", getIconByType(accounts.get(i).getType()));
            map.put("item_text", accounts.get(i).getAccountname());
            String totalMoney = accountRecordService.getTotalMoneyByAccount(accounts.get(i));
            map.put("item_money", new BigDecimal(accounts.get(i).getMoney()).add(new BigDecimal(totalMoney)).toString());
            Log.i("111", selectAccount+"=="+i);
            if(selectAccount == accounts.get(i).getId())
                map.put("item_selector", R.drawable.ic_selector_blue);
            else
                map.put("item_selector", null);
            data.add(map);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_list_account,
                new String[]{"item_icon", "item_text","item_money","item_selector"}, new int[]{R.id.item_icon,
                R.id.item_text,R.id.item_money,R.id.item_selector});
        list.setAdapter(simpleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < data.size(); i++) {
                    if(position == i)
                        data.get(i).put("item_selector", R.drawable.ic_selector_blue);
                    else
                        data.get(i).put("item_selector", null);
                }
                simpleAdapter.notifyDataSetChanged();
                accountPopupWindow.dismiss();

                selectAccount = accounts.get(position).getId();
                SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("defaultAccount",selectAccount);
                editor.apply();
                btnAccount.setText(accounts.get(position).getAccountname());
            }
        });

        list.setDivider(null);//去除分割线

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountPopupWindow.dismiss();
            }
        });
    }
    private void initMemberPopupWindow() {
        View contentView = LayoutInflater.from(AddItemActivity.this).inflate(R.layout.popupwindow_member, null);
        memberPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        ListView list = (ListView)contentView.findViewById(R.id.list);

        AccountRecordService accountRecordService = new AccountRecordService();
        MyApplication app = MyApplication.getApplication();
        final List<String> members = new ArrayList<String>(app.getMembers());
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for(int i = 0; i < members.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("item_text", members.get(i));
            if(editRecord != null && editRecord.getMember().equals(members.get(i)))
                map.put("item_selector", R.drawable.ic_selector_blue);
            else
                map.put("item_selector", null);
            data.add(map);
        }

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.item_list_member,
                new String[]{"item_text","item_selector"}, new int[]{R.id.item_text,R.id.item_selector});
        list.setAdapter(simpleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(selectMember.equals(members.get(position))){
                    data.get(position).put("item_selector", null);
                    selectMember = "";
                }
                else {
                    for (int i = 0; i < data.size(); i++) {
                        if (position == i)
                            data.get(i).put("item_selector", R.drawable.ic_selector_blue);
                        else
                            data.get(i).put("item_selector", null);
                    }
                    selectMember = members.get(position);
                }
                simpleAdapter.notifyDataSetChanged();
                memberPopupWindow.dismiss();

                updateBtnMember();
            }
        });

        list.setDivider(null);//去除分割线

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                memberPopupWindow.dismiss();
            }
        });
    }
    private void initRemarkPopupWindow() {
        View contentView = LayoutInflater.from(AddItemActivity.this).inflate(R.layout.popupwindow_remark, null);
        remarkPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        //设置各个控件的点击响应
        final EditText editText =  (EditText)contentView.findViewById(R.id.editText);
        editText.setText(remark);
        editText.setSelection(remark.length());
        ImageView close =  (ImageView)contentView.findViewById(R.id.close);
        ImageView ok =  (ImageView)contentView.findViewById(R.id.ok);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remarkPopupWindow.dismiss();
                updateBtnRemark();
            }
        });
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remark = editText.getText().toString().trim();
                remarkPopupWindow.dismiss();
                updateBtnRemark();
            }
        });

        View outOfWindow = (View)contentView.findViewById(R.id.outof_popup_window);
        outOfWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remarkPopupWindow.dismiss();
            }
        });
    }

    private void showPopupWindow(PopupWindow popupWindow) {
        View rootview = LayoutInflater.from(AddItemActivity.this).inflate(R.layout.activity_additem, null);
        popupWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
    }

    private int getIconByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return R.drawable.ic_cash_gray;
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return R.drawable.ic_bank_card_gray;
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return R.drawable.ic_credit_card_gray;
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return R.drawable.ic_alipay_gray;
            default:return -1;
        }
    }


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

    /**
     * edit记录初始化Tab分页时调用
     * @param position 当前分页数（收入 | 支出）
     */
    public void ItemChange(int position){
        this.tabPosition = position; //改变分页位置标记
        this.indexAddItem = -1;//edit记录时将其特殊化为-1，表示未修改过记录
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
     * 改变tab分页时调用
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
     * 选择AddItem项目时调用
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

