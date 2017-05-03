package com.swjtu.huxin.accountmanagement.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by jhon on 2016/4/11.
 */
public class ItemSwipeLayout extends RelativeLayout {
    public View mCenterView;
    public View mRightView;
    public int mRightWidth;
    public int mCenterWidth;

    public ItemSwipeLayout(Context context) {
        super(context);
    }

    public ItemSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ItemSwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
       super.onLayout(changed,l,t,r,b);
       if(mRightView != null){
           mRightWidth = mRightView.getMeasuredWidth();
           int height = mRightView.getMeasuredHeight();
           mRightView.layout(r,0,r + mRightWidth,height);
       }
        if(mCenterView != null){
            mCenterWidth = mCenterView.getMeasuredWidth();
            int height = mCenterView.getMeasuredHeight();
            mCenterView.layout(l,0,r,height);
        }
    }

    public boolean isOpen(){
        if(mCenterView.getScrollX() > 0){
            return true;
        } else {
            return  false;
        }
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRightView = getChildAt(0);
        mCenterView = getChildAt(1);
    }
}
