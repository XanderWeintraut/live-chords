package com.example.live_chords;

/*
 *
 * When button is pressed, begins recording audio and processes it.
 *
 * Based heavily off of example found here: https://developer.android.com/guide/topics/media/mediarecorder#java
 *
 * @Author Xander Weintraut
 * @Version 6:02 PM, September 14 2019
 *
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

public class SecondActivity extends AppCompatActivity {


    private ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        button = findViewById(R.id.imageButton3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openActivity3();
            }
        });
    }

    public void openActivity3() {
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
    }
}
