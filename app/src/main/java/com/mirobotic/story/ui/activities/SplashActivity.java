package com.mirobotic.story.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.csjbot.coshandler.core.CsjRobot;
import com.csjbot.coshandler.listener.OnConnectListener;
import com.mirobotic.story.R;
import com.mirobotic.story.app.UserDataProvider;
import com.mirobotic.story.demo.DemoActivity;

import java.io.File;

import static com.mirobotic.story.app.Config.DIR_SONG;
import static com.mirobotic.story.app.Config.DIR_STORY;
import static com.mirobotic.story.app.Config.LANG_CANTONESE;
import static com.mirobotic.story.app.Config.LANG_ENGLISH;
import static com.mirobotic.story.app.Config.LANG_HOKKIEN;
import static com.mirobotic.story.app.Config.LANG_MANDARIN;
import static com.mirobotic.story.app.Config.MAIN_FOLDER;

public class SplashActivity extends AppCompatActivity {


    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private Context context;
    private String[] permissionsRequired = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private UserDataProvider dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = SplashActivity.this;
        dataProvider = UserDataProvider.getInstance(context);

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkPermissions();
    }

    private void checkPermissions() {

        if (ActivityCompat.checkSelfPermission(context, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Permission Needed!");
            builder.setMessage("Please grand storage permission to use this app");
            builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (!dataProvider.isPermissionAsked()) {
                        ActivityCompat.requestPermissions(SplashActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        return;
                    }
                    Toast.makeText(context, "Please allow storage permission", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            return;
        }

        startApp();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allGranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allGranted = true;
                } else {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Log.e("permission", "all permissions granted!");
                startApp();
            } else {
                Log.e("permission", "permissions denied");
                Toast.makeText(context, "Permission denied!\nPlease allow storage permission to use app", Toast.LENGTH_SHORT).show();
                SplashActivity.this.finish();
            }

            dataProvider.dontAskPermission();
        }
    }

    private void startApp() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                checkFolders();


                connectRobot();
//                Intent intent = new Intent(context, SelectLanguageActivity.class);
//                startActivity(intent);
//                SplashActivity.this.finish();

            }
        }, 1500);
    }

    private void connectRobot() {
        if (CsjRobot.getInstance().getState().isConnect()) {
            startActivity(new Intent(context, MenuActivity.class));
            SplashActivity.this.finish();
        } else {
            CsjRobot.getInstance().registerConnectListener(new OnConnectListener() {
                @Override
                public void success() {
                    startActivity(new Intent(context, MenuActivity.class));
                    SplashActivity.this.finish();
                }

                @Override
                public void faild() {
                    Toast.makeText(context, "Failed to connect robot", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void timeout() {
                    Toast.makeText(context, "Robot Connection Timeout", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void disconnect() {
                    Toast.makeText(context, "Robot Disconnected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void checkFolders() {

        String tag = "Folders";

        File dirMain = new File(Environment.getExternalStorageDirectory(), MAIN_FOLDER);
        if (!dirMain.exists()) {
            if (!dirMain.mkdirs()) {
                Log.e(tag, "error " + dirMain.getAbsolutePath());
            }

        }

        File dirSongs = new File(Environment.getExternalStorageDirectory() + "/" + MAIN_FOLDER, DIR_SONG);
        if (!dirSongs.exists()) {
            if (!dirSongs.mkdirs()) {
                Log.e(tag, "error " + dirSongs.getAbsolutePath());
            }
        }

        File dirStory = new File(Environment.getExternalStorageDirectory() + "/" + MAIN_FOLDER, DIR_STORY);
        if (!dirStory.exists()) {
            if (!dirStory.mkdirs()) {
                Log.e(tag, "error " + dirStory.getAbsolutePath());
            }
        }


        // CREATE FOLDERS FOR LANGUAGE

        String songFolder = Environment.getExternalStorageDirectory() + "/" + MAIN_FOLDER + "/" + DIR_SONG;

        File dirSongsEng = new File(songFolder, LANG_ENGLISH);
        if (!dirSongsEng.exists()) {
            if (!dirSongsEng.mkdirs()) {
                Log.e(tag, "error " + dirSongsEng.getAbsolutePath());
            }
        }

        String storyFolder = Environment.getExternalStorageDirectory() + "/" + MAIN_FOLDER + "/" + DIR_STORY;

        File dirStoryEng = new File(storyFolder, LANG_ENGLISH);
        if (!dirStoryEng.exists()) {
            if (!dirStoryEng.mkdirs()) {
                Log.e(tag, "error " + dirStoryEng.getAbsolutePath());
            }
        }


        File dirSongsCan = new File(songFolder, LANG_CANTONESE);
        if (!dirSongsCan.exists()) {
            if (!dirSongsCan.mkdirs()) {
                Log.e(tag, "error " + dirSongsCan.getAbsolutePath());
            }
        }

        File dirStoryCan = new File(storyFolder, LANG_CANTONESE);
        if (!dirStoryCan.exists()) {
            if (!dirStoryCan.mkdirs()) {
                Log.e(tag, "error " + dirStoryCan.getAbsolutePath());
            }
        }

        File dirSongsMan = new File(songFolder, LANG_MANDARIN);
        if (!dirSongsMan.exists()) {
            if (!dirSongsMan.mkdirs()) {
                Log.e(tag, "error " + dirSongsMan.getAbsolutePath());
            }
        }

        File dirStoryMan = new File(storyFolder, LANG_MANDARIN);
        if (!dirStoryMan.exists()) {
            if (!dirStoryMan.mkdirs()) {
                Log.e(tag, "error " + dirStoryMan.getAbsolutePath());
            }
        }

        File dirSongsHok = new File(songFolder, LANG_HOKKIEN);
        if (!dirSongsHok.exists()) {
            if (!dirSongsHok.mkdirs()) {
                Log.e(tag, "error " + dirSongsHok.getAbsolutePath());
            }
        }

        File dirStoryHok = new File(storyFolder, LANG_HOKKIEN);
        if (!dirStoryHok.exists()) {
            if (!dirStoryHok.mkdirs()) {
                Log.e(tag, "error " + dirStoryHok.getPath());
            }
        }

    }

}
