package com.LAS11.detectvolumeup;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

/*
foreground-сервис, который отслеживает нажатие кнопок громкости
*/

public class DetectVolumeButtonsService extends Service {

    private VolumeButtonsController controller;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    //это для работы уведолмлений
    final String SERVICE_CHANNEL_ID = "DETECT_VOLUME_BUTTONS_SERVICE_CHANNEL_ID";
    final int SERVICE_NOTIFICATION_ID = 1;

    public DetectVolumeButtonsService() {
    }


    /*
    при старте сервиса генерируется неубиваемое уведомление, которое висит в шторке
    оно необходимо для работы foreground-сервиса
    бонусом, если приложение стартует на Android версии 8 и выше - создаётся новый канал уведомлдений
    */
    @Override
    public void onCreate() {
        notificationManager = getSystemService(NotificationManager.class);

        //проверка версии системы и регистрация канала уведомлений при необходимости
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        controller = new VolumeButtonsController(this);
        notificationBuilder = new NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Фонарик на клавишах громкости")
                .setContentText("Отслеживаю нажатие кнопок громкости")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //здесь обычный сервис становится foreground-сервисом
        startForeground(SERVICE_NOTIFICATION_ID, notificationBuilder.build());
        super.onCreate();
    }

    //регистрация нового канала уведлмлений
    private void createNotificationChannel() {
        CharSequence name = "Уведомление об отслеживании кнопок громкости";
        String description = "This channel is used to notify an user about detecting volume buttons";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(SERVICE_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //именно тут начинается регистрация нажатий кнопок громкости
        controller.startDetectVolumeButtons();
        Toast.makeText(this, "Нажатие кнопок громкости отслеживается", Toast.LENGTH_SHORT).show();

        //по идее должно помогать возобновлять работу сервиса, если оно дало сбой
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        notificationManager.cancel(SERVICE_NOTIFICATION_ID);
        controller.destroy();
        Toast.makeText(this, "Нажатие кнопок громкости не отслеживается", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}