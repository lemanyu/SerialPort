package com.hsap.test2;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.google.gson.Gson;
import com.hsap.test2.data.Data;
import com.hsap.test2.data.IsClearBean;
import com.hsap.test2.data.MessageEvent;
import com.hsap.test2.utils.ContantsUtil;
import com.hsap.test2.utils.Date.DateStyle;
import com.hsap.test2.utils.Date.DateUtil;
import com.hsap.test2.utils.DeviceIdUtils;
import com.hsap.test2.utils.EnvironmentUtil;
import com.hsap.test2.utils.SleepUtil;
import com.hsap.test2.utils.SpUtils;
import com.hsap.test2.utils.ToastUtils;
import com.hsap.test2.view.CircleSeekBar;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.medica.jni.SleepAnalysis_Out;
import com.medica.jni.SleepHelperJni;
import com.medica.restonsdk.Constants;
import com.medica.restonsdk.bluetooth.RestOnHelper;
import com.medica.restonsdk.domain.BleDevice;
import com.medica.restonsdk.domain.Detail;
import com.medica.restonsdk.domain.RealTimeData;
import com.medica.restonsdk.domain.Summary;
import com.medica.restonsdk.interfs.Method;
import com.medica.restonsdk.interfs.RealtimeDataCallback;
import com.medica.restonsdk.interfs.ResultCallback;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DateFormat;
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

public class LeftFragment extends BaseFragmentPager implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, OnOpenSerialPortListener {
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
    @BindView(R.id.tv_zhuangtai)
    TextView tvZhuangtai;
    @BindView(R.id.tv_qiangdu)
    TextView tvQiangdu;
    Unbinder unbinder;
    @BindView(R.id.circleSeekBar)
    CircleSeekBar circleSeekBar;
    private RestOnHelper sleepHelper;
    private BleDevice device;
    private StringBuilder dateSB = new StringBuilder();
    private SerialPortManager mSerialPortManager;
    private SerialPortManager mSerialPortManager1;
    private SerialPortManager mSerialPortManager2;
    private ArrayList<Short> list = new ArrayList<>();
    private ArrayList<String> dataList;
    private String sendBegin = "A5 01 01 A7 5A";
    private String sendEnd = "A5 01 05 AB 5A";
    private String low = "A5 01 04 AA 5A";
    private String in = "A5 01 02 A8 5A";
    private String high = "A5 01 03 A9 5A";
    private String end="A50200000000A7AE";
    private String []diwen1={"AA 70 00 CC 33 C3 3C","AA 70 01 CC 33 C3 3C",
            "AA 70 02 CC 33 C3 3C","AA 70 03 CC 33 C3 3C","AA 70 04 CC 33 C3 3C",
            "AA 70 05 CC 33 C3 3C","AA 70 06 CC 33 C3 3C","AA 70 07 CC 33 C3 3C",
            "AA 70 08 CC 33 C3 3C","AA 70 09 CC 33 C3 3C","AA 70 10 CC 33 C3 3C"};
    private int[] strength_number = {1, 2, 3, 4, 5, 6, 7, 8};
    private int strength=4;
    private Device mDevice;
    private int j = 1;
    private int sum = 0;
    private int sumHeart;
    private int sumBreath;
    private int sumRoll;
    private int size;
    private boolean isEnd;
    private int diwenNuber;
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
                case 2:
                    if (size==100){
                        size=0;
                    }else {
                        size+=10;
                    }
                    circleSeekBar.setTextSize(size);
                    mHandler.sendEmptyMessageDelayed(2,500);
                    break;
                case 3:
                    isEnd=true;
                    mSerialPortManager1.sendBytes(EnvironmentUtil.HexString2Bytes(end
                            .length() % 2 == 1 ? end += "0" : end.replace(" ", "")));
                    mSerialPortManager2.sendBytes(EnvironmentUtil.HexString2Bytes(diwen1[0]
                            .length() % 2 == 1 ? diwen1[0] += "0" : diwen1[0].replace(" ", "")));
                    mHandler.removeMessages(3);
                    break;
                case 4:
                    mSerialPortManager2.sendBytes(EnvironmentUtil.HexString2Bytes(diwen1[diwenNuber]
                            .length() % 2 == 1 ? diwen1[diwenNuber] += "0" : diwen1[diwenNuber].replace(" ", "")));
                    diwenNuber+=1;
                    mHandler.sendEmptyMessageDelayed(4,1000*60*4);
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
        SerialPortFinder serialPortFinder = new SerialPortFinder();
        ArrayList<Device> devices = serialPortFinder.getDevices();
        for (Device device : devices) {
            if ("/dev/ttySAC1".equals(device.getFile().getPath())) {
                mDevice = device;
            }
        }
        Log.e(TAG, "initData: " + mSerialPortManager);
        sleepHourTextview.setText(0 + "");
        sleepMinutTextview.setText(0 + "");
        tvShiDu.setText(0 + "");
        tvWendu.setText(0 + "");
        tvPM25.setText(0 + "");
        tvPM10.setText(0 + "");
        disableRadioGroup(rgWind);
    }

    @Override
    public void initListener() {
        rgWind.setEnabled(false);
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
                if (mSerialPortManager != null) {
                    mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(low
                            .length() % 2 == 1 ? low += "0" : low.replace(" ", "")));
                }
                break;
            case R.id.rbInWind:
                ToastUtils.showToast(mActivity, "中风");
                if (mSerialPortManager != null) {
                    mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(in
                            .length() % 2 == 1 ? in += "0" : in.replace(" ", "")));
                }
                break;
            case R.id.rbHighWind:
                ToastUtils.showToast(mActivity, "高风");
                if (mSerialPortManager != null) {
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
        isEnd=false;
        if (mSerialPortManager == null) {
            openSerial();
            showStrengthDialog();
            opensubzero();
        }

        boolean sendBytes = mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(sendBegin
                .length() % 2 == 1 ? sendBegin += "0" : sendBegin.replace(" ", "")));
        enableRadioGroup(rgWind);
        sleepHelper = SleepUtil.open(mActivity);
        if (sleepHelper != null) {
            device = SleepUtil.linkBluetooth(sleepHelper, mHandler);
        }
        //Todo 发送低温
        mHandler.sendEmptyMessage(4);
    }

    private void opensubzero() {
        if (mSerialPortManager2==null){
            mSerialPortManager2=new SerialPortManager();
        }
        mSerialPortManager2.setOnOpenSerialPortListener(this).setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {

            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.e(TAG, "onDataSent:33333 "+bytes);
            }
        }).openSerialPort(new File("/dev/ttySAC3"),9600);
    }

    private AlertDialog alertDialog;

    private void showStrengthDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity, R.style.MyTheme);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view_strgenth, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        LoopView loopView = (LoopView) dialogView.findViewById(R.id.loopView);
        String[] timer_pick = {"一级", "二级", "三级", "四级", "五级", "六级", "七级", "八级"};

        // 滚动监听
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {

                strength = strength_number[index];
            }
        });

        dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvQiangdu.setText("治疗强度：" + strength);
                tvZhuangtai.setText("治疗状态：治疗中");
                openCure();
                updateCircle();
                alertDialog.dismiss();
            }
        });
//
        // 设置原始数据
        loopView.setItems(Arrays.asList(timer_pick));
        loopView.setInitPosition(3);
        strength = strength_number[3];
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void updateCircle() {
        mHandler.sendEmptyMessage(2);
    }

    private void openCure() {
            if (mSerialPortManager1==null){
                mSerialPortManager1=new SerialPortManager();
                openSerial1();
            }
       String cure= "A5010205"+intTohexString(strength)+"02"+Integer.toHexString(Integer.parseInt("A5",16)
                +Integer.parseInt("01",16)+Integer.parseInt("02",16)+Integer.parseInt("05",16)
                +Integer.parseInt(strength+"",16)+Integer.parseInt("02",16))+"AE";
        mSerialPortManager1.sendBytes(EnvironmentUtil.HexString2Bytes(cure
                .length() % 2 == 1 ? cure+= "0" : cure.replace(" ", "")));
       mHandler.sendEmptyMessageDelayed(3,1000*60*40);

    }

    private void openSerial1() {
        mSerialPortManager1.setOnOpenSerialPortListener(this).setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                Log.e(TAG, "onDataReceivedaaaaaaaaaaaaaa: "+bytes );
                String hexString = EnvironmentUtil.bytesToHexString(bytes);
                Log.e(TAG, "onDataReceivedaaaaaaaaaaaaaa: "+hexString );
                String substring = hexString.substring(2, 6);
                Log.e(TAG, "onDataReceivedaaaaaaaaaaa: "+substring );
                if ("0501".equals(substring)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("设备校验故障，请关机重启");
                    builder.show();
                }
                if ("0502".equals(substring)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("设备校验故障，请联系厂商");
                    builder.show();
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.e(TAG, "onDataSentbbbbbbbbbbbbbbbbbbbbbb: "+bytes );
            }
        }).openSerialPort(new File("/dev/ttySAC2"),9600);
    }

    //打开串口接受
    private void openSerial() {
        if (mSerialPortManager == null) {
            mSerialPortManager = new SerialPortManager();
        }
        boolean port = mSerialPortManager.setOnOpenSerialPortListener(this).setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(final byte[] bytes) {
                //TODO 接受的  更新温湿度空气质量
               String hexString = EnvironmentUtil.bytesToHexString(bytes);

                String substring = hexString.substring(0, 22);

                dataList = new ArrayList<>();

                for (int i = 0; i < substring.length() - 3; i += 2) {
                    dataList.add(substring.substring(i, 2 * j));
                    j++;
                }
                for (int i = 0; i < dataList.size(); i++) {
                    sum = sum + Integer.parseInt(dataList.get(i), 16);
                }
                String s1 = substring.substring(substring.length() - 2);
                String s2 = Integer.toHexString(sum).substring(Integer.toHexString(sum).length() - 2);


                if (s1.equals(s2)) {
                    //检验成功
                    String P1 = dataList.get(6);
                    String P2 = dataList.get(7);
                    String M1 = dataList.get(8);
                    String M2 = dataList.get(9);
                    final String s3 = dataList.get(2);//湿度高字节
                    final String s4 = dataList.get(3);//湿度低字节
                    final String s5 = dataList.get(4);//温度高字节
                    final String s6 = dataList.get(5);//温度低字节
                    final int x = Integer.parseInt(P1.substring(0, 1), 16);//低字节
                    final int y = Integer.parseInt(P1.substring(1, 2), 16);//低字节
                    final int a = Integer.parseInt(P2.substring(0, 1), 16);//高字节
                    final int b = Integer.parseInt(P2.substring(1, 2), 16);//高字节
                    final int i1 = Integer.parseInt(M1.substring(0, 1), 16);
                    final int i2 = Integer.parseInt(M1.substring(1, 2), 16);
                    final int i3 = Integer.parseInt(M2.substring(0, 1), 16);
                    final int i4 = Integer.parseInt(M2.substring(1, 2), 16);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvShiDu.setText(Integer.parseInt(s3, 16) + "." + Integer.parseInt(s4, 16));
                            tvWendu.setText(Integer.parseInt(s5, 16) + "." + Integer.parseInt(s6, 16));
                            tvPM25.setText(((a * 16 + b) * 256 + (x * 16 + y)) / 10 + "");
                            tvPM10.setText(((i3 * 16 + i4) * 256 + (i1 * 16 + i2)) / 10 + "");
                            //ToastUtils.showToast(mActivity,String.format("接收\n%s", new String(bytes)));
                        }
                    });
                } else {
                    Log.e(TAG, "onDataReceived: 错误了");
                }
                dataList.clear();
                j = 1;
                sum = 0;
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
                       // ToastUtils.showToast(mActivity, String.format("发送\n%s", new String(finalBytes)));
                    }
                });
            }
        }).openSerialPort(mDevice.getFile(), 9600);
        Log.e(TAG, "openSerial: " + port);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        Log.d(TAG, "onRequestPermissionsResult: " + Arrays.toString(permissions));
        Log.d(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
    }

    //关闭睡眠
    private void end() {
        if (!isEnd){
            mSerialPortManager1.sendBytes(EnvironmentUtil.HexString2Bytes(end
                    .length() % 2 == 1 ? end+= "0" : end.replace(" ", "")));
            mSerialPortManager1.sendBytes(EnvironmentUtil.HexString2Bytes(diwen1[0]
                    .length() % 2 == 1 ? diwen1[0]+= "0" : diwen1[0].replace(" ", "")));

        }
        //关闭串口
        if (null != mSerialPortManager) {
            mSerialPortManager.sendBytes(EnvironmentUtil.HexString2Bytes(sendEnd
                    .length() % 2 == 1 ? sendEnd += "0" : sendEnd.replace(" ", "")));
            mSerialPortManager.closeSerialPort();
            mSerialPortManager = null;
            disableRadioGroup(rgWind);
        }

        if (null != mSerialPortManager1) {
            mSerialPortManager1.closeSerialPort();
            mSerialPortManager1 = null;
            mHandler.removeMessages(2);
        }
        if (null != mSerialPortManager2) {
            mSerialPortManager1.closeSerialPort();
            mSerialPortManager1 = null;
            mHandler.removeMessages(4);
        }
        tvZhuangtai.setText("治疗状态：已结束");
        circleSeekBar.setTextSize(100);
        sleepState.setText("请连接设备");
        sleepCTLLTextview.setText(0 + "");
        sleepCTRLTextview.setText(0 + "");
        btBegin.setEnabled(true);
        String s = DateUtil.DateToString(new Date(), DateStyle.HH_MM);
        dateSB.append("~" + s);
        tvLeftSleep.setText(dateSB.toString());
        btEnd.setEnabled(false);
        totalTime(SpUtils.getString(ContantsUtil.BEGINTIME, mActivity), DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM));
        Detail detail = sleepHelper.queryHistoryDetail(new Summary());

        // ArrayList<Summary> summaries = sleepHelper.queryHistorySummary(SpUtils.getLong(ContantsUtil.Begin, mActivity).intValue(), new Long(System.currentTimeMillis()).intValue());
        Log.e(TAG, "end: " + detail.breathRate);
        Log.e(TAG, "end: " + detail.heartRate);
        //TODO 提交数据
        int len = Integer.valueOf(sleepHourTextview.getText().toString()) * 60 + Integer.valueOf(sleepMinutTextview.getText().toString());

        SleepAnalysis_Out out =
                SleepHelperJni.analysis(
                        SpUtils.getLong(ContantsUtil.Begin, mActivity).intValue(),
                        8 * 60 * 60, SpUtils.getInt(ContantsUtil.Sex, mActivity), 60, len,
                        detail.breathRate, detail.heartRate, detail.status, detail.statusValue, Constants.DEVICE_TYPE_RESTON_Z2
                );
        Log.e(TAG, "end: " + out.MdAverBreathRate);
        Log.e(TAG, "end: " + out.MdAverHeartRate);
        Log.e(TAG, "end: " + out.sleepScore);
        final LoadingDailog dailog = ToastUtils.showDailog(mActivity, "提交报告中...");
        dailog.show();

        OkGo.<String>post(ContantsUtil.endSleep)
                .params("deviceId", DeviceIdUtils.getDeviceId(mActivity))
                .params("mdFallasleepTime", Integer.valueOf(out.MdFallasleepTime))
                .params("userId", SpUtils.getInt(ContantsUtil.userId, mActivity))
                .params("json", new Gson().toJson(out))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e(TAG, "onSuccess: " + response.body().toString());
                        dailog.dismiss();
                    }

                    @Override
                    public void onError(Response<String> response) {

                        dailog.dismiss();
                    }
                });

        //登出设备
        SleepUtil.logout(sleepHelper);
        SleepUtil.endSleep(mActivity, sleepHelper);
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
        sleepHourTextview.setText((day * 24 + hour) + "");
        sleepMinutTextview.setText(min + "");
    }

    private void seeData() {
        btBegin.setEnabled(false);
        Log.e(TAG, "begin: ");
        btEnd.setEnabled(true);
        SpUtils.putLong(ContantsUtil.Begin, System.currentTimeMillis(), mActivity);
        Date date = new Date();
        String s = DateUtil.DateToString(date, DateStyle.HH_MM);
        SpUtils.putString(ContantsUtil.BEGINTIME, DateUtil.DateToString(date, DateStyle.YYYY_MM_DD_HH_MM), mActivity);
        dateSB.setLength(0);
        dateSB.append(s);
        tvLeftSleep.setText(dateSB.toString());
        Log.e(TAG, "seeData: " + s);
        sleepHelper.seeRealtimeData(new RealtimeDataCallback() {
            @Override
            public void handleRealtimeData(final RealTimeData realTimeData) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sleepCTLLTextview.setText(realTimeData.heartRate + "");
                        sleepCTRLTextview.setText(realTimeData.breathRate + "");
                        sleepState.setText(SleepUtil.getSleepState(realTimeData.status));
                        //发送
                        list.add(realTimeData.heartRate);
                        sumHeart += realTimeData.heartRate;
                        sumBreath += realTimeData.breathRate;
                        if (realTimeData.status == Constants.SleepStatusType.SLEEP_TURN_OVER) {
                            sumRoll++;
                        }
                        if (list.size() == 60) {
                            EventBus.getDefault().post(new Data(sumHeart / 60, sumBreath / 60, sumRoll));
                            list.clear();
                            sumHeart = 0;
                            sumBreath = 0;
                            sumRoll = 0;
                        }

                    }
                });
            }

            @Override
            public void onResult(Method method, final Object o) {
                Calendar calendar = Calendar.getInstance();
                if ((boolean) o) {
                    EventBus.getDefault().post(new MessageEvent(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                    EventBus.getDefault().post(new IsClearBean(true));
                }
            }
        });
    }

    //连接成功 登录设备
    private void loginBleDevice() {
        sleepHelper.loginDevice(device, SpUtils.getInt(ContantsUtil.userId, mActivity), new ResultCallback() {
            @Override
            public void onResult(Method method, Object o) {
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


    @Override
    public void onSuccess(File file) {
        Log.e(TAG, "onSuccess: "+file.getPath());
    }

    @Override
    public void onFail(File file, Status status) {
        Log.e(TAG, "onFail: ");
        switch (status) {
            case NO_READ_WRITE_PERMISSION:
                ToastUtils.showToast(mActivity, "没有读写权限");
                break;
            case OPEN_FAIL:
            default:
                ToastUtils.showToast(mActivity, "串口打开失败");
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void disableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    public void enableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(true);
        }
    }
    public  String intTohexString(int n){
        if (n<16) {
            return "0" + Integer.toHexString(n);
        }
        return ""+Integer.toHexString(n);
    }
}
