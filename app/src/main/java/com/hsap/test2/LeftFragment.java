package com.hsap.test2;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hsap.test2.data.Data;
import com.hsap.test2.data.MessageEvent;
import com.hsap.test2.utils.ContantsUtil;
import com.hsap.test2.utils.Date.DateStyle;
import com.hsap.test2.utils.Date.DateUtil;
import com.hsap.test2.utils.EnvironmentUtil;
import com.hsap.test2.utils.SleepUtil;
import com.hsap.test2.utils.SpUtils;
import com.hsap.test2.utils.ToastUtils;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.medica.jni.SleepAnalysis_Out;
import com.medica.jni.SleepHelperJni;
import com.medica.restonsdk.Constants;
import com.medica.restonsdk.bluetooth.RestOnHelper;
import com.medica.restonsdk.domain.BleDevice;
import com.medica.restonsdk.domain.RealTimeData;
import com.medica.restonsdk.interfs.Method;
import com.medica.restonsdk.interfs.RealtimeDataCallback;
import com.medica.restonsdk.interfs.ResultCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by zhao on 2018/2/3.
 */

public class LeftFragment extends BaseFragmentPager implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{
    private final String TAG = "LeftFragment";
    @BindView(R.id.rbLowWind)
    RadioButton rbLowWind;
    @BindView(R.id.rbInWind)
    RadioButton rbInWind;
    @BindView(R.id.rbHighWind)
    RadioButton rbHighWind;
    @BindView(R.id.rgWind)
    RadioGroup rgWind;
    @BindView(R.id.btBegin)
    Button btBegin;
    @BindView(R.id.btEnd)
    Button btEnd;
    @BindView(R.id.sleep_c_t_l_l_textview)
    TextView sleepCTLLTextview;
    @BindView(R.id.sleep_state)
    TextView sleepState;
    @BindView(R.id.sleep_c_t_r_l_textview)
    TextView sleepCTRLTextview;
    @BindView(R.id.tvLeftSleep)
    TextView tvLeftSleep;
    @BindView(R.id.sleep_hour_textview)
    TextView sleepHourTextview;
    @BindView(R.id.sleep_minut_textview)
    TextView sleepMinutTextview;
    @BindView(R.id.tvWendu)
    TextView tvWendu;
    @BindView(R.id.tvShiDu)
    TextView tvShiDu;
    @BindView(R.id.tvPM25)
    TextView tvPM25;
    @BindView(R.id.tvPM10)
    TextView tvPM10;
    private RestOnHelper sleepHelper;
    private BleDevice device;
    private StringBuilder dateSB = new StringBuilder();
    private SerialPortManager mSerialPortManager;
    private ArrayList<Short> list=new ArrayList<>();
    private ArrayList<String> dataList;
    private String sendBegin="A5 01 01 A7 5A";
    private String sendEnd="A5 01 05 AB 5A";
    private String low="A5 01 02 A8 5A";
    private String in="A5 01 03 A9 5A";
    private String high="A5 01 04 AA 5A";
    private int j=1;
    private int sum=0;
    private int sumHeart;
    private int sumBreath;
    private int sumRoll;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //实时数据
                    seeData();
                    break;
                case 1:
                    loginBleDevice();
                    break;
                case 99:
                    device = (BleDevice) msg.obj;
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_left, null);
        return view;
    }


    @Override
    public void initData() {
        sleepHourTextview.setText(0+"");
        sleepMinutTextview.setText(0+"");
        tvShiDu.setText(0+"");
        tvWendu.setText(0+"");
        tvPM25.setText(0+"");
        tvPM10.setText(0+"");
        openSerial();
    }

    @Override
    public void initListener() {
        btEnd.setEnabled(false);
        rbLowWind.setChecked(true);
        rgWind.setOnCheckedChangeListener(this);
        btBegin.setOnClickListener(this);
        btEnd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btBegin:
                begin();
                break;
            case R.id.btEnd:
                end();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rbLowWind:
                ToastUtils.showToast(mActivity, "低风");
                if (mSerialPortManager!=null){
                    mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(low
                            .length() % 2 == 1 ? low += "0" : low.replace(" ", "")));
                }
                break;
            case R.id.rbInWind:
                ToastUtils.showToast(mActivity, "中风");
                if (mSerialPortManager!=null){
                    mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(in
                            .length() % 2 == 1 ? in += "0" : in.replace(" ", "")));
                }
                break;
            case R.id.rbHighWind:
                ToastUtils.showToast(mActivity, "高风");
                if (mSerialPortManager!=null){
                    mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(high
                            .length() % 2 == 1 ? high += "0" : high.replace(" ", "")));
                }
                break;
            default:
                break;
        }
    }

    //开启睡眠
    private void begin() {
        openSerial();
        sleepHelper = SleepUtil.beginSleep(mActivity);
        device = SleepUtil.linkBluetooth(sleepHelper, mHandler);

    }
    //打开串口接受
    private void openSerial() {
        if (mSerialPortManager==null){
            mSerialPortManager=new SerialPortManager();
        }
        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(final byte[] bytes) {
                //TODO 接受的  更新温湿度空气质量
                Log.e(TAG, "onDataReceived [ byte[] ]: " + Arrays.toString(bytes));
                Log.e(TAG, "onDataReceived [ String ]: " + new String(bytes));
                Log.e(TAG, "onDataReceived: "+EnvironmentUtil.bytesToHexString(bytes));
                String hexString = EnvironmentUtil.bytesToHexString(bytes);

                String substring = hexString.substring(0, 22);

                dataList = new ArrayList<>();

                for(int i=0;i<substring.length()-3;i+=2){
                    Log.e(TAG, "onDataReceived: iiiiiii"+i);
                    Log.e(TAG, "onDataReceived: jjjjjjjjjjj"+j );
                    dataList.add(substring.substring(i,2*j));
                    j++;
                }
                for (int i = 0; i <dataList.size(); i++) {
                    Log.e(TAG, "onDataReceived: aaaaaaaa"+Integer.parseInt(dataList.get(i),16));
                    sum=sum+Integer.parseInt(dataList.get(i),16);
                }
                String s1 =substring.substring(substring.length() - 2);
                Log.e(TAG, "s1    "+s1 );
                String s2 = Integer.toHexString(sum).substring(Integer.toHexString(sum).length()-2);
                Log.e(TAG, "s2      "+s2 );
                if (s1.equals(s2)){
                    //检验成功
                    String P1 = dataList.get(6);
                    String P2=dataList.get(7);
                    String M1=dataList.get(8);
                    String M2=dataList.get(9);
                    final String s3 = dataList.get(2);//湿度高字节
                    final String s4 = dataList.get(3);//湿度低字节
                    final String s5 = dataList.get(4);//温度高字节
                    final String s6 = dataList.get(5);//温度低字节
                    final int x=Integer.parseInt(P1.substring(0,1),16);//低字节
                    final int y=Integer.parseInt(P1.substring(1,2),16);//低字节
                    final int a=Integer.parseInt(P2.substring(0,1),16);//高字节
                    final int b=Integer.parseInt(P2.substring(1,2),16);//高字节
                    final int i1 = Integer.parseInt(M1.substring(0, 1), 16);
                    final int i2 = Integer.parseInt(M1.substring(1, 2), 16);
                    final int i3 = Integer.parseInt(M2.substring(0, 1), 16);
                    final int i4 = Integer.parseInt(M2.substring(1, 2), 16);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvShiDu.setText(Integer.parseInt(s3,16)+"."+Integer.parseInt(s4,16));
                            tvWendu.setText(Integer.parseInt(s5,16)+"."+Integer.parseInt(s6,16));
                            tvPM25.setText(((a*16+b)*256+(x*16+y))/10+"");
                            tvPM10.setText(((i3*16+i4)*256+(i1*16+i2))/10+"");
                            //ToastUtils.showToast(mActivity,String.format("接收\n%s", new String(bytes)));
                        }
                    });
                }else {
                    Log.e(TAG, "onDataReceived: 错误了" );
                }
                dataList.clear();
                j=1;
                sum=0;
            }

            @Override
            public void onDataSent(byte[] bytes) {
                //TODO 数据的发送  中低高风
                Log.i(TAG, "onDataSent [ byte[] ]: " + Arrays.toString(bytes));
                Log.i(TAG, "onDataSent [ String ]: " + new String(bytes));
                final byte[] finalBytes = bytes;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //ToastUtils.showToast(mActivity,String.format("发送\n%s", new String(finalBytes)));
                    }
                });
            }
        }).openSerialPort(new File("/dev/ttySAC1"),9600);
        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File file) {
                Log.e(TAG, "onSuccess: " );
              mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(sendBegin
                      .length() % 2 == 1 ? sendBegin += "0" : sendBegin.replace(" ", "")));

            }

            @Override
            public void onFail(File file, Status status) {
                Log.e(TAG, "onFail: " );
                switch (status) {
                    case NO_READ_WRITE_PERMISSION:
                        ToastUtils.showToast(mActivity,"没有读写权限");
                        break;
                    case OPEN_FAIL:
                    default:
                        ToastUtils.showToast(mActivity,"串口打开失败");
                        break;
                }
            }
        });

    }

    //关闭睡眠
    private void end() {
        //关闭串口
        if (null!=mSerialPortManager){
            mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(sendEnd
                    .length() % 2 == 1 ? sendEnd += "0" : sendEnd.replace(" ", "")));
            mSerialPortManager.closeSerialPort();
            mSerialPortManager=null;
        }
        SleepUtil.endSleep(mActivity, sleepHelper);
        sleepState.setText("请连接设备");
        sleepCTLLTextview.setText(0 + "");
        sleepCTRLTextview.setText(0 + "");
        btBegin.setEnabled(true);
        String s = DateUtil.DateToString(new Date(), DateStyle.HH_MM);
        dateSB.append("~" + s);
        tvLeftSleep.setText(dateSB.toString());
        btEnd.setEnabled(false);
        totalTime(SpUtils.getString(ContantsUtil.BEGINTIME,mActivity), DateUtil.DateToString(new Date(),DateStyle.YYYY_MM_DD_HH_MM));
    }

    private void totalTime(String beginTime, String endTime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            Date one = df.parse(beginTime);
            Date two = df.parse(endTime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleepHourTextview.setText((day*24+hour)+"");
        sleepMinutTextview.setText(min+"");
    }

    private void seeData() {
        btBegin.setEnabled(false);
        Log.e(TAG, "begin: ");
        btEnd.setEnabled(true);
        Date date = new Date();
        String s = DateUtil.DateToString(date, DateStyle.HH_MM);
        SpUtils.putString(ContantsUtil.BEGINTIME,DateUtil.DateToString(date,DateStyle.YYYY_MM_DD_HH_MM),mActivity);
        dateSB.setLength(0);
        dateSB.append(s);
        tvLeftSleep.setText(dateSB.toString());
        Log.e(TAG, "seeData: " + s);
        sleepHelper.seeRealtimeData(new RealtimeDataCallback() {
            @Override
            public void handleRealtimeData(final RealTimeData realTimeData) {
                Log.e(TAG, "handleRealtimeData: " + realTimeData.toString());
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sleepCTLLTextview.setText(realTimeData.heartRate + "");
                        sleepCTRLTextview.setText(realTimeData.breathRate + "");
                        sleepState.setText(SleepUtil.getSleepState(realTimeData.status));
                        //发送
                        list.add(realTimeData.heartRate);
                        sumHeart+=realTimeData.heartRate;
                        sumBreath+=realTimeData.breathRate;
                        if (realTimeData.status==Constants.SleepStatusType.SLEEP_TURN_OVER){
                           sumRoll++;
                        }
                        if (list.size()==60){
                            EventBus.getDefault().post(new Data(sumHeart/60,sumBreath/60,sumRoll));
                            list.clear();
                            sumHeart=0;
                            sumBreath=0;
                            sumRoll=0;
                        }

                    }
                });
            }

            @Override
            public void onResult(Method method, final Object o) {
                Log.e(TAG, "onResult: -----------" + o.toString());
                Log.d(TAG, "onResult: " + method);
                Calendar calendar = Calendar.getInstance();
                if ((boolean)o){
                    EventBus.getDefault().post(new MessageEvent(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
                }
            }
        });
    }

    //连接成功 登录设备
    private void loginBleDevice() {
        sleepHelper.loginDevice(device, 1, new ResultCallback() {
            @Override
            public void onResult(Method method, Object o) {
                Log.d(TAG, "onResult: loginDevice" + method);
                Log.d(TAG, "onResult: loginDevice" + o);
            }
        });
        //开始采集
        sleepHelper.startCollect(new ResultCallback() {
            @Override
            public void onResult(Method method, Object o) {
                Log.e(TAG, "onResult: " + o.toString());
                if ((boolean) o) {
                    mHandler.sendEmptyMessage(0);
                } else {
                    ToastUtils.showToast(mActivity, "采集失败，请重新开启睡眠");
                }
            }
        });
    }


}
