package com.niucong.lkprinter.app;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;

/**
 * Created by think on 2018/3/29.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "5ab996cdf29d9859af00002f");
        UMConfigure.init(this, "5ab996cdf29d9859af00002f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
    }
}
