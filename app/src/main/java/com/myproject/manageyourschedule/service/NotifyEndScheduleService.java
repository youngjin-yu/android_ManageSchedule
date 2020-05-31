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

import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.receiver.MyReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import static com.myproject.manageyourschedule.Constant.CHANNEL_ID;
import static com.myproject.manageyourschedule.Constant.MY_SCHEDULE_END;
import static com.myproject.manageyourschedule.Constant.NOTIFICATION_END_SCHEDULE_ID;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListOtherSchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListSchedule;
import static com.myproject.manageyourschedule.activities.SettingsActivity.isNotifyEndSchedule;

public class NotifyEndScheduleService extends Service {

    //2020.03.31+
    private String tag = "NotifyEndScheduleService";
    private static final String TAG = "일정 종료 알림 서비스";

    //노티피케이션 빌더 선언
    private NotificationCompat.Builder builder;

    //노티피케이션 매니저 선언
    private NotificationManager notificationManager;

    // 브로드캐스트 리시버
    private MyReceiver myReceiver = null;

    //생성자
    public NotifyEndScheduleService() {

    }


    // 서비스가 만들어 졌을 때
    @Override
    public void onCreate() {
        super.onCreate();
        //리시버 생성한다.
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_SCHEDULE_END);
        registerReceiver(myReceiver, intentFilter);
        Log.d(tag, "onCreate 입니다.");

    }

    // 사용하지 않을 때는 onBind는 null 값을 반환하면 됨.
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    // 서비스가 시작 전에
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(isNotifyEndSchedule=true) {

            //if (isSwitchOn) {
            Intent myBroadcastIntent = new Intent(MY_SCHEDULE_END);
            sendBroadcast(myBroadcastIntent);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    countFinishedSchedule();
                }
            };

            Timer timer = new Timer();
            //30초 마다 실행하는 timertask
            timer.schedule(timerTask, 0, 3000);

            /*Intent myBroadcastIntent = new Intent(MY_ACTION);
        sendBroadcast(myBroadcastIntent);*//*
            }*/
            // 리시버 등록
        }
        Log.d(tag, "onStartCommand 입니다.");
        //2020.03.31+ 임시삭제 startService 자체적으로 종료시킨다.
        //stopSelf();
        //START_NOT_STICKY : onStartCommand() 를 반환 후에 중단시키면 서비스를 재생성하지 않는다.
        return START_NOT_STICKY;
    }

    private void countFinishedSchedule() {

        String returnedSchedule = null;
        Iterator<Schedule> iterator = arrayListSchedule.iterator();
        while (iterator.hasNext()) {
            StringBuffer stringBuffer = new StringBuffer();
            Schedule schedule = iterator.next();
            String yearOfSchedule = String.format("%04d", schedule.getYear());
            String monthOfSchedule = String.format("%02d", schedule.getMonth());
            String dayOfSchedule = String.format("%02d", schedule.getDayOfMonth());
            String hourOfSchedule = String.format("%02d", schedule.getHourOfEnd());
            String minuteOfSchedule = String.format("%02d", schedule.getMinuteOfEnd());
            //String secondOfSchedule = "00";
            stringBuffer.append(yearOfSchedule).append("-").append(monthOfSchedule).append("-").append(dayOfSchedule).append(" ")
                    .append(hourOfSchedule).append(":").append(minuteOfSchedule);

            Date dateOfSchedule = null;
            Date dateNow = null;
            Date currentTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                dateNow = simpleDateFormat.parse(simpleDateFormat.format(currentTime));
                dateOfSchedule = simpleDateFormat.parse(String.valueOf(stringBuffer));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dateOfSchedule.compareTo(dateNow) == 0) {
                //countStudySchedule++;
                returnedSchedule = (schedule.getTitle());
            }
        }


        Iterator<Schedule> iteratorOtherSchedule = arrayListOtherSchedule.iterator();
        while (iteratorOtherSchedule.hasNext()) {
            StringBuffer stringBuffer = new StringBuffer();
            Schedule schedule = iteratorOtherSchedule.next();
            String yearOfSchedule = String.format("%04d", schedule.getYear());
            String monthOfSchedule = String.format("%02d", schedule.getMonth());
            String dayOfSchedule = String.format("%02d", schedule.getDayOfMonth());
            String hourOfSchedule = String.format("%02d", schedule.getHourOfEnd());
            String minuteOfSchedule = String.format("%02d", schedule.getMinuteOfEnd());
            //String secondOfSchedule = "00";
            stringBuffer.append(yearOfSchedule).append("-").append(monthOfSchedule).append("-").append(dayOfSchedule).append(" ")
                    .append(hourOfSchedule).append(":").append(minuteOfSchedule);

            Date dateOfSchedule = null;
            Date dateNow = null;
            Date currentTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                dateNow = simpleDateFormat.parse(simpleDateFormat.format(currentTime));
                dateOfSchedule = simpleDateFormat.parse(String.valueOf(stringBuffer));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dateOfSchedule.compareTo(dateNow) == 0) {
                //countOtherSchedule++;
                returnedSchedule = (schedule.getTitle());
            }
        }
        if(returnedSchedule!=null) {
            notifyEndScheduleToUser(returnedSchedule);
        }
    }

    private void notifyEndScheduleToUser(String scheduleTitle) {

        // 노피티케이션 빌더
        builder = new NotificationCompat.Builder(NotifyEndScheduleService.this, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_calendar);
        //노티피케이션 제목
        builder.setContentTitle("일정 종료를 알려드립니다.");
        //노티피케이션 내용
        builder.setContentText(scheduleTitle + " 일정이 종료되었습니다. 수행률을 입력해주세요.");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        //setContentIntent : 알람을 눌렀을 때 실행할 작업 인텐트를 설정합니다
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_END_SCHEDULE_ID, builder.build());

        }
    }

    //서비스가 종료할 때 호출된다.
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "onDestroy 입니다.");
        // 리시버 해제
        unregisterReceiver(myReceiver);

    }


}
