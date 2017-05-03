package com.swjtu.huxin.accountmanagement.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.domain.Account;
import com.swjtu.huxin.accountmanagement.domain.AccountBook;
import com.swjtu.huxin.accountmanagement.service.AccountBookService;
import com.swjtu.huxin.accountmanagement.service.AccountService;
import com.swjtu.huxin.accountmanagement.utils.ConstantUtils;
import com.swjtu.huxin.accountmanagement.utils.ItemXmlPullParserUtils;
import com.swjtu.huxin.accountmanagement.utils.TimeUtils;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by huxin on 2017/2/28.
 */

public class SplashActivity extends BaseAppCompatActivity {
    private Handler rotateHandler = new RotateHandler(this);
    private Handler skipHandler = new SkipHandler(this);
    public ImageView imgXiaolian;
    private boolean isFirstRun;
    private TextView text1;
    private TextView text2;
    private Date firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkFirstRun();
        imgXiaolian = (ImageView) findViewById(R.id.ic_xiaolian_blue);
        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);

        text1.setText("我们于"+new SimpleDateFormat("yyyy年MM月dd日").format(firstTime)+"相遇");
        int dayDistance = TimeUtils.getTimeDistance(firstTime,new Date());
        if(dayDistance < 1) text2.setText("你今天刚刚开始记账");
        else text2.setText("你坚持记账 "+dayDistance+" 天了");

        rotateHandler.sendEmptyMessageDelayed(0, 800);
        skipHandler.sendEmptyMessageDelayed(0, 2000);

        MyApplication app = MyApplication.getApplication();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
//        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        app.setScreenHeight(height);
        app.setScreenWidth(width);
        try {
            app.setShouruAddItems(ItemXmlPullParserUtils.parseAddItemList(this, "shouru.xml"));
            app.setZhichuAddItems(ItemXmlPullParserUtils.parseAddItemList(this, "zhichu.xml"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(isFirstRun) {
            Map<Integer, AccountBook> books = new TreeMap<Integer, AccountBook>();
            AccountBook defaultAccountBook = new AccountBook();
            defaultAccountBook.setBookname("默认账本");
            defaultAccountBook.setType(ConstantUtils.ACCOUNT_BOOK_TYPE_DEFAULT);
            AccountBookService accountBookService = new AccountBookService();
            int defaultAccountBookID = (int)accountBookService.addAccountBook(defaultAccountBook);
            defaultAccountBook.setId(defaultAccountBookID);
            books.put(defaultAccountBookID,defaultAccountBook);
            app.setAccountBooks(books);

            Map<Integer, Account> accounts = new TreeMap<Integer, Account>();
            Account cash = new Account();
            cash.setAccountname("现金");
            cash.setAccountdetail("生活费");
            cash.setColor(ConstantUtils.ACCOUNT_COLOR[5]);
            cash.setType(ConstantUtils.ACCOUNT_TYPE_CASH);
            cash.setMoney("0.00");
            Account bankcard = new Account();
            bankcard.setAccountname("储蓄卡");
            bankcard.setAccountdetail("未知");
            bankcard.setColor(ConstantUtils.ACCOUNT_COLOR[4]);
            bankcard.setType(ConstantUtils.ACCOUNT_TYPE_BANK_CARD);
            bankcard.setMoney("0.00");
            Account creditcard = new Account();
            creditcard.setAccountname("信用卡");
            creditcard.setAccountdetail("未知");
            creditcard.setColor(ConstantUtils.ACCOUNT_COLOR[6]);
            creditcard.setType(ConstantUtils.ACCOUNT_TYPE_CREDIT_CARD);
            creditcard.setMoney("0.00");
            Account alipay = new Account();
            alipay.setAccountname("支付宝");
            alipay.setAccountdetail("未知");
            alipay.setColor(ConstantUtils.ACCOUNT_COLOR[3]);
            alipay.setType(ConstantUtils.ACCOUNT_TYPE_ALIPAY);
            alipay.setMoney("0.00");
            Account wechat = new Account();
            wechat.setAccountname("微信钱包");
            wechat.setAccountdetail("未知");
            wechat.setColor(ConstantUtils.ACCOUNT_COLOR[7]);
            wechat.setType(ConstantUtils.ACCOUNT_TYPE_WECHAT);
            wechat.setMoney("0.00");
            AccountService AccountService = new AccountService();
            int cashID = (int)AccountService.addAccount(cash);
            int bankcardID = (int)AccountService.addAccount(bankcard);
            int creditcardID = (int)AccountService.addAccount(creditcard);
            int alipayID = (int)AccountService.addAccount(alipay);
            int wechatID = (int)AccountService.addAccount(wechat);
            cash.setId(cashID);
            bankcard.setId(bankcardID);
            creditcard.setId(creditcardID);
            alipay.setId(alipayID);
            wechat.setId(wechatID);
            accounts.put(cashID,cash);
            accounts.put(bankcardID,bankcard);
            accounts.put(creditcardID,creditcard);
            accounts.put(alipayID,alipay);
            app.setAccounts(accounts);

            Set<String> member = new TreeSet<String>();
            member.add("我");
            member.add("爸爸");
            member.add("妈妈");
            SharedPreferences sharedPreferences = this.getSharedPreferences("userData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("member",member);
            app.setMembers(member);
        }
        else{
            Map<Integer, AccountBook> books = new TreeMap<Integer, AccountBook>();
            AccountBookService accountBookService = new AccountBookService();
            books = accountBookService.getAccountBookMap();
            app.setAccountBooks(books);

            Map<Integer, Account> accounts = new TreeMap<Integer, Account>();
            AccountService accountService = new AccountService();
            accounts = accountService.getAccountMap();
            app.setAccounts(accounts);

            SharedPreferences sharedPreferences = this.getSharedPreferences("userData", MODE_PRIVATE);
            Set<String> defaultMember = new TreeSet<String>();
            defaultMember.add("我");
            defaultMember.add("爸爸");
            defaultMember.add("妈妈");
            Set<String> member = sharedPreferences.getStringSet("member",defaultMember);
            Set<String> sortMember = new TreeSet<String>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Collator instance = Collator.getInstance(Locale.CHINA);
                    return instance.compare(o1, o2);
                }
            });
            sortMember.addAll(member);
            app.setMembers(sortMember);

//            String myTheme = sharedPreferences.getString("currentTheme","MyTheme_White");
//            int themeResID = getResources().getIdentifier(myTheme, "style", getPackageName());
//            app.setMyTheme(themeResID);
        }
    }

    private void checkFirstRun(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("userData", MODE_PRIVATE);
        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun)  {
            Log.i("debug", "第一次运行");
            editor.putBoolean("isFirstRun", false);
            editor.putLong("firstTime", new Date().getTime());
            editor.putBoolean("isBudget",true);
            editor.putString("totalMoney","3000.00");
            editor.apply();
            firstTime = new Date();
        }
        else {
            Log.i("debug", "不是第一次运行");
            long firstTimeMilliSeconds = sharedPreferences.getLong("firstTime", System.currentTimeMillis());
            firstTime = new Date(firstTimeMilliSeconds);
        }
    }

    static class SkipHandler extends Handler {
        WeakReference<SplashActivity> mActivityReference;
        SkipHandler(SplashActivity activity) {
            mActivityReference= new WeakReference<SplashActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final SplashActivity activity = mActivityReference.get();
            if (activity != null) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    static class RotateHandler extends Handler {
        WeakReference<SplashActivity> mActivityReference;
        RotateHandler(SplashActivity activity) {
            mActivityReference= new WeakReference<SplashActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            final SplashActivity activity = mActivityReference.get();
            if (activity != null) {
                Animation rotate = AnimationUtils.loadAnimation(activity, R.anim.rotate_anim_xiaolian);
                activity.imgXiaolian.setAnimation(rotate);
                activity.imgXiaolian.startAnimation(rotate);
            }
        }
    }
}
