package com.swjtu.huxin.accountmanagement.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.swjtu.huxin.accountmanagement.R;
import com.swjtu.huxin.accountmanagement.base.BaseAppCompatActivity;
import com.swjtu.huxin.accountmanagement.base.BaseRecyclerViewAdapter;
import com.swjtu.huxin.accountmanagement.base.MyApplication;
import com.swjtu.huxin.accountmanagement.base.OnItemClickListener;
import com.swjtu.huxin.accountmanagement.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by huxin on 2017/5/2.
 */

public class MoreSkinActivity extends BaseAppCompatActivity {
    private LinearLayout btnBack;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MoreSkinRecyclerAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_skin);
        initView();
    }

    private void initView() {
        btnBack = (LinearLayout) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelToast();
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new GridLayoutManager(this,3);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerViewAdapter = new MoreSkinRecyclerAdapter(this);
        mRecyclerViewAdapter.setCreateViewLayout(R.layout.item_recycler_more_skin);
        mRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view, int pos, String viewName) {
                if("itemView".equals(viewName)) {
                    List<String> myThemes = (ArrayList<String>)mRecyclerViewAdapter.getDatas("myThemes");
                    SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentTheme",myThemes.get(pos));
                    editor.apply();
                    MyApplication.getApplication().setMyTheme(myThemes.get(pos));
                    finish();
                    Intent intent = new Intent(MoreSkinActivity.this, MainActivity.class);
                    intent.putExtra("defaultPosition",3);
                    intent.setFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    showToast("皮肤更换成功", Toast.LENGTH_SHORT);
                }
            }
        });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//添加/删除item默认的动画效果
        initRecyclerViewData();
    }

    private void initRecyclerViewData(){
        List<String> myThemes = new ArrayList<String>();
        myThemes.add("MyTheme_White");
        myThemes.add("MyTheme_Cat");
        myThemes.add("MyTheme_Sea");
        myThemes.add("MyTheme_Constellation");
        myThemes.add("MyTheme_Dark");
        myThemes.add("MyTheme_Paris");
        myThemes.add("MyTheme_Volcano");
        mRecyclerViewAdapter.addDatas("myThemes",myThemes);
        mRecyclerViewAdapter.notifyDataSetChanged();
    }
}

class MoreSkinRecyclerAdapter extends BaseRecyclerViewAdapter {
    private Context mContext;

    public MoreSkinRecyclerAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) return mDatas.get("myThemes").size();
        if (mHeaderView != null && mFooterView != null) return mDatas.get("myThemes").size() + 2;
        return mDatas.get("myThemes").size() + 1;
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
            List<String> myThemes = (ArrayList<String>)mDatas.get("myThemes");
            int themeResID = mContent.getResources().getIdentifier(myThemes.get(pos), "style", mContent.getPackageName());
            int[] attrsArray = { R.attr.mainBackgrount };
            TypedArray typedArray = mContext.obtainStyledAttributes(themeResID,attrsArray);
            int imgResID = typedArray.getResourceId(0,-1);
            typedArray.recycle();
            Glide.with(mContent).load(imgResID).override(640,360).bitmapTransform(new RoundedCornersTransformation(mContext,
                    DensityUtils.dp2px(mContext,20),DensityUtils.dp2px(mContext,20))).into(holder.img);
            if(myThemes.get(pos).equals(MyApplication.getApplication().getMyTheme()))
                holder.btnSelector.setVisibility(View.VISIBLE);
            else
                holder.btnSelector.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(v,pos,"itemView");
                }
            });
        }
    }

    static class Holder extends RecyclerView.ViewHolder {
        public ImageView img;
        public RelativeLayout btnSelector;
        public Holder(View itemView, int viewType) {
            super(itemView);
            if(viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return;
            if(viewType == TYPE_NORMAL) {
                img = (ImageView) itemView.findViewById(R.id.img);
                btnSelector = (RelativeLayout) itemView.findViewById(R.id.btnSelector);
            }
        }
    }
}
