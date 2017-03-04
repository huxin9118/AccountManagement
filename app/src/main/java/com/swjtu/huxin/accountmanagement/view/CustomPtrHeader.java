package com.swjtu.huxin.accountmanagement.view;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.john.waveview.WaveView;
import com.swjtu.huxin.accountmanagement.R;

/**
 * Created by huxin on 2017/2/26.
 */

public class CustomPtrHeader extends FrameLayout implements PtrUIHandler {
    WaveView wave_view;
    int i;

    public CustomPtrHeader(Context context) {
        super(context);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomPtrHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_recycler_mingxi_refresh_header, this);
        wave_view = (WaveView) view.findViewById(R.id.wave_view);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

    }


    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator){
        float percent = Math.min(1f, ptrIndicator.getCurrentPercent());

        //if (status == PtrFrameLayout.PTR_STATUS_PREPARE) {
        wave_view.setProgress((int) (percent * 300* 1.0));
        invalidate();
        // }
    }

    /**
     * 设置波纹进度
     * @param progress 进度
     */
    private void setWaveProgress(int progress){
        wave_view.setProgress(progress);
    }

}