package com.myproject.manageyourschedule.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.adapters.RecyclerViewAdapter;
import com.myproject.manageyourschedule.service.NotifyService;

import java.io.File;

import static com.myproject.manageyourschedule.Constant.OTHER_SCHEDULE_ADD;
import static com.myproject.manageyourschedule.Constant.OTHER_SCHEDULE_UPDATE;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;
import static com.myproject.manageyourschedule.activities.LoginActivity.storageReference;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListOtherSchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.recyclerViewAdapter;


public class OtherScheduleActivity extends AppCompatActivity {


    // 리사이클러뷰
    RecyclerView recyclerView = null;
    private FloatingActionButton addButton;
    private String TAG="OtherScheduleActivity";

    //2020.03.23+(s)
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //2020.03.23+(e)


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_schedule);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButtonOtherSchedule);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recyclerViewAdapter);


        Intent intent = getIntent();
        Schedule schedule = (Schedule) intent.getSerializableExtra("otherScheduleData");

        if (schedule != null) {

            //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.
            //2020.04.05+(s)
            /*DatabaseReference ref;
            DatabaseReference pushTime = refTimestamp.push();
            String key = pushTime.getKey();
            pushTime.setValue(schedule.getId());
            schedule.setTimeKey(key);
            ref = database.getReference(schedule.getId());
            ref.setValue(schedule);*/
            //2020.04.05+(e)
            //2020.04.12+(s)
            /*firebaseFirestore.collection("Timestamp").document(schedule.getId())
                    .set(mapSchedule)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });*/
            //2020.04.12+(e)
            //2020.04.14+(s) firestore version
            uploadImageToCloudStorage(schedule);
            firebaseFirestore.collection("schedule").document(schedule.getId())
                    .set(schedule)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                        }
                    });
            //2020.04.14+(e)

            //임시삭제 2020.04.06 refOtherSchedule.push().setValue(schedule);

            arrayListOtherSchedule.add(schedule);
            //추가 되었다고 어뎁터한테 알려주기
            recyclerViewAdapter.notifyDataSetChanged();

            //2020.03.31+(s)

            Intent intentService = new Intent(OtherScheduleActivity.this, NotifyService.class);
            startService(intentService);
            //2020.03.31+(e)


        }
        setItemClickListener();

        setBtnClickListener();


    }

    //2020.03.23+(s)

    private void uploadImageToCloudStorage(Schedule schedule) {
        if (schedule.getImageFilePath() != null) {
            Uri uri = Uri.fromFile(new File(schedule.getImageFilePath()));
            StorageReference storageRef = storageReference.child("images/" + (uri).getLastPathSegment());
            Log.d(TAG, schedule.getImageFilePath().substring(schedule.getImageFilePath().length() - 22));
            UploadTask uploadTask = storageRef.putFile(uri);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
    }

    //리싸이클러뷰 아이템 클릭 리스너 -> 일정 상세 페이지로 이동
    private void setItemClickListener() {

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Schedule schedule = arrayListOtherSchedule.get(position);
                /*//2020.03.23+(s)
                preferences = getSharedPreferences("OtherSchedule", MODE_PRIVATE);
                String value = preferences.getString(("userEmail@google.com") + "★" + 1 + "★" + position, null);

                Schedule schedule = new Schedule();
                if (value != null) {
                    //split쓸 경우 두번째 인자 -1을 설정하는 것에 유의
                    String[] scheduleArray = value.split("★", -1);
                    schedule.setTitle(scheduleArray[0]);
                    schedule.setYear(Integer.parseInt(scheduleArray[1]));
                    schedule.setMonth(Integer.parseInt(scheduleArray[2]));
                    schedule.setDayOfMonth(Integer.parseInt(scheduleArray[3]));
                    schedule.setHourOfStart(Integer.parseInt(scheduleArray[4]));
                    schedule.setMinuteOfStart(Integer.parseInt(scheduleArray[5]));
                    schedule.setHourOfEnd(Integer.parseInt(scheduleArray[6]));
                    schedule.setMinuteOfEnd(Integer.parseInt(scheduleArray[7]));
                    schedule.setPlace(scheduleArray[8]);
                    schedule.setUri(scheduleArray[9]);
                    schedule.setContent(scheduleArray[10]);
                    //scheduleArray[11]: 이미지의 경로가 "null"이 아니어야 한다.
                    if (!scheduleArray[11].equals("null")) {
                        schedule.setImageFilePath(scheduleArray[11]);
                    }
                    schedule.setSatisfaction(Float.parseFloat(scheduleArray[12]));
                    schedule.setComment(scheduleArray[13]);
                    *//*del 2020.03.29
                    //scheduleArray[11]: 이미지의 경로가 "null"이 아니어야 한다.
                    if (!scheduleArray[11].equals("null")) {
                        File file = new File(scheduleArray[11]);
                        schedule.setImageFile(file);
                    }*//*

                }
                //2020.03.23+(e)*/

                //del 2020.03.23    Schedule schedule = arrayListOtherSchedule.get(position);
                Intent intent = new Intent(OtherScheduleActivity.this, OtherScheduleUpdateActivity.class);
                intent.putExtra("CorrespondingSchedule", schedule);
                intent.putExtra("itemPosition", position);
                startActivityForResult(intent, OTHER_SCHEDULE_UPDATE);

            }
        });

    }

    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {
        toMain();
    }

    private void toMain() {

        Intent intent = new Intent(OtherScheduleActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //리싸이클러뷰에서 추가 버튼 클릭 리스너
    private void setBtnClickListener() {

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherScheduleActivity.this, OtherScheduleAddActivity.class);
                startActivityForResult(intent, OTHER_SCHEDULE_ADD);
            }
        });


    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OtherScheduleActivity.this);
        builder.setMessage("일정앞에 버튼을 눌러주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // 리싸이클러 뷰에서 아이템 추가
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == OTHER_SCHEDULE_ADD) {
                Schedule schedule = (Schedule) data.getSerializableExtra("addOtherScheduleData");

                //해당 arrayList 마지막 위치에 item을 추가한다.
                arrayListOtherSchedule.add(schedule);

                // SharedPreferences 객체 생성, MODE_PRIVATE: 이 앱에서만 사용, Login 이라는 이름을 가진 SharedPreferences를 생성한다.
                //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.

                //2020.04.05+(s)
                /*DatabaseReference ref;
                DatabaseReference pushTime = refTimestamp.push();
                String key = pushTime.getKey();
                pushTime.setValue(schedule.getId());
                schedule.setTimeKey(key);
                ref = database.getReference(schedule.getId());
                ref.setValue(schedule);*/
                //2020.04.05+(e)
                //2020.04.14+(s) firestore version
                uploadImageToCloudStorage(schedule);
                firebaseFirestore.collection("schedule").document(schedule.getId())
                        .set(schedule)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                //2020.04.14+(e)

                recyclerViewAdapter.notifyDataSetChanged();

                //2020.03.31+(s)

                Intent intentService = new Intent(OtherScheduleActivity.this, NotifyService.class);
                startService(intentService);
                //2020.03.31+(e)

                // 리싸이클러 뷰에서 아이템 수정
            } else if (requestCode == OTHER_SCHEDULE_UPDATE) {

                int pos = data.getIntExtra("itemPosition", 0);
                Schedule schedule = (Schedule) data.getSerializableExtra("updateOtherScheduleData");
                arrayListOtherSchedule.set(pos, (schedule));

                /*DatabaseReference ref;
                ref = database.getReference(schedule.getId());
                ref.setValue(schedule);*/
                //2020.03.23+(s)
                // SharedPreferences 객체 생성, MODE_PRIVATE: 이 앱에서만 사용, Login 이라는 이름을 가진 SharedPreferences를 생성한다.
                //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.
                /*preferences = getSharedPreferences("OtherSchedule", MODE_PRIVATE);
                editor = preferences.edit();

                String key = (("userEmail@google.com") + "★" + 1 + "★" + pos);

                // stringBuffer에 value 추가
                StringBuffer value = new StringBuffer(schedule.getTitle());
                value.append("★").append(schedule.getYear());
                value.append("★").append(schedule.getMonth());
                value.append("★").append(schedule.getDayOfMonth());
                value.append("★").append(schedule.getHourOfStart());
                value.append("★").append(schedule.getMinuteOfStart());
                value.append("★").append(schedule.getHourOfEnd());
                value.append("★").append(schedule.getMinuteOfEnd());
                value.append("★").append(schedule.getPlace());
                value.append("★").append(schedule.getUri());
                value.append("★").append(schedule.getContent());
                value.append("★").append(schedule.getImageFilePath());
                value.append("★").append(schedule.getSatisfaction());
                value.append("★").append(schedule.getComment());

                editor.putString((key), String.valueOf(value));
                editor.apply();*/


                //2020.03.23+(e)

                //2020.04.14+(s) firestore version
                uploadImageToCloudStorage(schedule);
                firebaseFirestore.collection("schedule").document(schedule.getId())
                        .set(schedule)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                //2020.04.14+(e)

                //수정된 위치만 리싸이클러 어뎁터 갱신
                recyclerViewAdapter.notifyItemChanged(pos);

                //2020.03.31+(s)

                Intent intentService = new Intent(OtherScheduleActivity.this, NotifyService.class);
                startService(intentService);
                //2020.03.31+(e)
            }
        }
    }
}
