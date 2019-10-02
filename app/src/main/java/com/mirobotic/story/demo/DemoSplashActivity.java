package com.mirobotic.story.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.listener.OnConnectListener;
import com.mirobotic.story.R;

public class DemoSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_splash);
        if (CsjRobot.getInstance().getState().isConnect()) {
            startActivity(new Intent(DemoSplashActivity.this, DemoActivity.class));
        } else {
            CsjRobot.getInstance().registerConnectListener(new OnConnectListener() {
                @Override
                public void success() {
                    startActivity(new Intent(DemoSplashActivity.this, DemoActivity.class));
                }

                @Override
                public void faild() {

                }

                @Override
                public void timeout() {

                }

                @Override
                public void disconnect() {

                }
            });
        }

    }
}
