package com.myproject.manageyourschedule.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.receiver.MyReceiver;
import com.myproject.manageyourschedule.service.NotifyEndScheduleService;
import com.myproject.manageyourschedule.service.NotifyService;
import com.myproject.manageyourschedule.service.NotifyStartScheduleService;

import static com.myproject.manageyourschedule.Constant.CHANNEL_ID;
import static com.myproject.manageyourschedule.Constant.MY_ACTION;
import static com.myproject.manageyourschedule.Constant.MY_SCHEDULE_END;
import static com.myproject.manageyourschedule.Constant.MY_SCHEDULE_START;
import static com.myproject.manageyourschedule.Constant.NOTIFICATION_ID;
import static com.myproject.manageyourschedule.Constant.countOtherSchedule;
import static com.myproject.manageyourschedule.Constant.countStudySchedule;
import static com.myproject.manageyourschedule.Constant.entranceMainActivity;
import static com.myproject.manageyourschedule.Constant.notifyDescription;
import static com.myproject.manageyourschedule.Constant.notifyTitle;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListScheduleId;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUser;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserEmail;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserId;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserPassword;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;
import static com.myproject.manageyourschedule.activities.LoginActivity.loginEmailTextInputEditText;
import static com.myproject.manageyourschedule.activities.LoginActivity.storageReference;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListOtherSchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListSchedule;
import com.myproject.manageyourschedule.DTO.User;

import java.io.File;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {


    //2020.03.31+(s)
    //브로드캐스트 리시버
    private BroadcastReceiver mReceiver;
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static Boolean isSwitchOn = false;
    public static Boolean isNotifyStartSchedule=false;
    public static Boolean isNotifyEndSchedule=false;
    private String TAG = "SettingsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //리시버 생성
        mReceiver = new MyReceiver();
        setContentView(R.layout.activity_settings);

        // 쉐어드 프리퍼런스 객체 생성
        preferences = getSharedPreferences("NotifySwitch", MODE_PRIVATE);
        editor = preferences.edit();

        Switch sw = findViewById(R.id.switchNotify);
        Switch switchStartSchedule = findViewById(R.id.switchNotifyStartSchedule);
        Switch switchEndSchedule = findViewById(R.id.switchNotifyEndSchedule);

        if (isSwitchOn) {
            sw.setChecked(true);
        } else {
            sw.setChecked(false);
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSwitchOn = true;
                    editor.putBoolean("NotifyToUser", true);
                    editor.apply();
                    //Toast.makeText(getApplicationContext(), "알림 설정을 허용하였습니다.", Toast.LENGTH_LONG).show();
                    Intent myBroadcastIntent = new Intent(MY_ACTION);
                    sendBroadcast(myBroadcastIntent);
                    //notifyToUser();
                    //백그라운드 서비스 시작
                    Intent intent = new Intent(SettingsActivity.this, NotifyService.class);
                    startService(intent);
                } else {
                    isSwitchOn = false;
                    editor.putBoolean("NotifyToUser", false);
                    editor.apply();
                    buttonView.setChecked(false);
                    Toast.makeText(getApplicationContext(), "알림 설정을 종료하였습니다.", Toast.LENGTH_LONG).show();
                    // Notification 제거
                    //notificationManager.cancel(NOTIFICATION_ID);
                    //del  2020.03.31   서비스는 한번 실행하고 자체적으로 종료시키므로 아래 코드는 삭제한다.
                    //백그라운드 서비스 종료
                    Intent intent = new Intent(SettingsActivity.this, NotifyService.class);
                    stopService(intent);
                    //알림 설정을 끄면 count 변수도 0으로 초기화 한다.
                    countStudySchedule = 0;
                    countOtherSchedule = 0;

                }
            }
        });

        if(isNotifyStartSchedule){
            switchStartSchedule.setChecked(true);
        }else{
            switchStartSchedule.setChecked(false);
        }

        switchStartSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNotifyStartSchedule = true;
                    Toast.makeText(getApplicationContext(), "일정 시작 알림 설정을 허용하였습니다.", Toast.LENGTH_LONG).show();
                    Intent myBroadcastIntent = new Intent(MY_SCHEDULE_START);
                    sendBroadcast(myBroadcastIntent);
                    //notifyToUser();
                    //백그라운드 서비스 시작
                    Intent intent = new Intent(SettingsActivity.this, NotifyStartScheduleService.class);
                    startService(intent);
                } else {
                    isNotifyStartSchedule = false;
                    buttonView.setChecked(false);
                    Toast.makeText(getApplicationContext(), "일정 시작 알림 설정을 거부하였습니다.", Toast.LENGTH_LONG).show();
                    // Notification 제거
                    //notificationManager.cancel(NOTIFICATION_ID);
                    //del  2020.03.31   서비스는 한번 실행하고 자체적으로 종료시키므로 아래 코드는 삭제한다.
                    //백그라운드 서비스 종료
                    Intent intent = new Intent(SettingsActivity.this, NotifyStartScheduleService.class);
                    stopService(intent);
                    //알림 설정을 끄면 count 변수도 0으로 초기화 한다.
                    //countStudySchedule = 0;
                    //countOtherSchedule = 0;

                }
            }
        });

        if(isNotifyEndSchedule){
            switchEndSchedule.setChecked(true);
        }else{
            switchEndSchedule.setChecked(false);
        }

        switchEndSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNotifyEndSchedule = true;
                    Toast.makeText(getApplicationContext(), "일정 종료 알림 설정을 허용하였습니다.", Toast.LENGTH_LONG).show();
                    Intent myBroadcastIntent = new Intent(MY_SCHEDULE_END);
                    sendBroadcast(myBroadcastIntent);
                    //notifyToUser();
                    //백그라운드 서비스 시작
                    Intent intent = new Intent(SettingsActivity.this, NotifyEndScheduleService.class);
                    startService(intent);
                } else {
                    isNotifyEndSchedule = false;
                    buttonView.setChecked(false);
                    Toast.makeText(getApplicationContext(), "일정 종료 알림 설정을 거부하였습니다.", Toast.LENGTH_LONG).show();
                    // Notification 제거
                    //notificationManager.cancel(NOTIFICATION_ID);
                    //del  2020.03.31   서비스는 한번 실행하고 자체적으로 종료시키므로 아래 코드는 삭제한다.
                    //백그라운드 서비스 종료
                    Intent intent = new Intent(SettingsActivity.this, NotifyEndScheduleService.class);
                    stopService(intent);
                    //알림 설정을 끄면 count 변수도 0으로 초기화 한다.
                    //countStudySchedule = 0;
                    //countOtherSchedule = 0;

                }
            }
        });
    }

    private void notifyToUser() {
        builder = new NotificationCompat.Builder(SettingsActivity.this, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.ic_calendar);
        //노티피케이션 제목
        builder.setContentTitle(notifyTitle);
        //노티피케이션 내용
        builder.setContentText(notifyDescription);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);
        //setContentIntent : 알람을 눌렀을 때 실행할 작업 인텐트를 설정합니다
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());

        }
    }


    //액티비티가 화면에 보일 때 브로드캐스트 리시버를 등록한다.
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MY_ACTION);
        registerReceiver(mReceiver, intentFilter);

    }

    //액티비티가 화면에 보이지 않으면 브로드캐스트 리시버를 해제한다.
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void onClickWithdrawal(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setMessage("회원 탈퇴 하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //2020.04.25+(s)
                int position=-1;
                for(int i =0; i<arrayListUserEmail.size(); i++){
                    if(arrayListUserEmail.get(i).equals(loginEmailTextInputEditText.getText().toString())){
                        position = i;
                    }
                }
                User user = arrayListUser.get(position);
                //회원 탈퇴하는 부분

                arrayListUserId.remove(position);
                arrayListUser.remove(user);
                arrayListUserEmail.remove(position);
                arrayListUserPassword.remove(position);


                //유저에 대한 데이터 삭제 - 파이어베이스
                firebaseFirestore.collection("users").document(user.getUserId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });


                ArrayList<Schedule> temporaryArrayList = new ArrayList<>();
                temporaryArrayList.addAll(arrayListSchedule);
                temporaryArrayList.addAll(arrayListOtherSchedule);

                for(int i=0; i<temporaryArrayList.size(); i++){
                    Schedule schedule = temporaryArrayList.get(i);
                    //유저의 모든 일정을 파이어베이스에서 삭제
                    firebaseFirestore.collection("schedule").document(schedule.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });
                    //유저의 모든 업로드한 사진들을 삭제
                    deleteImageFromCloudStorage(schedule);
                }

                //메인액티비티 실행하는 횟수 초기화
                entranceMainActivity=0;
                //schedule Id를 담고있는 arrayList 초기화
                arrayListScheduleId.clear();
                //업무 일정을 담고있는 arrayList 초기화
                arrayListSchedule.clear();
                //업무 외 일정을 담고있는 arrayList 초기화
                arrayListOtherSchedule.clear();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
                //2020.04.25+(e)
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void deleteImageFromCloudStorage(Schedule schedule) {
        if(schedule.getImageFilePath()!=null) {
            Uri uri = Uri.fromFile(new File(schedule.getImageFilePath()));
            StorageReference storageRef = storageReference.child("images/" + (uri).getLastPathSegment());
            // Delete the file
            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }
    }
    //2020.03.31+(e)
}
