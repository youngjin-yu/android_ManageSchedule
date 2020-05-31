package com.myproject.manageyourschedule.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.receiver.MyReceiver;
import com.myproject.manageyourschedule.thread.CountScheduleThread;

import static com.myproject.manageyourschedule.Constant.CHANNEL_ID;
import static com.myproject.manageyourschedule.Constant.COUNT_ACTION;
import static com.myproject.manageyourschedule.Constant.NOTIFICATION_ALREADY_DONE_NOTIFY_ID;
import static com.myproject.manageyourschedule.Constant.NOTIFICATION_COUNT_ID;
import static com.myproject.manageyourschedule.Constant.countOtherSchedule;
import static com.myproject.manageyourschedule.Constant.countStudySchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListOtherSchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListSchedule;
import static com.myproject.manageyourschedule.activities.SettingsActivity.isSwitchOn;

public class NotifyService extends Service {

    //2020.03.31+
    private String tag ="NotifyService";
    private static final String TAG = "일정알림서비스";

    // 메인액티비티에서 받아온 시간을 저장하는 변수
    int currentTime;
    //노티피케이션 빌더 선언
    private NotificationCompat.Builder builder;
    private NotificationCompat.Builder anotherBuilder;
    //노티피케이션 매니저 선언
    private NotificationManager anotherNotificationManager;
    private NotificationManager notificationManager;


    // 브로드캐스트 리시버
    private MyReceiver myReceiver = null;

    // 유저가 일정 등록 했는지 여부
    private Boolean isUserRegisterSchedule;

    //생성자
    public NotifyService() {

    }


    // 서비스가 만들어 졌을 때
    @Override
    public void onCreate() {
        super.onCreate();
        //리시버 생성한다.
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(COUNT_ACTION);
        registerReceiver(myReceiver, intentFilter);
        Log.d(tag,"onCreate 입니다.");

    }

    // 사용하지 않을 때는 onBind는 null 값을 반환하면 됨.
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    // 서비스가 시작 전에
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (isSwitchOn) {
            Intent myBroadcastIntent = new Intent(COUNT_ACTION);
            sendBroadcast(myBroadcastIntent);

            Runnable countRunnable = new CountScheduleThread();
            Thread countThread = new Thread(countRunnable);
            countThread.start();
            //2020.03.31 arrayList의 size가 변경될 때마다 서비스를 시작하므로 join()을 추가해야 한다.
            try {
                countThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            notifyAlreadyDone();

            notifyCountScheduleToUser();
        }

        Log.d(tag,"onStartCommand 입니다.");
        /*Intent myBroadcastIntent = new Intent(MY_ACTION);
        sendBroadcast(myBroadcastIntent);*/

        // 리시버 등록


        //2020.03.31+ 임시삭제 startService 자체적으로 종료시킨다.
        //stopSelf();
        //START_NOT_STICKY : onStartCommand() 를 반환 후에 중단시키면 서비스를 재생성하지 않는다.
        return START_NOT_STICKY;
    }

    private void notifyAlreadyDone() {
        anotherBuilder = new NotificationCompat.Builder(NotifyService.this, CHANNEL_ID);

        anotherBuilder.setSmallIcon(R.drawable.ic_calendar);
        //노티피케이션 제목
        anotherBuilder.setContentTitle("수행한 일정은 다음과 같습니다. 수행률을 입력해주세요.");
        //노티피케이션 내용
        anotherBuilder.setContentText("업무일정은 " + (arrayListSchedule.size() - countStudySchedule) + "개 수행하였고, 업무 외 일정은 " + (arrayListOtherSchedule.size() - countOtherSchedule) + "개 수행하였습니다.");
        anotherBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        // 사용자가 탭을 클릭하면 자동 제거
        anotherBuilder.setAutoCancel(true);
        //setContentIntent : 알람을 눌렀을 때 실행할 작업 인텐트를 설정합니다
        anotherBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


        anotherNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (anotherNotificationManager != null) {
            anotherNotificationManager.notify(NOTIFICATION_ALREADY_DONE_NOTIFY_ID, anotherBuilder.build());

        }
    }

    private void notifyCountScheduleToUser() {

        // 노피티케이션 빌더
        builder = new NotificationCompat.Builder(NotifyService.this, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_calendar);
        //노티피케이션 제목
        builder.setContentTitle("앞으로 남은 일정은 다음과 같습니다.");
        //노티피케이션 내용
        builder.setContentText("현재 업무일정은 " + countStudySchedule + "개 남았고, 업무 외 일정은 " + countOtherSchedule + "개 남았습니다.");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        //setContentIntent : 알람을 눌렀을 때 실행할 작업 인텐트를 설정합니다
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_COUNT_ID, builder.build());

        }
    }

    //서비스가 종료할 때 호출된다.
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag,"onDestroy 입니다.");
        // 리시버 해제
        unregisterReceiver(myReceiver);

    }


}
