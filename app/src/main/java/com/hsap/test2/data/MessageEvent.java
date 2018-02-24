package com.hsap.test2.data;

import com.medica.restonsdk.domain.RealTimeData;

/**
 * Created by zhao on 2018/2/6.
 */

 public class MessageEvent {
    public int hour;
    public int minute;
    public RealTimeData data;

    public MessageEvent(int hour, int minute){
        this.hour=hour;
        this.minute=minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


}
