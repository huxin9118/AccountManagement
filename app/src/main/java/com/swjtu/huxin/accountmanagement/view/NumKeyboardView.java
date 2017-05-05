package com.swjtu.huxin.accountmanagement.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.R;

import java.util.ArrayList;

/**
 * Created by huxin on 2017/2/25.
 */

public class NumKeyboardView extends RelativeLayout {

    Context context;

    private GridView gridView;    //用GrideView布局键盘，其实并不是真正的键盘，只是模拟键盘的功能

    private ArrayList<String> valueList;    //因为要用Adapter中适配，用数组不能往adapter中填充
    private KeyBoardAdapter keyBoardAdapter;

    private String oldAmount;
    private boolean isAddSymbol;
    private boolean isSubtractSymbol;
    private boolean isZero;

    public NumKeyboardView(Context context) {
        this(context, null);
    }

    public NumKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        View view = View.inflate(context, R.layout.view_num_keyboard, null);

        valueList = new ArrayList<>();

//        imgBack = (RelativeLayout) view.findViewById(R.id.imgBack);//收回键盘按键

        gridView = (GridView) view.findViewById(R.id.gv_keybord);

        initValueList();

        setupView();

        addView(view);      //必须要，不然不显示控件
    }

    public ArrayList<String> getValueList() {
        return valueList;
    }

    private void initValueList() {

        // 初始化按钮上应该显示的数字
        valueList.add("1");valueList.add("2");valueList.add("3");valueList.add("");
        valueList.add("4");valueList.add("5");valueList.add("6");valueList.add("+");
        valueList.add("7");valueList.add("8");valueList.add("9");valueList.add("-");
        valueList.add("C");valueList.add("0");valueList.add(".");valueList.add("确定");
    }

    public GridView getGridView() {
        return gridView;
    }

    public void changeBtnOK(){
        keyBoardAdapter.changeBtnOK();
    }

    public void changeBtnEqual(){
        keyBoardAdapter.changeBtnEqual();
    }

    public String getBtnText(){
        return keyBoardAdapter.getBtnText();
    }

    private void setupView() {
        keyBoardAdapter = new KeyBoardAdapter(context, valueList);
        gridView.setAdapter(keyBoardAdapter);
    }

    public String getOldAmount() {
        return oldAmount;
    }

    public void setOldAmount(String oldAmount) {
        this.oldAmount = oldAmount;
    }

    public boolean isAddSymbol() {
        return isAddSymbol;
    }

    public void setAddSymbol(boolean addSymbol) {
        isAddSymbol = addSymbol;
    }

    public boolean isSubtractSymbol() {
        return isSubtractSymbol;
    }

    public void setSubtractSymbol(boolean subtractSymbol) {
        isSubtractSymbol = subtractSymbol;
    }

    public boolean isZero() {
        return isZero;
    }

    public void setZero(boolean zero) {
        isZero = zero;
    }
}

class KeyBoardAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> valueList;
    private TextView ok;

    public KeyBoardAdapter(Context mContext, ArrayList<String> valueList) {
        this.mContext = mContext;
        this.valueList = valueList;
    }

    @Override
    public int getCount() {
        return valueList.size();
    }

    @Override
    public Object getItem(int position) {
        return valueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_grid_num_keyboard, null);
            viewHolder = new ViewHolder();
            viewHolder.btnKey = (TextView) convertView.findViewById(R.id.btn_keys);
            viewHolder.imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position == 3) {//退格键
            int[] attrsArray = { R.attr.theme_isLight };
            TypedArray typedArray = mContext.obtainStyledAttributes(attrsArray);
            boolean isLight = typedArray.getBoolean(0,true);
            typedArray.recycle();
            if(isLight)
                viewHolder.imgDelete.setBackgroundResource(R.drawable.ic_keyboard_delete_gray);
            else
                viewHolder.imgDelete.setBackgroundResource(R.drawable.ic_keyboard_delete_white);
            viewHolder.imgDelete.setVisibility(View.VISIBLE);
            viewHolder.btnKey.setBackgroundColor(mContext.getResources().getColor(R.color.functionKey));
        }
                        //   +                -                 C                 .                 确定
        else if (position == 7 || position == 11 || position == 12 || position == 14 || position == 15) {
            viewHolder.imgDelete.setVisibility(View.INVISIBLE);
            viewHolder.btnKey.setText(valueList.get(position));
            viewHolder.btnKey.setBackgroundColor(mContext.getResources().getColor(R.color.functionKey));
            if(position == 15)ok = viewHolder.btnKey;
        }
        else {
            viewHolder.imgDelete.setVisibility(View.INVISIBLE);
            viewHolder.btnKey.setText(valueList.get(position));
            viewHolder.btnKey.setBackgroundColor(mContext.getResources().getColor(R.color.numKey));
        }
        return convertView;
    }

    public void changeBtnOK(){
        ok.setText("确定");
    }

    public void changeBtnEqual(){
        ok.setText("=");
    }

    public String getBtnText(){
        return ok.getText().toString();
    }


    /**
     * 存放控件
     */
    public final class ViewHolder {
        public TextView btnKey;
        public ImageView imgDelete;
    }
}