package com.mirobotic.story.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.core.Speech;
import com.csjbot.coshandler.core.State;
import com.csjbot.coshandler.listener.OnGoRotationListener;
import com.csjbot.coshandler.listener.OnSpeechListener;
import com.csjbot.coshandler.listener.OnWakeupListener;
import com.csjbot.coshandler.log.CsjlogProxy;
import com.mirobotic.story.R;
import com.mirobotic.story.app.UserDataProvider;
import com.mirobotic.story.ui.fragments.MenuFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity implements OnActivityInteraction {

    private CsjRobot mCsjBot;
    private static final String TAG = "MenuActivity";
    private Context context;
    private EventListener eventListener;
    private UserDataProvider dataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = MenuActivity.this;

        dataProvider = UserDataProvider.getInstance(context);

        mCsjBot = CsjRobot.getInstance();

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment,new MenuFragment());
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

        mCsjBot.registerWakeupListener(new OnWakeupListener() {
            @Override
            public void response(int i) {
                Log.d("TAG", "registerWakeupListener:i:" + i);
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

        mCsjBot.registerSpeechListener(new OnSpeechListener() {
            @Override
            public void speechInfo(String s, int i) {

                // Simple parsing example
                Log.d(TAG, "registerSpeechListener:s:" + s);
                if (Speech.SPEECH_RECOGNITION_RESULT == i) {
                    // Identified information
                    try {
                        String text = new JSONObject(s).getString("text");
                        Toast.makeText(context, "Recognized text:" + text, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (Speech.SPEECH_RECOGNITION_AND_ANSWER_RESULT == i) {
                    // Recognized information and answers
                    try {
                        String say = new JSONObject(s).getJSONObject("result").getJSONObject("data").getString("say");
                        Toast.makeText(context, "Acquired answer information:" + say, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }


    private void processCommand(String text) {

        Log.e(TAG, "COMMAND RECEIVED > " + text);
        if (text.contains("language")) {

            if (text.contains("btnCan")) {
                dataProvider.setLanguage("btnCan");
                dataProvider.setLanguageCode("cn");

                changeLanguage();

            } else if (text.contains("btnHok")) {
                dataProvider.setLanguage("btnHok");
                dataProvider.setLanguageCode("cn");
                changeLanguage();
            } else if (text.contains("btnMan")) {
                dataProvider.setLanguage("btnMan");
                dataProvider.setLanguageCode("cn");
                changeLanguage();
            } else if (text.contains("chinese")) {
                dataProvider.setLanguage("chinese");
                dataProvider.setLanguageCode("cn");
                changeLanguage();
            } else if (text.contains("btnEng")) {
                dataProvider.setLanguage("btnEng");
                dataProvider.setLanguageCode("en");
                changeLanguage();
            }

        }


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
}