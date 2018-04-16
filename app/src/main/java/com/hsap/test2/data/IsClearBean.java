package com.hsap.test2.data;

/**
 * Created by zhao on 2018/4/4.
 */

public class IsClearBean {
   private boolean isClear;
    public  IsClearBean(boolean isClear){
        this.isClear=isClear;
    }
    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }
}
