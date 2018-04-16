package com.hsap.test2;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.hsap.test2.utils.ContantsUtil;
import com.hsap.test2.utils.DeviceIdUtils;
import com.hsap.test2.utils.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by zhao on 2018/3/6.
 */

public class MyApp extends Application {
    private String TAG="MyApp";
    @Override
    public void onCreate() {
        super.onCreate();
        initPush();
        initOkgo();
       // AddWifiConfig(this,"HSAP_YANFA","hsap1234");
    }

    private void initWork() {

    }
    public  void AddWifiConfig(Context context, String wifiname , String pwd) {//第二个参数是账号名称，也就是我们WiFi列表里所看到的名字
        WifiManager wifimanager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);//得到wifi管理器对象
        int wifiId = -1;//自己定义的数值，判断用
        WifiConfiguration wifiCong = new WifiConfiguration();//这个类是我们构造wifi对象使用的，具体可以百度
        wifiCong.SSID = "\"" + wifiname + "\"";// \"转义字符，代表"//为成员变量赋值
        wifiCong.preSharedKey = "\"" + pwd + "\"";// WPA-PSK密码
        wifiCong.hiddenSSID = false;
        wifiCong.status = WifiConfiguration.Status.ENABLED;
        wifiId = wifimanager.addNetwork(wifiCong);// 将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
        if ( wifiId!=-1 )
        {
//添加成功
        }else
        {
//添加失败
        }
        boolean isConected =  wifimanager.enableNetwork(wifiId, true);  // 连接配置好的指定ID的网络 true连接成功
        if ( isConected )
        {
//连接成功
            WifiInfo info = wifimanager.getConnectionInfo();
        }else
        {
//连接失败
        }
    }


    private void initOkgo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //配置log打印
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.WARNING);
        builder.addInterceptor(loggingInterceptor);
        //配置cookie  配置到sp中
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        HttpParams params = new HttpParams();
        OkGo.getInstance()
                .init(this)
                .addCommonParams(params)
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);
    }

    private void initPush() {
        Log.e(TAG, "initPush: "  );
        XGPushConfig.setMiPushAppId(getApplicationContext(),"2882303761517730290");
        XGPushConfig.setMiPushAppKey(getApplicationContext(), "5531773030290");
        XGPushConfig.enableOtherPush(getApplicationContext(), true);
        XGPushConfig.enableDebug(this,true);
        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                Log.e("TPush", "注册成功，设备token为：" + o);
            }

            @Override
            public void onFail(Object o, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
        Log.e(TAG, "initPush: "+XGPushConfig.getToken(getApplicationContext()) );

    }
}
