package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.OnNumKeyboardItemClickListener;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountRecord;
import com.swjtu.huxin.accountmanagement.domain.AddItem;
import com.swjtu.huxin.accountmanagement.service.AccountRecordService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;


/**
 * Created by huxin on 2017/2/25.
 */

public class AddItemActivity extends BaseAppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private TextView btnShouru;
    private TextView btnZhichu;
    private ImageView btnBack;
    private TextView nameAddItem;
    private ImageView iconAddItem;
    private TextView numAddItem;
    private TextView symbolAddItem;
    private LinearLayout keyboard;
    private NumKeyboardView numKeyboardView;
    private GridView gridView;
    private Animation enterAnim;
    private Animation exitAnim;

    private int tabPosition; //当前分页数
    private int indexAddItem = 0; //当前选取的项目数
    private Date timeAddItem;//当前的时间
    private AccountRecord editRecord; //编辑记录
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    private TextView btnAccount;
    private TextView btnDate;
    private TextView btnTime;
    private ImageView btnRemark;
    private ImageView btnMember;
    private int selectAccount;
    private String selectMember;
    private String remark;

    private PopupWindow accountPopupWindow;
    private PopupWindow remarkPopupWindow;
    private PopupWindow memberPopupWindow;

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
        symbolAddItem = (TextView) findViewById(R.id.item_symbol);

        keyboard = (LinearLayout)findViewById(R.id.keybord);
        numKeyboardView = (NumKeyboardView) findViewById(R.id.numKeyboardView);

        btnAccount = (TextView) findViewById(R.id.account);
        btnAccount.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(accountPopupWindow);
            }
        });
        btnRemark = (ImageView) findViewById(R.id.remark);
        btnRemark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(remarkPopupWindow);
            }
        });
        btnMember = (ImageView) findViewById(R.id.member);
        btnMember.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(memberPopupWindow);
            }
        });

        btnDate = (TextView) findViewById(R.id.date);
        btnTime = (TextView) findViewById(R.id.time);
        btnDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(getFragmentManager(), DATEPICKER_TAG);
            }
        });
        btnTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);
            }
        });

        gridView = numKeyboardView.getGridView();
        gridView.setOnItemClickListener(new OnNumKeyboardItemClickListener(numKeyboardView, numAddItem, symbolAddItem, new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if (!"0.00".equals(numAddItem.getText().toString())) {
                    AccountRecord record;
                    if (editRecord != null) record = editRecord;
                    else record = new AccountRecord();

                    if (indexAddItem != -1) {//编辑项目改变了Icon和Recordname
                        MyApplication app = MyApplication.getApplication();
                        ArrayList<AddItem> shouruAddItems = app.getShouruAddItems();
                        ArrayList<AddItem> zhichuAddItems = app.getZhichuAddItems();
                        if (tabPosition == 0) {//存储收入记录
                            record.setIcon(shouruAddItems.get(indexAddItem).getIconAddItem());
                            record.setRecordname(shouruAddItems.get(indexAddItem).getNameAddItem());
                        } else {
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

                    Log.i("3: ", record.toString());

                    SharedPreferences sharedPreferences = AddItemActivity.this.getSharedPreferences("userData", MODE_PRIVATE);
                    long firstTimeMilliSeconds = sharedPreferences.getLong("firstTime", System.currentTimeMillis());
                    boolean isUpdateFirstTime = record.getRecordtime() < firstTimeMilliSeconds;
                    if(isUpdateFirstTime){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("firstTime", record.getRecordtime());
                        editor.apply();
                    }
                    MyApplication.getApplication().getDataChangeObservable().dataChange();

                    Intent intent = new Intent();
                    intent.putExtra("data", record);
                    intent.putExtra("isFloatingActionButton", isFloatingActionButton);
                    intent.putExtra("isUpdateFirstTime", isUpdateFirstTime);
                    setResult(RESULT_OK, intent);
                    finishAfterTransition();//带动画的退出
                } else {//输入金额为0
                    showToast("收入/支出金额必须大于0", Toast.LENGTH_SHORT);
                }
            }
        }));

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
                mViewPager.setCurrentItem(0);
                ItemChange(editRecord.getRecordname(),editRecord.getIcon(),0);
                indexAddItem = -1;
            }
            else{
                numAddItem.setText(editRecord.getMoney().substring(1));
                mViewPager.setCurrentItem(1);
                ItemChange(editRecord.getRecordname(),editRecord.getIcon(),1);
                indexAddItem = -1;
            }
            timeAddItem = new Date(editRecord.getRecordtime());
            updateBtnDateAndTime(timeAddItem);
            Account account = editRecord.getAccount();
            btnAccount.setText(account.getAccountname());
            selectAccount = account.getId();

            selectMember = editRecord.getMember();
            remark = editRecord.getRemark();
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

            btnZhichu.performClick();//默认点击
        }

        updateBtnMember();
        updateBtnRemark();
        initDatePickerDialog(timeAddItem);
        initTimePickerDialog(timeAddItem);

        initAccountPopupWindow();
        initMemberPopupWindow();
        initRemarkPopupWindow();
    }

    private void initDatePickerDialog(Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int[] attrsArray = { R.attr.dialog_backgound };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        datePickerDialog = DatePickerDialog.newInstance(this,calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setAccentColor(color);
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_1);
    }

    private void initTimePickerDialog(Date time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int[] attrsArray = { R.attr.dialog_backgound };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        timePickerDialog = TimePickerDialog.newInstance(this,calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.setAccentColor(color);
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_1);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeAddItem);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if(calendar.getTime().getTime() > new Date().getTime()){
            showToast("不能选取未来的时间哦~~",Toast.LENGTH_SHORT);
        }
        else {
            timeAddItem = calendar.getTime();
            updateBtnDateAndTime(timeAddItem);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timeAddItem);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        if(calendar.getTime().getTime() > new Date().getTime()){
            showToast("不能选取未来的时间哦~~",Toast.LENGTH_SHORT);
        }
        else {
            timeAddItem = calendar.getTime();
            updateBtnDateAndTime(timeAddItem);
        }
    }

    private void updateBtnDateAndTime(Date time){
        btnDate.setText(new SimpleDateFormat("yy年MM月dd日").format(time));
        btnTime.setText(new SimpleDateFormat("HH:mm").format(time));
    }

    private void updateBtnMember(){
        if("".equals(selectMember)) {
            int[] attrsArray = { R.attr.more_half_transparent_contrast };
            TypedArray typedArray = obtainStyledAttributes(attrsArray);
            int color = typedArray.getColor(0,-1);
            typedArray.recycle();
            btnMember.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            btnMember.invalidate();
        }
        else {
            btnMember.getBackground().setColorFilter(getResources().getColor(R.color.customBlue), PorterDuff.Mode.SRC_ATOP);
            btnMember.invalidate();
        }
    }

    private void updateBtnRemark(){
        if("".equals(remark)) {
            int[] attrsArray = { R.attr.more_half_transparent_contrast };
            TypedArray typedArray = obtainStyledAttributes(attrsArray);
            int color = typedArray.getColor(0,-1);
            typedArray.recycle();
            btnRemark.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            btnRemark.invalidate();
        }
        else {
            btnRemark.getBackground().setColorFilter(getResources().getColor(R.color.customBlue), PorterDuff.Mode.SRC_ATOP);
            btnRemark.invalidate();
        }
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
            map.put("item_icon", getPopupWindowIconByType(accounts.get(i).getType()));
            map.put("item_text", accounts.get(i).getAccountname());
            String totalMoney = accountRecordService.getTotalMoneyByAccount(accounts.get(i));
            map.put("item_money", new BigDecimal(accounts.get(i).getMoney()).add(new BigDecimal(totalMoney)).toString());
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
                editText.setText(remark);
                editText.setSelection(remark.length());
                updateBtnRemark();
            }
        });
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remark = editText.getText().toString().trim();
                remarkPopupWindow.dismiss();
                editText.setText(remark);
                editText.setSelection(remark.length());
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

    private int getPopupWindowIconByType(int type){
        switch (type) {
            case ConstantUtils.ACCOUNT_TYPE_CASH:return R.drawable.ic_cash_gray;
            case ConstantUtils.ACCOUNT_TYPE_BANK_CARD:return R.drawable.ic_bank_card_gray;
            case ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD:return R.drawable.ic_credit_card_gray;
            case ConstantUtils.ACCOUNT_TYPE_ALIPAY:return R.drawable.ic_alipay_gray;
            case ConstantUtils.ACCOUNT_TYPE_WECHAT:return R.drawable.ic_wechat_gray;
            default:return -1;
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
        int[] attrsArray = { R.attr.textSecondaryColor };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int color = typedArray.getColor(0,-1);
        typedArray.recycle();
        if(position == 0){
            btnShouru.setTextColor(getResources().getColor(R.color.customBlue));
            btnZhichu.setTextColor(color);
        }
        else{
            btnShouru.setTextColor(color);
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
            Glide.with(mContent).load(resID).dontAnimate().into(holder.item_icon);
//            holder.item_icon.setBackgroundResource(resID);
        }
        else{
            ArrayList<AddItem> list = (ArrayList<AddItem>)mDatas.get("zhichu");
            holder.item_str.setText(list.get(pos).getNameAddItem());
            int resID = mContent.getResources().getIdentifier(list.get(pos).getIconAddItem(), "drawable", mContent.getPackageName());
            Glide.with(mContent).load(resID).dontAnimate().into(holder.item_icon);
//            holder.item_icon.setBackgroundResource(resID);
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

