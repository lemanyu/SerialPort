package com.hsap.test2.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.medica.restonsdk.Constants;
import com.medica.restonsdk.bluetooth.RestOnHelper;
import com.medica.restonsdk.domain.BleDevice;
import com.medica.restonsdk.interfs.BleScanListener;
import com.medica.restonsdk.interfs.Method;
import com.medica.restonsdk.interfs.ResultCallback;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dongsh on 2018/2/3.
 */
public class SleepUtil {

    //开启睡眠
    private static boolean flag;
    private static Activity activity;
    private static BleDevice bleDevice;

    public static RestOnHelper beginSleep(Activity activity) {
        SleepUtil.activity = activity;
        RestOnHelper helper = RestOnHelper.getInstance(activity);
        flag = false;
        if (helper.isSupportBle()) {
            if (!helper.isBluetoothOpen()) {
                if (Build.VERSION.SDK_INT >= 23) {
                    //申请蓝牙权限
                    AndPermission.with(activity).permission(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION).
                            onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            flag = true;
                        }
                    }).onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            flag = false;
                        }
                    });
                } else {
                    helper.openBluetooth();
                    flag = true;
                }
            } else {
                flag = true;
            }

            if (flag) {

            } else {
                Toast.makeText(activity, "蓝牙权限开启失败，请手动开启蓝牙", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "当前设备不支持BLE", Toast.LENGTH_SHORT).show();
        }

        return helper;
    }

    //搜索链接设备
    public static BleDevice linkBluetooth(final RestOnHelper helper, final Handler mHandler) {
        final LoadingDialog dialog = new LoadingDialog(activity);
        dialog.setLoadingText("获取关联中").setSuccessText("连接成功").setFailedText("连接失败").setInterceptBack(true).setLoadSpeed(LoadingDialog.Speed.SPEED_TWO).setRepeatCount(0);

        helper.scanBleDevice(new BleScanListener() {
            @Override
            public void onBleScanStart() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.show();
                    }
                });
            }

            @Override
            public void onBleScan(BleDevice bleDevice) {
                Message message = mHandler.obtainMessage();
                message.what = 99;
                message.obj = bleDevice;
                mHandler.sendMessage(message);
            }

            @Override
            public void onBleScanFinish() {
                //现在一个，后期删除加入选择
                helper.connDevice(new ResultCallback() {
                    @Override
                    public void onResult(Method method, Object o) {
                        switch (helper.getConnectState()) {
                            case 0:
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.loadFailed();
                                }
                            });
                                break;
                            case 1:
                                dialog.close();
                                Toast.makeText(activity, "连接超时", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.loadSuccess();
                                    }
                                });
                                mHandler.sendEmptyMessage(1);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
        return bleDevice;
    }

    public static RestOnHelper endSleep(Activity activity, RestOnHelper helper) {
        final LoadingDialog dialog = new LoadingDialog(activity);
        dialog.setLoadingText("正在关闭").setSuccessText("关闭成功").setFailedText("关闭失败").setInterceptBack(true).setLoadSpeed(LoadingDialog.Speed.SPEED_TWO).setRepeatCount(0).show();
        if (helper != null) {
            switch (helper.getConnectState()) {
                case 0:
                    helper = null;
                    dialog.loadSuccess();
                    break;
                case 1:
                    helper.stopScan();
                    helper = null;
                    dialog.loadSuccess();
                    break;
                case 2:
                    helper.disconnect();
                    helper = null;
                    dialog.loadSuccess();

                    break;
                default:
                    dialog.loadFailed();
                    ToastUtils.showToast(activity, "请关闭蓝牙");
                    break;
            }
        } else {
            dialog.close();
            ToastUtils.showToast(activity, "当前未开启睡眠");
        }
        return helper;
    }

    public static String getSleepState(byte status) {
        String str;
        switch (status) {
            case Constants.SleepStatusType.SLEEP_OK:
                str = "正常";
                break;
            case Constants.SleepStatusType.SLEEP_INIT:
                str = "初始化中";
                break;
            case Constants.SleepStatusType.SLEEP_B_STOP:
                str = "呼吸暂停";
                break;
            case Constants.SleepStatusType.SLEEP_H_STOP:
                str = "心跳暂停";
                break;
            case Constants.SleepStatusType.SLEEP_BODYMOVE:
                str = "体动";
                break;
            case Constants.SleepStatusType.SLEEP_LEAVE:
                str = "离床";
                break;
            case Constants.SleepStatusType.SLEEP_TURN_OVER:
                str = "翻身";
                break;
            case Constants.SleepStatusType.SLEEP_BODYMOVE_TEMP:
                str = "体动振幅";
                break;
            case Constants.SleepStatusType.SLEEP_INVALID:
                str = "无效";
                break;
            default:
                str = "错误";
        }
        return str;
    }
    /** 两个时间相差距离多少天多少小时多少分 */
    public static String getDistanceTime(String str1, String str2, String pattern) {
        if (pattern == null || pattern.equals("")) {
            pattern = "yyyy-MM-dd HH:mm";
        }
        DateFormat df = new SimpleDateFormat(pattern);
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            Date one = df.parse(str1);
            Date two = df.parse(str2);
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
        if(day==0){
            return  hour + "小时" + min + "分" ;
        }else {
            return day + "天" + hour + "小时" + min + "分";
        }
    }
}
