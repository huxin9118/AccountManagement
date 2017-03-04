package com.swjtu.huxin.accountmanagement.view;

/**
 * Created by huxin on 2017/2/24.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

import com.swjtu.huxin.accountmanagement.R;

public class PercentageBallView extends View {

    private Context mContext;

    private int mScreenWidth;
    private int mScreenHeight;

    //private Paint mRingPaint;
    private Paint mCirclePaint;
    private Paint mWavePaint;
    private Paint flowPaint;

    //private int mRingSTROKEWidth = 8;
    //private int mCircleSTROKEWidth = 8;
    //private int mLineSTROKEWidth = 1;

    private Handler mHandler;
    private long c = 0L;
    private boolean mStarted = false;
    private final float f = 0.033F;
    private int mAlpha = 50;// 透明度
    private float mAmplitude = 0.0F; // 振幅
    private float mWaterLevel = 0.0F;// 水高(0~1)
    private Path mPath;

    // 绘制文字显示在圆形中间，只是我没有设置，我觉得写在布局上也挺好的
    private String flowNum = "";

    /**
     * @param context
     */
    public PercentageBallView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext);
    }

    /**
     * @param context
     * @param attrs
     */
    public PercentageBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        init(mContext);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public PercentageBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(mContext);
    }

    public void setmWaterLevel(float mWaterLevel, String str) {
        this.mWaterLevel = mWaterLevel;
        this.flowNum = str;
    }

    public void setmWaterWave(float mWaterLevel, String str, float mWater) {
        this.mWaterLevel = mWaterLevel;
        this.flowNum = str;
        this.mAmplitude = mWater;
    }

    private void init(Context context) {
        // 外圈
//        mRingPaint = new Paint();
//        mRingPaint.setColor(this.getResources().getColor(R.color.dodgerblue));
//        mRingPaint.setStyle(Paint.Style.STROKE);//空心园效果
//        mRingPaint.setAntiAlias(true);//抗锯齿
//        mRingPaint.setStrokeWidth(mRingSTROKEWidth);

        // 内圈
        mCirclePaint = new Paint();
        mCirclePaint.setColor(this.getResources().getColor(R.color.silver));
        mCirclePaint.setAntiAlias(true);

        // 文字
        flowPaint = new Paint();
        flowPaint.setColor(this.getResources().getColor(R.color.white));
        flowPaint.setStyle(Paint.Style.FILL);
        flowPaint.setAntiAlias(true);
        flowPaint.setTextSize(30);

        // 内填充
        mWavePaint = new Paint();
        mWavePaint.setStrokeWidth(1.0F);
        mWavePaint.setColor(this.getResources().getColor(R.color.dodgerblue));
        // mWavePaint.setAlpha(mAlpha);
        mPath = new Path();

        mHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    invalidate();
                    if (mStarted) {
                        // 不断发消息给自己，使自己不断被重绘
                        mHandler.sendEmptyMessageDelayed(0, 60L);
                    }
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measure(widthMeasureSpec, true);
        int height = measure(heightMeasureSpec, false);
        if (width < height) {
            setMeasuredDimension(width, width);
        } else {
            setMeasuredDimension(height, height);
        }

    }

    /**
     * @category 测量
     * @param measureSpec
     * @param isWidth
     * @return
     */
    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenWidth = w;
        mScreenHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        // 得到控件的宽高
        int width = getWidth();
        int height = getHeight();

        setBackgroundColor(Color.alpha(255));// 可以自定义色值

        //绘制圆形
//        canvas.drawCircle(mScreenWidth / 2, mScreenHeight / 2, mScreenWidth / 2.5f + mRingSTROKEWidth, mRingPaint);
        canvas.drawCircle(mScreenWidth / 2, mScreenHeight / 2, mScreenWidth / 2.2f, mCirclePaint);

        // 计算当前油量线和水平中线的距离
        float centerOffset = Math.abs(mScreenWidth / 2 * mWaterLevel - mScreenWidth / 4);
        // 计算油量线和与水平中线的角度
        float horiAngle = (float) (Math.asin(centerOffset / (mScreenWidth / 4)) * 180 / Math.PI);
        // 扇形的起始角度和扫过角度
        float startAngle, sweepAngle;
        if (mWaterLevel > 0.5F) {
            startAngle = 360F - horiAngle;
            sweepAngle = 180F + 2 * horiAngle;
        } else {
            startAngle = horiAngle;
            sweepAngle = 180F - 2 * horiAngle;
        }

        // 如果未开始（未调用startWave方法）,绘制一个扇形
        if ((!mStarted) || (mScreenWidth == 0) || (mScreenHeight == 0)) {
            // 绘制,即水面静止时的高度
            RectF oval = new RectF(mScreenWidth / 2 - mScreenWidth / 2.2f, mScreenHeight / 2 - mScreenHeight / 2.2f,
                    mScreenWidth / 2 + mScreenWidth / 2.2f, mScreenHeight / 2 + mScreenHeight / 2.2f);
            canvas.drawArc(oval, startAngle, sweepAngle, false, mWavePaint);
            return;
        }
        // 绘制,即水面静止时的高度
        RectF oval = new RectF(mScreenWidth / 2 - mScreenWidth / 2.2f, mScreenHeight / 2 - mScreenHeight / 2.2f,
                mScreenWidth / 2 + mScreenWidth / 2.2f, mScreenHeight / 2 + mScreenHeight / 2.2f);
        canvas.drawArc(oval, startAngle, sweepAngle, false, mWavePaint);

        // 放到这里绘制文字，不然会因为前面的绘图遮挡住文字
        TextPaint textPaint = new TextPaint(flowPaint);//此可以在View的初始化中进行
        StaticLayout staticLayout = new StaticLayout(flowNum, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
        canvas.translate(0,mScreenHeight * 5 / 16);
        staticLayout.draw(canvas);

        //float num = flowPaint.measureText(flowNum);
        //canvas.drawText(flowNum, mScreenWidth * 4 / 8 - num / 2, mScreenHeight * 4 / 8, flowPaint);

        canvas.restore();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = (int) c;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        c = ss.progress;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 关闭硬件加速，防止异常unsupported operation exception
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /**
     * 开始波动
     */
    public void startWave() {
        if (!mStarted) {
            this.c = 0L;
            mStarted = true;
            this.mHandler.sendEmptyMessage(0);
        }
    }

    /**
     * @category 停止波动
     */
    public void stopWave() {
        if (mStarted) {
            this.c = 0L;
            mStarted = false;
            this.mHandler.removeMessages(0);
        }
    }

    /**
     * @category 保存状态
     */
    static class SavedState extends BaseSavedState {
        int progress;

        /**
         * Constructor called from {@link ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
