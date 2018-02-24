package com.hsap.test2.data;

import com.medica.restonsdk.domain.RealTimeData;

import java.util.Date;

/**
 * Created by zhao on 2018/2/8.
 */

public class Data {

    public int sumHeart;
    public int sumBreath;
    public int sumRoll;
    public Data(int sumHeart,int sumBreath,int sumRoll){
        this.sumHeart=sumHeart;
        this.sumBreath=sumBreath;
        this.sumRoll=sumRoll;
    }

    public int getSumRoll() {
        return sumRoll;
    }

    public void setSumRoll(int sumRoll) {
        this.sumRoll = sumRoll;
    }

    public int getSumBreath() {
        return sumBreath;
    }

    public void setSumBreath(int sumBreath) {
        this.sumBreath = sumBreath;
    }

    public int getSumHeart() {
        return sumHeart;
    }

    public void setSumHeart(int sumHeart) {
        this.sumHeart = sumHeart;
    }
}
