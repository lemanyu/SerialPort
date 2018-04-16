package com.hsap.test2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.hsap.test2.receiver.ContentBean;
import com.hsap.test2.utils.ActivityManagerUtils;
import com.hsap.test2.utils.ContantsUtil;
import com.hsap.test2.utils.DeviceIdUtils;
import com.hsap.test2.utils.SpUtils;
import com.hsap.test2.utils.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhao on 2018/3/6.
 */

public class HomeActivity extends FragmentActivity {
    private static final String TAG = "HomeActivity";
    @BindView(R.id.mImageView)
    ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        ActivityManagerUtils.getInstance().addActivity(this);
        OkGo.<String>post(ContantsUtil.addDrvice).
                params("derivcId", DeviceIdUtils.getDeviceId(getApplicationContext())).
                params("token",XGPushConfig.getToken(getApplicationContext())).
                execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e(TAG, "onSuccess: "+ response.body().toString());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        ToastUtils.showToast(getApplicationContext(),"服务器错误，请关闭重新打开");
                    }
                });

        if (!isWifiEnabled(this)){
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.Settings");
            startActivity(intent);
        }
        initView();
    }


    private void initView() {
        mImageView.setBackground(null);
        Bitmap bitmap = CodeUtils.createImage(DeviceIdUtils.getDeviceId(getApplicationContext()), 200, 200, null);
        mImageView.setImageBitmap(bitmap);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
    public static boolean isWifiEnabled(Context context) {
        ConnectivityManager mgrConn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mgrTel = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

}
