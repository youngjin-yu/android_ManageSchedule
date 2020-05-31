package com.myproject.manageyourschedule.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.adapters.ListViewAdapter;
import com.myproject.manageyourschedule.adapters.RecyclerViewAdapter;
import com.myproject.manageyourschedule.adapters.RecyclerViewByDateAdapter;
import com.myproject.manageyourschedule.service.NotifyService;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static com.myproject.manageyourschedule.Constant.Beginning;
import static com.myproject.manageyourschedule.Constant.STUDY_SCHEDULE;
import static com.myproject.manageyourschedule.Constant.entranceMainActivity;
import static com.myproject.manageyourschedule.Constant.entranceOtherSchedule;
import static com.myproject.manageyourschedule.Constant.entranceStudySchedule;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListScheduleId;
import static com.myproject.manageyourschedule.activities.LoginActivity.database;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;
import static com.myproject.manageyourschedule.activities.LoginActivity.loginEmailTextInputEditText;
import static com.myproject.manageyourschedule.activities.LoginActivity.storageReference;
import static com.myproject.manageyourschedule.activities.SettingsActivity.isSwitchOn;


public class MainActivity extends AppCompatActivity {


    // 연,월 텍스트뷰
    private int yearOfSchedule;
    private int monthOfSchedule;
    private int dayOfMonthOfSchedule;


    //캘린더 변수
    private Calendar mCal;
    //쉐어드 변수
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static ArrayList<Schedule> arrayListSchedule = new ArrayList<>();
    public static ListViewAdapter adapter = new ListViewAdapter(arrayListSchedule);

    public static ArrayList<Schedule> arrayListOtherSchedule = new ArrayList<>();
    public static RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(arrayListOtherSchedule);

    public static ArrayList<Schedule> arrayListAllSchedule = new ArrayList<>();
    public static RecyclerViewByDateAdapter recyclerViewByDateAdapter = new RecyclerViewByDateAdapter(arrayListAllSchedule);

    public static DatabaseReference refTimestamp = database.getReference("Timestamp");
    public static ArrayList<String> arrayListTimeStamp = new ArrayList<>();
    /*public static DatabaseReference refStudySchedule = database.getReference("StudySchedule");
    public static DatabaseReference refOtherSchedule = database.getReference("OtherSchedule");
    public static DatabaseReference refScheduleID = database.getReference("ScheduleID");*/
    // 날짜
    private Button workScheduleBtn, personalScheduleBtn, statisticsBtn, exerciseScheduleBtn, settingsBtn, logoutBtn, exitBtn;

    private FloatingActionButton floatingActionButton;
    public static Map signUpHashMap = new HashMap();
    public static ArrayList<Schedule> myScheduleList = new ArrayList<>();
    private String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,getSigneture(this));

        workScheduleBtn = findViewById(R.id.work_button);
        personalScheduleBtn = findViewById(R.id.personal_work_button);

        exitBtn = findViewById(R.id.exit_button);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        settingsBtn = findViewById(R.id.settings_button);
        logoutBtn = findViewById(R.id.logout_button);

        DatePicker datePicker = findViewById(R.id.datePicker);


        // 업무 일정 버튼 눌렀을 때
        workScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent workIntent = new Intent(MainActivity.this, StudyScheduleActivity.class);
                startActivity(workIntent);
                // LoginActivity Destory
                //finish();

            }
        });

        // 개인 일정 버튼 눌렀을 때
        personalScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent personalIntent = new Intent(MainActivity.this, OtherScheduleActivity.class);
                startActivity(personalIntent);
                // LoginActivity Destory
                //finish();

            }
        });


        //환경설정 버튼을 눌렀을 때
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        //로그아웃 버튼을 눌렀을 때
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toLogin();
            }
        });


        // 종료 버튼 눌렀을 때
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isExit();
                /*del 2020.03.29 로그아웃 기능 삭제
                 Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logoutIntent);
                // LoginActivity Destory
                finish();*/

            }
        });

        // 일정 추가하였을 경우
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                //activity를 호출한 후 결과를 받아야 하므로 startActivityForResult를 사용한다.
                startActivityForResult(intent, 52);
            }
        });


        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        final Date date = new Date(now);
        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        //현재 날짜 텍스트뷰에 뿌려줌
        //tvDate.setText(curYearFormat.format(date) + "/" + curMonthFormat.format(date) + "/" + curDayFormat.format(date));


        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //


        //2020.03.31+(s)
        getDataFromFirebase();
        //getSwitchDataFromSharedPreference();
        //getStudyScheduleFromSharedPreference();
        //getOtherScheduleFromSharedPreference();
        //2020.03.31+(e)

        //2020.04.03+(s)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int month, int dayOfMonth) {


                    yearOfSchedule = year;
                    monthOfSchedule = month + 1;
                    dayOfMonthOfSchedule = dayOfMonth;


                    arrayListAllSchedule.addAll(arrayListSchedule);
                    arrayListAllSchedule.addAll(arrayListOtherSchedule);

                    ArrayList<Schedule> returnedArrayList = compareDates(arrayListAllSchedule);
                    arrayListAllSchedule.clear();
                    arrayListAllSchedule.addAll(returnedArrayList);

                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            recyclerViewByDateAdapter.notifyDataSetChanged();
                        }
                    };
                    handler.post(r);
                    //해당 날짜에 일정이 있다면 유저에게 알려준다.
                    if ((returnedArrayList.size()) != 0) {
                        Intent intent = new Intent(MainActivity.this, ViewByDateActivity.class);
                        startActivity(intent);
                    }
                    //해당 날짜에는 일정이 없음을 유저에게 알려준다.
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("해당 날짜에는 일정이 없습니다.");
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
                }
            });
        }
        /*refScheduleID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                String value = (String) dataSnapshot.getValue();
                Log.d("Database", "key is : " + key);
                Log.d("Database", "value is : " + value);
                JSONObject jsonObject = (JSONObject) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        //2020.04.03+(e)
    }

    //  카카오맵 KeyHash 값 얻는 부분
    public static String getSigneture(Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);

            for(int i = 0; i < packageInfo.signatures.length; i++){
                Signature signature = packageInfo.signatures[i];
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            }

        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume입니다.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause입니다.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop입니다.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy입니다.");
    }

    //static 변수를 통해 한번만 실행되도록 할 것
    private void getDataFromFirebase() {
        if (entranceMainActivity == Beginning) {
            firebaseFirestore.collection("schedule").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            arrayListScheduleId.add(document.getId());
                        }
                        Log.d(TAG, arrayListScheduleId.toString());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                    for (int i = 0; i < arrayListScheduleId.size(); i++) {
                        firebaseFirestore.collection("schedule")
                                .document(arrayListScheduleId.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        Schedule schedule = task.getResult().toObject(Schedule.class);
                                        if (schedule != null) {
                                            if (schedule.getUserEmail().equals(loginEmailTextInputEditText.getText().toString())) {
                                                if (schedule.getImageFilePath() != null) {
                                                    File file = new File(schedule.getImageFilePath());
                                                    if ((!((schedule.getImageFilePath().equals("")))) && (!(file.exists()))) {
                                                        String filePath = downloadImage(schedule);
                                                        schedule.setImageFilePath(filePath);
                                                    }
                                                }
                                                if (schedule.getType() == STUDY_SCHEDULE) {
                                                    //업무 일정인 경우
                                                    arrayListSchedule.add(schedule);
                                                    adapter.notifyDataSetChanged();
                                                } else {
                                                    //업무 외 일정인 경우
                                                    arrayListOtherSchedule.add(schedule);
                                                    recyclerViewAdapter.notifyDataSetChanged();
                                                }

                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }

                        });
                    }
                }
            });
        }
        entranceMainActivity++;
        /*
            refTimestamp.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot);
                    String date[] = dataSnapshot.toString().split("=");
                    System.out.println(dataSnapshot.toString());

                    for (int i = 0; i < date.length; i++) {
                        StringBuffer stringBuffer = new StringBuffer();
                        System.out.println("date[" + i + "] : " + date[i]);
                        if (date[i].length() != 24) {
                            int length = date[i].length();
                            int padding = 24 - length;
                            stringBuffer.append(date[i]);
                            for (int k = 0; k < padding; k++) {
                                stringBuffer.append(0);
                            }
                            //date[i]=(String.format("%02d", 8));
                        }
                        String source = stringBuffer.substring(0, 23);
                        String pattern = "^[0-9][0-9][0-9][0-9]\\-[0-9][0-9]\\-[0-9][0-9]\\-[0-9][0-9]\\:[0-9][0-9]\\:[0-9][0-9]\\:[0-9][0-9][0-9]$"; // ^시작,$끝
                        if (!source.matches(pattern)) {// (년도-월-일) 의 패턴으로 넘어오는지 체크
                            System.out.println("년월일의 패턴이 아닙니다.");
                            continue;
                        }
                        arrayListTimeStamp.add(source);
                        Log.d(TAG, date[i]);
                    }

                    for (int i = 0; i < arrayListTimeStamp.size(); i++) {
                        DatabaseReference ref = database.getReference(arrayListTimeStamp.get(i));
                        Log.d(TAG, arrayListTimeStamp.get(i));
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //String key = dataSnapshot.getKey();
                                if (dataSnapshot.getValue() != null) {
                                    final Schedule[] schedule = {new Schedule()};
                                    schedule[0] = dataSnapshot.getValue(Schedule.class);
                                    //데이터가 있을 때만 파이어베이스에서 데이터를 로드한다.
                                    Log.d("Database", "schedule[0]: " + schedule[0]);
                                    if ((schedule[0].getUserEmail()).equals(loginEmailTextInputEditText.getText().toString())) {


                                        if (schedule[0].getType() == STUDY_SCHEDULE) {

                                            arrayListSchedule.add(schedule[0]);
                                            //추가 되었다고 어뎁터한테 알려주기
                                            adapter.notifyDataSetChanged();


                                        } else {

                                            arrayListOtherSchedule.add(schedule[0]);
                                            //arrayListOtherSchedule.add(schedule);
                                            //추가 되었다고 어뎁터한테 알려주기

                                            //user아이디(이메일)+일정의 종류+일정의 index를 key로 한다.
                                            //2020.04.05+(s)
                                            //임시삭제 2020.04.06 refOtherSchedule.push().setValue(schedule);
                                            //2020.04.05+(e)

                                            recyclerViewAdapter.notifyDataSetChanged();


                                        }

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                            }
                        });
                    }
                    //Log.d("MainActivity", String.valueOf(dataSnapshot.getValue()));
                    //System.out.println(dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            *//*for(int i=0; i<arrayListSchedule.size(); i++){
               if(arrayListSchedule.get(i).getImageFile(arrayListSchedule.get(i).getImageFilePath())==null){
                    //arrayListSchedule.get(i).
               }
            *//*

        }
        */
    }

    private String downloadImage(Schedule schedule) {
        StorageReference ref = storageReference.child("images/" + (schedule.getImageFilePath().substring(schedule.getImageFilePath().length() - 22)));
        File localFile = null;

        try {
            localFile = createImageFile(schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        return (localFile.getAbsolutePath());
        /*Uri uri = Uri.fromFile(new File(schedule.getImageFilePath()));
        final StorageReference ref = storageReference.child("images/"+(schedule.getImageFilePath().substring(schedule.getImageFilePath().length()-22)));
        UploadTask uploadTask = ref.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });*/

        //Log.d(TAG,urlTask.getResult().toString());
    }

    private File createImageFile(Schedule schedule) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //이미지 이름
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + (schedule.getImageFilePath().substring((schedule.getImageFilePath().length() - 22))));
        return storageDir;
    }

    private ArrayList<Schedule> compareDates(ArrayList<Schedule> arrayListAllSchedule) {
        Iterator<Schedule> iterator = arrayListAllSchedule.iterator();
        ArrayList<Schedule> arrayListToReturn = new ArrayList<>();
        while (iterator.hasNext()) {

            StringBuffer stringBuffer = new StringBuffer();
            Schedule schedule = iterator.next();
            String yearOfArrayList = String.format("%04d", schedule.getYear());
            String monthOfArrayList = String.format("%02d", schedule.getMonth());
            String dayOfArrayList = String.format("%02d", schedule.getDayOfMonth());

            stringBuffer.append(yearOfArrayList).append("-").append(monthOfArrayList).append("-").append(dayOfArrayList);

            StringBuffer stringBufferSelectedDates = new StringBuffer();

            String yearOfSelectedSchedule = String.format("%04d", yearOfSchedule);
            String monthOfSelectedSchedule = String.format("%02d", monthOfSchedule);
            String dayOfSelectedSchedule = String.format("%02d", dayOfMonthOfSchedule);

            stringBufferSelectedDates.append(yearOfSelectedSchedule).append("-").append(monthOfSelectedSchedule).append("-").append(dayOfSelectedSchedule);

            Date dateOfSelected = null;
            Date dateOfArrayList = null;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dateOfArrayList = simpleDateFormat.parse(String.valueOf(stringBuffer));
                dateOfSelected = simpleDateFormat.parse(String.valueOf(stringBufferSelectedDates));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            //arrayListToReturn = new ArrayList<>();
            if (dateOfArrayList.compareTo(dateOfSelected) == 0) {
                arrayListToReturn.add(schedule);
            }

        }
        return arrayListToReturn;
    }

    private void getOtherScheduleFromSharedPreference() {

        //처음 프로그램 시작할 때만 sharedpreference에서 정보를 읽어온다., MODE_PRIVATE: 이 앱에서만 사용
        if (entranceOtherSchedule == Beginning) {
            preferences = getSharedPreferences("OtherSchedule", MODE_PRIVATE);

            Map<String, ?> keys = preferences.getAll();
            //로드를 할지 말지 결정
            boolean loadOrNot = false;
            //리싸이클러뷰 item 총 개수
            ArrayList numberOfListViewList = new ArrayList<Integer>();

            //SharedPreferences에서 해당 일정이 총 몇 개 인지를 알아본다.
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                if (entry.getKey().contains(("userEmail@google.com"))) {
                    loadOrNot = true;
                    String[] key = entry.getKey().split("★", -1);
                    //key[0]: 유저 이메일, key[1]: 어느 일정 인지 구분, key[2]: 해당 일정의 인덱스
                    numberOfListViewList.add(Integer.parseInt(key[2]));
                }
            }
            //SharedPreferences에서 데이터를 가져온다면
            if (loadOrNot) {

                for (int i = 0; i < (numberOfListViewList).size(); i++) {
                    String value = preferences.getString(("userEmail@google.com") + "★" + 1 + "★" + i, null);

                    if (value != null) {
                        Schedule schedule = new Schedule();
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

                        schedule.setContent(scheduleArray[10]);
                        if (scheduleArray[11] != null && (!(scheduleArray[11].equals("null")))) {
                            schedule.setImageFilePath(scheduleArray[11]);
                        }
                        schedule.setSatisfaction(Float.parseFloat(scheduleArray[12]));

                        /*del 2020.03.29
                        if (scheduleArray[11] != null && (!(scheduleArray[11].equals("null")))) {
                            File file = new File(scheduleArray[11]);
                            schedule.setImageFile(file);
                        }*/

                        arrayListOtherSchedule.add(schedule);
                        //일정이 추가되었을 경우, 라디오버튼 위치가 View에 보여야 하므로 arraylist의 개수에서 1을 뺀다.
                        //listViewRadioBtnPosition = (arrayListSchedule.size() - 1);
                        //추가 되었다고 어뎁터한테 알려주기
                        recyclerViewAdapter.notifyDataSetChanged();

                        //2020.03.31+(s)

                        Intent intentService = new Intent(MainActivity.this, NotifyService.class);
                        startService(intentService);
                        //2020.03.31+(e)
                    }
                }
            }
        }
        entranceOtherSchedule++;
    }

    private void getStudyScheduleFromSharedPreference() {

        //처음 프로그램 시작할 때만 sharedpreference에서 정보를 읽어온다., MODE_PRIVATE: 이 앱에서만 사용
        if (entranceStudySchedule == Beginning) {
            preferences = getSharedPreferences("StudySchedule", MODE_PRIVATE);

            Map<String, ?> keys = preferences.getAll();
            //로드를 할지 말지 결정
            boolean loadOrNot = false;
            //리스트뷰 item 총 개수
            ArrayList numberOfListViewList = new ArrayList<Integer>();

            //SharedPreferences에서 해당 일정이 총 몇 개 인지를 알아본다.
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                if (entry.getKey().contains(("userEmail@google.com"))) {
                    loadOrNot = true;
                    String[] key = entry.getKey().split("★", -1);
                    //key[0]: 유저 이메일, key[1]: 어느 일정 인지 구분, key[2]: 해당 일정의 인덱스
                    numberOfListViewList.add(Integer.parseInt(key[2]));
                }
            }
            //SharedPreferences에서 데이터를 가져온다면
            if (loadOrNot) {

                for (int i = 0; i < (numberOfListViewList).size(); i++) {
                    String value = preferences.getString(("userEmail@google.com") + "★" + 0 + "★" + i, null);

                    if (value != null) {
                        Schedule schedule = new Schedule();
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

                        schedule.setContent(scheduleArray[10]);
                        if ((scheduleArray[11] != null) && (!(scheduleArray[11].equals("null")))) {
                            schedule.setImageFilePath(scheduleArray[11]);
                        }
                        schedule.setSatisfaction(Float.parseFloat(scheduleArray[12]));

                        /* del 2020.03.29
                        if (scheduleArray[11] != null && (!(scheduleArray[11].equals("null")))) {
                            File file = new File(scheduleArray[11]);
                            schedule.setImageFile(file);
                        }*/

                        arrayListSchedule.add(schedule);
                        //일정이 추가되었을 경우, 라디오버튼 위치가 View에 보여야 하므로 arraylist의 개수에서 1을 뺀다.
                        //listViewRadioBtnPosition = (arrayListSchedule.size() - 1);
                        //추가 되었다고 어뎁터한테 알려주기
                        adapter.notifyDataSetChanged();

                        //2020.03.31+(s)

                        Intent intentService = new Intent(MainActivity.this, NotifyService.class);
                        startService(intentService);
                        //2020.03.31+(e)
                    }
                }
            }
        }
        entranceStudySchedule++;
    }


    //2020.03.31+(s) 쉐어드에서 isSwitchOn 스위치 정보를 읽어온다.
    private void getSwitchDataFromSharedPreference() {
        if (entranceMainActivity == Beginning) {
            preferences = getSharedPreferences("NotifySwitch", MODE_PRIVATE);
            isSwitchOn = preferences.getBoolean("NotifyToUser", false);
        }
        entranceMainActivity++;
    }
    //2020.03.31+(e)


    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {
        isExit();
    }

    // 종료 확인 다이얼로그 창
    private void isExit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //메인화면으로 이동할지 묻는 부분
    private void toLogin() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("로그인 화면으로 이동하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //메인액티비티 실행하는 횟수 초기화
                entranceMainActivity=0;
                //schedule Id를 담고있는 arrayList 초기화
                arrayListScheduleId.clear();
                //업무 일정을 담고있는 arrayList 초기화
                arrayListSchedule.clear();
                //업무 외 일정을 담고있는 arrayList 초기화
                arrayListOtherSchedule.clear();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }


}
