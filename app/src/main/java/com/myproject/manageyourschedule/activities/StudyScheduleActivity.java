package com.myproject.manageyourschedule.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.service.NotifyService;

import java.io.File;

import static com.myproject.manageyourschedule.Constant.STUDY_SCHEDULE_ADD;
import static com.myproject.manageyourschedule.Constant.STUDY_SCHEDULE_UPDATE;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;
import static com.myproject.manageyourschedule.activities.LoginActivity.storageReference;
import static com.myproject.manageyourschedule.activities.MainActivity.adapter;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListSchedule;

//import static com.myproject.manageyourschedule.activities.MainActivity.ref;

public class StudyScheduleActivity extends AppCompatActivity {


    //2020.03.21+(s)
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String TAG = "StudyScheduleActivity";
    //2020.03.21+(e)

    // 리스트뷰
    ListView listView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule);

        listView = findViewById(R.id.listView);
        FloatingActionButton addButton = findViewById(R.id.addButton);


        //뷰
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        Intent intent = getIntent();
        Schedule schedule = (Schedule) intent.getSerializableExtra("studyScheduleData");

        if (schedule != null) {

            /*일정이 추가되었을 경우, 라디오버튼 위치가 View에 보여야 하므로 arraylist의 개수에서 1을 뺀다.
            listViewRadioBtnPosition = (arrayListSchedule.size() - 1);*/
            //2020.04.05+(s)

            //Map<String,Schedule> scheduleMap= new HashMap<>();
            //scheduleMap.put(schedule.getId(),schedule);

            /*DatabaseReference ref;
            DatabaseReference pushTime = refTimestamp.push();
            String key = pushTime.getKey();
            pushTime.setValue(schedule.getId());
            schedule.setTimeKey(key);
            ref = database.getReference(schedule.getId());
            ref.setValue(schedule);
            ref = database.getReference(schedule.getId());*/


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
            //2020.04.14+(s)

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

            arrayListSchedule.add(schedule);
            //추가 되었다고 어뎁터한테 알려주기
            adapter.notifyDataSetChanged();
            //2020.04.05+(e)
            /*
            //2020.03.21+(s)
            // SharedPreferences 객체 생성, MODE_PRIVATE: 이 앱에서만 사용, StudySchedule 이라는 이름을 가진 SharedPreferences를 생성한다.
            //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.
            preferences = getSharedPreferences("StudySchedule", MODE_PRIVATE);
            editor = preferences.edit();

            StringBuffer key = new StringBuffer(("userEmail@google.com"));
            key.append("★").append(0);
            //2020.03.27+ 일정의 index는 arrayListSchedule 크기에서 1을 뺀 숫자이다.
            key.append("★").append(arrayListSchedule.size() - 1);


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

            editor.putString(String.valueOf(key), String.valueOf(value));
            editor.apply();
            //2020.03.21+(e)*/


            //2020.03.31+(s)

            Intent intentService = new Intent(StudyScheduleActivity.this, NotifyService.class);
            startService(intentService);
            //2020.03.31+(e)

        }

        //리스트뷰 아이템 클릭 리스너 -> 일정 상세 페이지로 이동하여 데이터를 보여주는 부분, item 클릭하면 상세 보기로 넘어가서 수정할 수 있게 한다. 리스트뷰 동적으로 데이터 수정하는 부분
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(StudyScheduleActivity.this, (position + 1) + "번째 아이템 선택", Toast.LENGTH_SHORT).show();

                Schedule schedule = arrayListSchedule.get(position);
                /*//2020.03.21+(s)
                preferences = getSharedPreferences("StudySchedule", MODE_PRIVATE);
                String value = preferences.getString(("userEmail@google.com") + "★" + 0 + "★" + position, null);

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

                }*/
                //2020.03.21+(e)

                Intent intent = new Intent(StudyScheduleActivity.this, StudyScheduleUpdateActivity.class);
                intent.putExtra("CorrespondingSchedule", schedule);
                intent.putExtra("itemPosition", position);
                startActivityForResult(intent, STUDY_SCHEDULE_UPDATE);


            }
        });

        //listview에 동적으로 데이터 추가하는 부분
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudyScheduleActivity.this, StudyScheduleAddActivity.class);
                startActivityForResult(intent, STUDY_SCHEDULE_ADD);
            }
        });


    }

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


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudyScheduleActivity.this);
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
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {
        toMain();
    }

    private void toMain() {
        Intent intent = new Intent(StudyScheduleActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == STUDY_SCHEDULE_ADD) {
                Schedule schedule = (Schedule) data.getSerializableExtra("addStudyScheduleData");

                //해당 arrayList 마지막 위치에 item을 추가한다.
                arrayListSchedule.add(schedule);
                //2020.04.05+(s)
                /*DatabaseReference ref;
                DatabaseReference pushTime = refTimestamp.push();
                String key = pushTime.getKey();
                pushTime.setValue(schedule.getId());
                schedule.setTimeKey(key);
                ref = database.getReference(schedule.getId());
                ref.setValue(schedule);*/
                //2020.04.05+(e)

                //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.

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

                adapter.notifyDataSetChanged();

                //2020.03.31+(s)

                Intent intentService = new Intent(StudyScheduleActivity.this, NotifyService.class);
                startService(intentService);
                //2020.03.31+(e)

            } else if (requestCode == STUDY_SCHEDULE_UPDATE) {

                int pos = data.getIntExtra("itemPosition", 0);
                Schedule updatedStudySchedule = (Schedule) data.getSerializableExtra("updateStudyScheduleData");
                arrayListSchedule.set(pos, (updatedStudySchedule));

                /*DatabaseReference ref;
                ref = database.getReference(updatedStudySchedule.getId());
                ref.setValue(updatedStudySchedule);*/
                //2020.03.21+(s)
                // SharedPreferences 객체 생성, MODE_PRIVATE: 이 앱에서만 사용, Login 이라는 이름을 가진 SharedPreferences를 생성한다.
                //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.

                //2020.04.14+(s) firestore version
                uploadImageToCloudStorage(updatedStudySchedule);
                firebaseFirestore.collection("schedule").document(updatedStudySchedule.getId())
                        .set(updatedStudySchedule)
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

                adapter.notifyDataSetChanged();
                //2020.03.21+(e)

                //2020.03.31+(s)

                Intent intentService = new Intent(StudyScheduleActivity.this, NotifyService.class);
                startService(intentService);
                //2020.03.31+(e)
            }
        }
    }
}


