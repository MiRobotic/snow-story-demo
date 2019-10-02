package com.mirobotic.story;

import android.app.Application;
import android.util.Log;

import com.csjbot.coshandler.core.CsjRobot;

/**
 * Created by Administrator on 2019/7/13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 初始化SDK
         */
        Log.e("MyApplication","ROBOT INITIALIZED");
        CsjRobot.getInstance().init(this);
    }
}
