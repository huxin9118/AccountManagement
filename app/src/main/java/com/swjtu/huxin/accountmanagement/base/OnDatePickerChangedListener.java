package com.swjtu.huxin.accountmanagement.base;

import android.widget.NumberPicker;

/**
 * Created by huxin on 2017/3/12.
 */

public interface OnDatePickerChangedListener{
    void onValueChange(NumberPicker picker, int oldVal, int newVal);
}
