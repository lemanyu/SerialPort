package com.hsap.test2;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hsap.test2.receiver.ContentBean;
import com.hsap.test2.utils.ActivityManagerUtils;
import com.hsap.test2.utils.ContantsUtil;
import com.hsap.test2.utils.SpUtils;
import com.hsap.test2.utils.ToastUtils;


public class MainActivity extends FragmentActivity {

    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        String content = getIntent().getStringExtra("content");
        ContentBean bean = new Gson().fromJson(content, ContentBean.class);

       SpUtils.putInt(ContantsUtil.userId,bean.getUserId(),getApplicationContext());
        SpUtils.putInt(ContantsUtil.Sex,bean.getSex(),getApplicationContext());
        ActivityManagerUtils.getInstance().finishActivityclass(HomeActivity.class);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                ToastUtils.showToast(this,"再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}
