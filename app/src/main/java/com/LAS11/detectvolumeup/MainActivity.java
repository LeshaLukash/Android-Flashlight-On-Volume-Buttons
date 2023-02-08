package com.LAS11.detectvolumeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

/*
главная страница приложения
*/

public class MainActivity extends AppCompatActivity {

    Button button;
    Intent serviceIntent;
    SwitchCompat switchDetection;
    SwitchCompat switchAltMode;
    FlashlightManager flashlight;
    boolean altMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        switchDetection = findViewById(R.id.switchDetection);
        switchAltMode = findViewById(R.id.switchAltMode);

        flashlight = new FlashlightManager(this);
        serviceIntent = new Intent(this, DetectVolumeButtonsService.class);
        serviceIntent.putExtra("altMode", altMode);

        //кнопка посредине экрана, включает и выключает фонарик
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try { flashlight.switchLight(); }
                catch (CameraAccessException e) { e.printStackTrace(); }
            }
        });

        //старт/стоп сервиса, отслеживающего нажатия кнопок громкости
        switchDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startService(serviceIntent);
                    switchAltMode.setClickable(false);
                    switchAltMode.setAlpha((float) 0.3);
                } else {
                    stopService(serviceIntent);
                    switchAltMode.setClickable(true);
                    switchAltMode.setAlpha((float) 1.0);
                }
            }
        });
        switchAltMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                altMode = isChecked;
                serviceIntent.putExtra("altMode", altMode);
            }
        });

    }



}