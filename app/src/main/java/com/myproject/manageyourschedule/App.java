package com.myproject.manageyourschedule;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import static com.myproject.manageyourschedule.Constant.CHANNEL_ID;
import static com.myproject.manageyourschedule.Constant.notifyDescription;
import static com.myproject.manageyourschedule.Constant.notifyTitle;

public class App extends Application {

    public static NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){

        // API 레벨 26 이상은 노티피케이션을 설정해 주려면 노피티케이션 채널을 추가해줘야함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, notifyTitle, importance);
            channel.setDescription(notifyDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        } else{

        }
    }

}
