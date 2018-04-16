package com.hsap.test2.receiver;

/**
 * Created by zhao on 2018/3/27.
 */

public class ContentBean {

    /**
     * sleepDetailId : 5
     * sleepAnalysisOutId : 5
     * summaryId : 5
     * userId : 29
     */

    private int sleepDetailId;
    private int sleepAnalysisOutId;
    private int summaryId;
    private int userId;
    private int sex;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getSleepDetailId() {
        return sleepDetailId;
    }

    public void setSleepDetailId(int sleepDetailId) {
        this.sleepDetailId = sleepDetailId;
    }

    public int getSleepAnalysisOutId() {
        return sleepAnalysisOutId;
    }

    public void setSleepAnalysisOutId(int sleepAnalysisOutId) {
        this.sleepAnalysisOutId = sleepAnalysisOutId;
    }

    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
