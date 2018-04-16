package com.hsap.test2.utils;

import android.content.Context;
import android.widget.Toast;


import com.android.tu.loadingdialog.LoadingDailog;
import com.hsap.test2.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

/**
 * Created by zhao on 2017/11/17.
 */

public class ToastUtils {
    public static void showToast(Context context, String text){
        StyleableToast.makeText(context,text, Toast.LENGTH_SHORT, R.style.MyToast).show();
    }
    public static LoadingDailog showDailog(Context context, String text){
        LoadingDailog dailog = new LoadingDailog.Builder(context)
                .setMessage(text)
                .setCancelable(true)
                .setCancelOutside(true).create();

        return dailog;
    }
}
