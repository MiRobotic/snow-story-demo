package com.mirobotic.story.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mirobotic.story.R;
import com.mirobotic.story.app.UserDataProvider;

import java.util.Locale;

import static com.mirobotic.story.app.Config.LANG_CANTONESE;
import static com.mirobotic.story.app.Config.LANG_ENGLISH;
import static com.mirobotic.story.app.Config.LANG_HOKKIEN;
import static com.mirobotic.story.app.Config.LANG_MANDARIN;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private UserDataProvider dataProvider;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lang);

        context = SelectLanguageActivity.this;
        dataProvider = UserDataProvider.getInstance(context);


        Button btnMan = (Button) findViewById(R.id.btnMan);
        Button btnCan = (Button) findViewById(R.id.btnCan);
        Button btnHok = (Button) findViewById(R.id.btnHokk);
        Button btnEng = (Button) findViewById(R.id.btnEng);

        btnMan.setOnClickListener(this);
        btnCan.setOnClickListener(this);
        btnHok.setOnClickListener(this);
        btnEng.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String language, code;

        if (view.getId() == R.id.btnCan) {
            language = LANG_CANTONESE;
            code = "cn";
        } else if (view.getId() == R.id.btnHokk) {
            language = LANG_HOKKIEN;
            code = "cn";
        } else if (view.getId() == R.id.btnMan) {
            language = LANG_MANDARIN;
            code = "cn";
        } else {
            language = LANG_ENGLISH;
            code = "en";
        }

        dataProvider.setLanguage(language);
        dataProvider.setLanguageCode(code);

        Locale locale = new Locale(dataProvider.getLanguageCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(context, MenuActivity.class);
        startActivity(intent);

    }
}