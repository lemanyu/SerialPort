package com.hsap.test2;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hsap.test2.data.Data;
import com.hsap.test2.data.MessageEvent;
import com.hsap.test2.view.TableView;
import com.medica.restonsdk.Constants;
import com.medica.restonsdk.domain.RealTimeData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by zhao on 2018/2/3.
 */

public class RightFragment extends BaseFragmentPager {
   private final String TAG="RightFragment";
    @BindView(R.id.tbvHeart)
    TableView tbvHeart;
    @BindView(R.id.tbvBreathing)
    TableView tbvBreathing;
    @BindView(R.id.tvbRoll)
    TableView tbvRoll;
    private int x=0;
    private LinkedList<Point> heartList=new LinkedList<>();
    private LinkedList<Point> breathList=new LinkedList<>();
    private LinkedList<Point> rollList=new LinkedList<>();
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_right, null);
        return view;
    }

    @Override
    public void initData() {
        LinkedList<Point> list = new LinkedList<>();
        list.add(new Point(0, 50));
        list.add(new Point(1, 60));
        list.add(new Point(2, 160));
        list.add(new Point(3, 90));
        list.add(new Point(4, 30));
        list.add(new Point(5, 120));
        list.add(new Point(6, 180));
        list.add(new Point(7, 10));
        list.add(new Point(8, 110));
        list.add(new Point(9, 110));
        list.add(new Point(10, 110));
        list.add(new Point(11, 110));
        list.add(new Point(12, 110));
        list.add(new Point(13, 110));

    }

    @Override
    public void initListener() {

    }

    @Override
    @Subscribe
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    @Subscribe
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        /* Do something */
        tbvHeart.startHour=event.getHour();
        tbvHeart.startMinute=event.getMinute();
        tbvBreathing.startHour=event.getHour();
        tbvBreathing.startMinute=event.getMinute();
        tbvRoll.startHour=event.getHour();
        tbvRoll.startMinute=event.getMinute();
       // heartList.add(new Point(x,(int) event.getData().heartRate));


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageData(Data event) {
        Log.e(TAG, "onMessageData: xxxxxxx--------"+x );
        Log.e(TAG, "onMessageData: SumHeart------"+event.getSumHeart());
        heartList.add(new Point(x,event.getSumHeart()));
        tbvHeart.setPointList(heartList);
        Log.e(TAG, "onMessageData: SumBreath-----"+event.getSumBreath());
        breathList.add(new Point(x,event.getSumBreath()));
        tbvBreathing.setPointList(breathList);
        Log.e(TAG, "onMessageData: SumRoll-----"+event.getSumRoll());
        rollList.add(new Point(x,event.getSumRoll()));
        tbvRoll.setPointList(rollList);
        x++;
    }
}
