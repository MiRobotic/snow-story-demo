package com.mirobotic.story.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Toast;

import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.core.Speech;
import com.csjbot.coshandler.listener.OnSpeakListener;
import com.csjbot.coshandler.listener.OnSpeechGetResultListener;
import com.csjbot.coshandler.listener.OnSpeechListener;
import com.csjbot.coshandler.tts.ISpeechSpeak;
import com.iflytek.cloud.SpeechError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class VoiceService extends Service {

    private static final String TAG = VoiceService.class.getSimpleName();
    private final IBinder binder = new RobotServiceBinder();
    private OnRobotResultListener resultListener;
    private CsjRobot mCsjBot;

    public VoiceService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create called");
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void connectRobot(final OnRobotResultListener resultListener) {
        this.resultListener = resultListener;
        final Context context = VoiceService.this;

        Log.e(TAG,"SERVICE CONNECTED");

        mCsjBot = CsjRobot.getInstance();

        Speech speech = CsjRobot.getInstance().getSpeech();
        speech.startSpeechService();
        speech.startIsr();

        ISpeechSpeak speak = mCsjBot.getTts();
        speak.setLanguage(Locale.ENGLISH);
        speech.setLanguage("en_us");

        // robot voice
        // Turn on the SMS service (it is enabled by default, no need to operate)
        speech.startSpeechService();

        // start speaking
        speak.startSpeaking("Hello, I am Snow", new OnSpeakListener() {
            @Override
            public void onSpeakBegin() {
                // Before you speak
                Log.e(TAG,"speaking started");
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                // speak is complete
                Log.e(TAG,"speaking end");
            }
        });

        // Manually get the answer to the question
        speech.getResult("What is your name?", new OnSpeechGetResultListener() {
            @Override
            public void response(String s) {
                Log.e(TAG,"SPEECH >> "+s);
            }
        });
        speech.getResult("What is the whether in Singapore", new OnSpeechGetResultListener() {
            @Override
            public void response(String t) {
                Log.e(TAG,"SPEECH >> "+t);
            }
        });


        mCsjBot.registerSpeechListener(new OnSpeechListener() {
            @Override
            public void speechInfo(String t, int i) {

                // Simple parsing example
                Log.d(TAG, "SPEECH >> " + t);
                if (Speech.SPEECH_RECOGNITION_RESULT == i) {
                    // Identified information
                    try {
                        String text = new JSONObject(t).getString("text");
                        resultListener.onVoiceResult(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (Speech.SPEECH_RECOGNITION_AND_ANSWER_RESULT == i) {
                    // Recognized information and answers
                    try {
                        String say = new JSONObject(t).getJSONObject("result").getJSONObject("data").getString("say");
                        resultListener.onVoiceResult(say);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        mCsjBot.registerSpeechListener(new OnSpeechListener() {
            @Override
            public void speechInfo(String s, int i) {

                // Simple parsing example
                Log.d(TAG, "SPEECH >> " + s);
                if (Speech.SPEECH_RECOGNITION_RESULT == i) {
                    // Identified information
                    try {
                        String text = new JSONObject(s).getString("text");
                        resultListener.onVoiceResult(text);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (Speech.SPEECH_RECOGNITION_AND_ANSWER_RESULT == i) {
                    // Recognized information and answers
                    try {
                        String say = new JSONObject(s).getJSONObject("result").getJSONObject("data").getString("say");
                        resultListener.onVoiceResult(say);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }


    public class RobotServiceBinder extends Binder {
        public VoiceService getService() {
            return VoiceService.this;
        }
    }

    public interface OnRobotResultListener{
        void onVoiceResult(String voice);
    }




}
