package com.swjtu.huxin.accountmanagement.base;

import java.util.Observable;

/**
 * Created by huxin on 2017/5/5.
 */

public class DataChangeObservable extends Observable {
    public void dataChange(){
        setChanged();
    }
}
