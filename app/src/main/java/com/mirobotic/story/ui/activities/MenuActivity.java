package com.mirobotic.story.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import androidx.navigation.fragment.NavHostFragment;

import com.csjbot.cosclient.constant.ClientConstant;
import com.csjbot.cosclient.entity.CommonPacket;
import com.csjbot.cosclient.entity.MessagePacket;
import com.csjbot.cosclient.listener.ClientEvent;
import com.csjbot.cosclient.listener.EventListener;
import com.csjbot.coshandler.core.Action;
import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.core.Speech;
import com.csjbot.coshandler.core.State;
import com.csjbot.coshandler.listener.OnGoRotationListener;
import com.csjbot.coshandler.listener.OnSpeakListener;
import com.csjbot.coshandler.listener.OnSpeechGetResultListener;
import com.csjbot.coshandler.listener.OnSpeechListener;
import com.csjbot.coshandler.listener.OnWakeupListener;
import com.csjbot.coshandler.log.CsjlogProxy;
import com.csjbot.coshandler.tts.ISpeechSpeak;
import com.iflytek.cloud.SpeechError;
import com.mirobotic.story.R;
import com.mirobotic.story.app.UserDataProvider;
import com.mirobotic.story.services.VoiceService;
import com.mirobotic.story.ui.fragments.MenuFragment;
import com.mirobotic.story.ui.fragments.TabsFragment;
import com.mirobotic.story.ui.viewModels.MainViewModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import static com.mirobotic.story.app.Config.DIR_SONG;
import static com.mirobotic.story.app.Config.DIR_STORY;
import static com.mirobotic.story.app.Config.LANG_CANTONESE;
import static com.mirobotic.story.app.Config.LANG_ENGLISH;
import static com.mirobotic.story.app.Config.LANG_HOKKIEN;
import static com.mirobotic.story.app.Config.LANG_MANDARIN;

public class MenuActivity extends AppCompatActivity implements OnActivityInteraction {

    private CsjRobot mCsjBot;
    private static final String TAG = "MenuActivity";
    private Context context;
    private EventListener eventListener;
    private UserDataProvider dataProvider;
    private VoiceService voiceService;
    private MainViewModel viewModel;
    private MenuFragment menuFragment;
    private VoiceService.OnRobotResultListener resultListener = new VoiceService.OnRobotResultListener() {
        @Override
        public void onVoiceResult(final String voice) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"Voice Results > "+voice);
                    processCommand(voice);
                    Toast.makeText(context, voice, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            VoiceService.RobotServiceBinder robotServiceBinder = (VoiceService.RobotServiceBinder) iBinder;
            voiceService = robotServiceBinder.getService();
            voiceService.connectRobot(resultListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = MenuActivity.this;

        dataProvider = UserDataProvider.getInstance(context);

        mCsjBot = CsjRobot.getInstance();

        if (savedInstanceState == null) {
            menuFragment = new MenuFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment,menuFragment);
            ft.commit();

//            NavHostFragment host = NavHostFragment.create(R.navigation.main_path);
//            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, host).commit();

        }

        init();

    }

    private void init() {
        eventListener = new EventListener() {
            @Override
            public void onEvent(final ClientEvent event) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (event.eventType) {
                            case ClientConstant.EVENT_RECONNECTED:
                                Log.d(TAG, " EVENT_RECONNECTED");
                                break;
                            case ClientConstant.EVENT_CONNECT_SUCCESS:
                                Log.d(TAG, " EVENT_CONNECT_SUCCESS");
                                //setLanguage();
                                break;
                            case ClientConstant.EVENT_CONNECT_FAILD:
                                Log.d(TAG, "EVENT_CONNECT_FAILD" + event.data);
                                break;
                            case ClientConstant.EVENT_CONNECT_TIME_OUT:
                                Log.d(TAG, "EVENT_CONNECT_TIME_OUT  " + event.data);
                                break;
                            case ClientConstant.SEND_FAILED:
                                showMessage(true, "Send Failed");
                                Log.d(TAG, "SEND_FAILED");
                                break;
                            case ClientConstant.EVENT_DISCONNET:
                                Log.d(TAG, "EVENT_DISCONNECT");
                                break;
                            case ClientConstant.EVENT_PACKET:
                                MessagePacket packet = (MessagePacket) event.data;
                                String json = ((CommonPacket) packet).getContentJson();
                                //handleResponse(json);
                                break;
                            default:
                                break;
                        }
                    }
                });

            }
        };

        Intent intent = new Intent(this, VoiceService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        mCsjBot.registerWakeupListener(new OnWakeupListener() {
            @Override
            public void response(int i) {
                Log.d(TAG, "registerWakeupListener:i:" + i);
                mCsjBot.getTts().startSpeaking("我在呢!", null);
                mCsjBot.getAction().moveAngle(i, new OnGoRotationListener() {
                    @Override
                    public void response(int i) {
                        if (i > 0 && i < 360) {
                            if (i <= 180) {
                                CsjlogProxy.getInstance().debug("向左转:+" + i);
                                if (mCsjBot.getState().getChargeState() == State.NOT_CHARGING) {
                                    mCsjBot.getAction().moveAngle(i, null);
                                }
                            } else {
                                CsjlogProxy.getInstance().debug("向右转:-" + (360 - i));
                                if (mCsjBot.getState().getChargeState() == State.NOT_CHARGING) {
                                    mCsjBot.getAction().moveAngle(-(360 - i), null);
                                }
                            }
                        }
                    }
                });
            }
        });



    }

    private void showMessage(final String s){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,s,Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void processCommand(String text) {

        Log.e(TAG, "COMMAND RECEIVED > " + text);
        text = text.toLowerCase();
        if (text.contains("language")) {

            if (text.contains(LANG_CANTONESE)) {
                dataProvider.setLanguage(LANG_CANTONESE);
                dataProvider.setLanguageCode("cn");

                changeLanguage();

            } else if (text.contains(LANG_HOKKIEN)) {
                dataProvider.setLanguage(LANG_HOKKIEN);
                dataProvider.setLanguageCode("cn");
                changeLanguage();
            } else if (text.contains(LANG_MANDARIN)) {
                dataProvider.setLanguage(LANG_MANDARIN);
                dataProvider.setLanguageCode("cn");
                changeLanguage();
            } else if (text.contains(LANG_ENGLISH)) {
                dataProvider.setLanguage("btnEng");
                dataProvider.setLanguageCode("en");
                changeLanguage();
            }

        }else if (text.contains("sing") || text.contains("song")){

            if (text.contains("start")){
                playFile("song");
            }else if(text.contains("stop")){
                stopFile();
            }

        }else if (text.contains("dance")){
            if (text.contains("start")){
                startDance();
            }else if(text.contains("stop")){
                stopDance();
            }
        }else if (text.contains("story")){
            if (text.contains("start")){
                playFile("story");
            }else if(text.contains("stop")){
                stopFile();
            }
        }
    }

    private void playFile(String type){
        Bundle bundle = new Bundle();
        bundle.putBoolean("autoPlay",true);
        if (type.equals("song")){
            bundle.putString("type", DIR_SONG);
        }else {
            bundle.putString("type", DIR_STORY);
        }

        TabsFragment fragment = new TabsFragment();
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.nav_host_fragment,fragment,"");
        ft.addToBackStack("TabsFragment");
        ft.commit();
    }

    private void stopFile(){
        onBackPressed();
    }

    private void changeLanguage() {
        Locale locale = new Locale(dataProvider.getLanguageCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(context, MenuActivity.class);
        startActivity(intent);

        MenuActivity.this.finish();
    }


    private void showMessage(boolean isError, final String msg) {

        if (isError) {
            Log.e(TAG, "error: >> " + msg);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void sendData(@NotNull String json) {

    }

    @Override
    public void startDance() {
        Action action = mCsjBot.getAction();
        action.startDance();
    }

    @Override
    public void stopDance() {
        Action action = mCsjBot.getAction();
        action.stopDance();
    }
}