package com.swjtu.huxin.accountmanagement.base;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.swjtu.huxin.accountmanagement.view.NumKeyboardView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by huxin on 2017/4/8.
 */

public class OnNumKeyboardItemClickListener implements AdapterView.OnItemClickListener {
    private NumKeyboardView numKeyboardView;
    private ArrayList<String> valueList;
    private TextView keyNum;
    private TextView keySymbol;
    private OnItemClickListener mOnItemClickListener;
    private boolean isInteger = true;//是否输入整数
    private int numDecimal = 0;//已输入的小数位数

    public OnNumKeyboardItemClickListener(NumKeyboardView numKeyboardView, TextView keyNum, TextView keySymbol,OnItemClickListener clickListener){
        this.numKeyboardView = numKeyboardView;
        this.valueList = this.numKeyboardView.getValueList();
        this.keyNum = keyNum;
        this.keySymbol = keySymbol;
        keySymbol.setText("");
        this.mOnItemClickListener = clickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String amount;
        if(numKeyboardView.isZero()) {
            amount = "0.00";
            numKeyboardView.setZero(false);
        }
        else {
            amount = keyNum.getText().toString().trim();
        }
        if (position < 14 && position != 3 && position != 7 && position != 11 && position != 12) {    //点击0~9按钮
            if(isInteger) {
                if(amount.charAt(0) == '0'){
                    amount = valueList.get(position) + amount.substring(1);
                }
                else{
                    amount = amount.substring(0,amount.length()-3) + valueList.get(position) + amount.substring(amount.length()-3);
                }
            }
            else{
                if(numDecimal == 0){
                    amount = amount.substring(0,amount.length()-2) + valueList.get(position) + "0";
                    numDecimal++;
                }
                else if(numDecimal == 1){
                    amount = amount.substring(0,amount.length()-1) + valueList.get(position);
                    numDecimal++;
                }
            }
        }
        else {
            if (position == 7) {      //点击+
                if(numKeyboardView.isAddSymbol()){
                    amount = (new BigDecimal(numKeyboardView.getOldAmount()).add(new BigDecimal(amount))).toString();
                    numKeyboardView.setZero(true);
                    numKeyboardView.setOldAmount(amount);
                    keyNum.setText(amount);
                }
                else if(numKeyboardView.isSubtractSymbol()){
                    numKeyboardView.setSubtractSymbol(false);
                    numKeyboardView.setAddSymbol(true);
                    keySymbol.setText("+");
                    amount = (new BigDecimal(numKeyboardView.getOldAmount()).subtract(new BigDecimal(amount))).toString();
                    if(new BigDecimal(amount).doubleValue()<0) amount = "0.00";
                    numKeyboardView.setZero(true);
                    numKeyboardView.setOldAmount(amount);
                    keyNum.setText(amount);
                }
                else{
                    keySymbol.setText("+");
                    numKeyboardView.changeBtnEqual();
                    numKeyboardView.setAddSymbol(true);
                    numKeyboardView.setZero(true);
                    numKeyboardView.setOldAmount(amount);
                }
                return;
            }
            if (position == 11) {      //点击—
                if(numKeyboardView.isSubtractSymbol()){
                    amount = (new BigDecimal(numKeyboardView.getOldAmount()).subtract(new BigDecimal(amount))).toString();
                    if(new BigDecimal(amount).doubleValue()<0) amount = "0.00";
                    numKeyboardView.setZero(true);
                    numKeyboardView.setOldAmount(amount);
                    keyNum.setText(amount);
                }
                else if(numKeyboardView.isAddSymbol()){
                    numKeyboardView.setAddSymbol(false);
                    numKeyboardView.setSubtractSymbol(true);
                    keySymbol.setText("-");
                    amount = (new BigDecimal(numKeyboardView.getOldAmount()).add(new BigDecimal(amount))).toString();
                    numKeyboardView.setZero(true);
                    numKeyboardView.setOldAmount(amount);
                    keyNum.setText(amount);
                }
                else{
                    keySymbol.setText("-");
                    numKeyboardView.changeBtnEqual();
                    numKeyboardView.setSubtractSymbol(true);
                    numKeyboardView.setZero(true);
                    numKeyboardView.setOldAmount(amount);
                }
                return;
            }
            if (position == 14) {      //点击小数点
                isInteger = isInteger==true?false:true;
            }
            if (position == 3) {      //点击退格键
                if(isInteger){
                    if(amount.charAt(0) != '0'){
                        amount = amount.substring(0, amount.length() - 4) + amount.substring(amount.length() - 3);
                    }
                    if(amount.length() == 3){
                        amount = "0"+amount;
                    }
                    if(amount.length() == 4 && amount.charAt(0) == '0' && numDecimal != 0){
                        isInteger = false;
                    }
                }
                else{
                    if (numDecimal == 0) {
                        isInteger = true;
                    }
                    else if(numDecimal == 1){
                        amount = amount.substring(0,amount.length()-2) + "00";
                        numDecimal--;
                    }
                    else if(numDecimal == 2){
                        amount = amount.substring(0,amount.length()-1) + "0";
                        numDecimal--;
                    }
                }
            }
            if (position == 12) {      //点击C
                amount = "0.00";
                if(numKeyboardView.isAddSymbol()){
                    keySymbol.setText("");
                    numKeyboardView.changeBtnOK();
                    numKeyboardView.setAddSymbol(false);
                }
                if(numKeyboardView.isSubtractSymbol()){
                    keySymbol.setText("");
                    numKeyboardView.changeBtnOK();
                    numKeyboardView.setSubtractSymbol(false);
                }
            }
            if (position == 15) {      //点击确定
                if(numKeyboardView.isAddSymbol()){
                    amount = (new BigDecimal(numKeyboardView.getOldAmount()).add(new BigDecimal(amount))).toString();
                    keySymbol.setText("");
                    numKeyboardView.changeBtnOK();
                    numKeyboardView.setAddSymbol(false);
                }
                else if(numKeyboardView.isSubtractSymbol()){
                    amount = (new BigDecimal(numKeyboardView.getOldAmount()).subtract(new BigDecimal(amount))).toString();
                    if(new BigDecimal(amount).doubleValue()<0) amount = "0.00";
                    keySymbol.setText("");
                    numKeyboardView.changeBtnOK();
                    numKeyboardView.setSubtractSymbol(false);
                }
                else {
                    mOnItemClickListener.onClick(view,15,"");
                }
            }
        }
        keyNum.setText(amount);
    }
}
