package com.swjtu.huxin.accountmanagement.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.swjtu.huxin.accountmanagement.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * ================================================
 * 作    者：顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * 版    本：
 * 创建日期：16/8/13-下午4:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class WaveProgressView extends View {

    private int radius = dp2px(55);
    private int textColor;
    private int textSize;
    private int progressColor;
    private int progressColorNegate;
    private int radiusColor;
    private TextPaint textPaint;
    private String text;
    private Paint circlePaint;
    private Paint pathPaint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private int width, height;
    private int minPadding;
    private float progress;
    private float maxProgress;
    private Path path = new Path();

    private float singleTextSize;
    private float doubleTextSize;


    public WaveProgressView(Context context) {
        this(context, null);
    }

    public WaveProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WaveProgressView, defStyleAttr, R.style.WaveProgressViewDefault);
        radius = (int) a.getDimension(R.styleable.WaveProgressView_WPV_radius, radius);
        textColor = a.getColor(R.styleable.WaveProgressView_WPV_progress_text_color, 0);
        textSize = a.getDimensionPixelSize(R.styleable.WaveProgressView_WPV_progress_text_size, 0);
        progressColor = a.getColor(R.styleable.WaveProgressView_WPV_progress_color, 0);
        progressColorNegate = a.getColor(R.styleable.WaveProgressView_WPV_progress_color_negate, 0);
        radiusColor = a.getColor(R.styleable.WaveProgressView_WPV_radius_color, 0);
        progress = a.getFloat(R.styleable.WaveProgressView_WPV_progress, 0);
        maxProgress = a.getFloat(R.styleable.WaveProgressView_WPV_maxProgress, 100);
        a.recycle();

        //初始化一些画笔
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/fzltxh.ttf");
        textPaint.setTypeface(font);
        textPaint.setTextSize(textSize);
        doubleTextSize = textSize;
        singleTextSize = textSize * 1.3f;
        textPaint.setDither(true);//防抖动
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);//抗锯齿


        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(radiusColor);
        circlePaint.setDither(true);
        circlePaint.setAntiAlias(true);

        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(progressColor);
        pathPaint.setDither(true);
        pathPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        pathPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //计算宽和高
        int exceptW = getPaddingLeft() + getPaddingRight() + 2 * radius;
        int exceptH = getPaddingTop() + getPaddingBottom() + 2 * radius;
        int width = resolveSize(exceptW, widthMeasureSpec);
        int height = resolveSize(exceptH, heightMeasureSpec);
        int min = Math.min(width, height);

        this.width = this.height = min;

        //计算半径,减去padding的最小值
        int minLR = Math.min(getPaddingLeft(), getPaddingRight());
        int minTB = Math.min(getPaddingTop(), getPaddingBottom());
        minPadding = Math.min(minLR, minTB);
        radius = (min - minPadding * 2) / 2;

        setMeasuredDimension(min, min);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        float progress;
        if(this.progress >= 0 )progress = this.progress > this.maxProgress?this.maxProgress:this.progress;
        else progress = this.progress*-1 > this.maxProgress?this.maxProgress:this.progress*-1;

        super.onDraw(canvas);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(bitmap);
        }
        bitmapCanvas.save();
        //移动坐标系
        bitmapCanvas.translate(minPadding, minPadding);
        //绘制圆
        bitmapCanvas.drawCircle(radius, radius, radius, circlePaint);

        //绘制PATH
        //重置绘制路线
        path.reset();
        float percent = progress * 1.0f / maxProgress;
        float y = (1 - percent) * radius * 2;
        //移动到右上边
        path.moveTo(radius * 2, y);
        //移动到最右下方
        path.lineTo(radius * 2, radius * 2);
        //移动到最左下边
        path.lineTo(0, radius * 2);
        //移动到左上边
        // path.lineTo(0, y);
        //实现左右波动,根据progress来平移
        path.lineTo(-(1 -percent) * radius*2, y);
        if (progress != 0.0f) {
            //根据直径计算绘制贝赛尔曲线的次数
            int count = radius * 4 / 60;
            //控制-控制点y的坐标
            float point = (1 - percent) * 15;
            for (int i = 0; i < count; i++) {
                path.rQuadTo(15, -point, 30, 0);
                path.rQuadTo(15, point, 30, 0);
            }
        }
        //闭合
        path.close();
        bitmapCanvas.drawPath(path, pathPaint);

        //绘制文字

        String text;
        if(this.text == null) {
            text = "月预算\n" + new DecimalFormat("0.00").format(this.progress);
            textPaint.setTextSize(doubleTextSize);
            StaticLayout staticLayout = new StaticLayout(text, textPaint, bitmapCanvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, true);
            bitmapCanvas.translate(0, radius / 1.8f);
            staticLayout.draw(bitmapCanvas);
            bitmapCanvas.restore();
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        else {
            text = this.text;
            textPaint.setTextSize(singleTextSize);
            float textW = textPaint.measureText(text);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
            float baseLine = radius - (fontMetrics.ascent + fontMetrics.descent) / 2;
            bitmapCanvas.drawText(text, radius - textW / 2, baseLine, textPaint);
            bitmapCanvas.restore();
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getProgress() {
        return progress;
    }


    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if(progress >= 0 || text != null) pathPaint.setColor(progressColor);
        else pathPaint.setColor(progressColorNegate);
        invalidate();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    private final static class SavedState extends BaseSavedState {
        float progress;

        public SavedState(Parcel source) {
            super(source);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = progress;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setProgress(ss.progress);
    }
}
