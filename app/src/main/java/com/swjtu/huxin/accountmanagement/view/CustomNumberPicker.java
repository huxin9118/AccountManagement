package com.swjtu.huxin.accountmanagement.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.swjtu.huxin.accountmanagement.R;

import java.lang.reflect.Field;

/**
 * Created by huxin on 2017/3/12.
 */

public class CustomNumberPicker extends NumberPicker {

    public CustomNumberPicker(Context context) {
        super(context);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        setNumberPickerText(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setNumberPickerText(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setNumberPickerText(child);
    }

    private void setNumberPickerText(View view) {
        if (view instanceof EditText) {
//            这里修改字体的属性
            int[] attrsArray = { R.attr.textSecondaryColor };
            TypedArray typedArray = getContext().obtainStyledAttributes(attrsArray);
            final int color = typedArray.getColor(0,-1);
            typedArray.recycle();
            ((EditText) view).setTextColor(color);

            ((EditText) view).setTextSize(18);
            ((EditText) view).setClickable(false);
            ((EditText) view).setFocusable(false);
        }
    }

    public void setNumberPickerDivider() {
        NumberPicker picker = this;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(this.getResources().getColor(R.color.listLine)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (pf.getName().equals("mSelectionDividerHeight")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的高度
                    pf.set(picker, 1);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
