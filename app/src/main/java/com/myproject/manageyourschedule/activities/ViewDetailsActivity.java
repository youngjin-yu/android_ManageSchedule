package com.myproject.manageyourschedule.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;

import java.io.File;

public class ViewDetailsActivity extends AppCompatActivity {

    private EditText title;
    private EditText place;
    private EditText uri;
    private EditText content;
    private TextView textViewDate;
    private TextView textViewStartTime;
    private TextView textViewEndTime;
    private int yearOfSchedule;
    private int monthOfSchedule;
    private int dayOfMonthOfSchedule;
    private int hourOfStart;
    private int minuteOfStart;
    private int hourOfEnd;
    private int minuteOfEnd;
    //2020.03.18+(s)
    private float satisfaction;
    private EditText comment;
    private RatingBar ratingBarUpdateSchedule;
    //2020.03.18+(s)

    //2020.03.19+(s)
    //업로드된 이미지
    private ImageView ivImage;
    //실제 사진 파일 경로
    private String currentPhotoPath;
    //축소된 비트맵 이미지
    private Bitmap resizedBitmap;
    //회전된 비트맵 이미지
    private Bitmap rotatedBitmap;
    //이미지 파일 경로
    private String imageFilePath;
    //2020.03.19+(e)

    //2020.03.23+(s)
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //2020.03.23+(e)

    private Boolean isPermission = true;
    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        //Button postDataBtn = findViewById(R.id.updateOtherScheduleButton);
        title = findViewById(R.id.titleOfSchedule_UpdateSchedule);
        place = findViewById(R.id.placeOfSchedule_UpdateSchedule);

        content = findViewById(R.id.contentOfSchedule_UpdateSchedule);
        textViewDate = findViewById(R.id.textViewDate_UpdateSchedule);
        textViewStartTime = (TextView) findViewById(R.id.textViewStartTime_UpdateSchedule);
        textViewEndTime = (TextView) findViewById(R.id.textViewEndTime_UpdateSchedule);
        Button startTimeBtn = findViewById(R.id.startTimeButton_UpdateSchedule);
        Button endTimeBtn = findViewById(R.id.endTimeButton_UpdateSchedule);
        Button dateSelectBtn = findViewById(R.id.dateSelectButton_UpdateSchedule);
        //ImageView imageView = findViewById(R.id.postImageView_UpdateSchedule);
        //2020.03.18+(s)
        //Button deleteDataBtn = findViewById(R.id.deleteOtherScheduleButton);
        ratingBarUpdateSchedule = findViewById(R.id.satisfactionUpdateSchedule);

        ivImage = findViewById(R.id.Uploaded_UpdateSchedule);
        //2020.03.18+(e)

        //리싸이클러뷰 아이템 클릭 리스너 -> 일정 상세 페이지로 이동하여 데이터를 보여주는 부분
        showDetailsOtherSchedule();


        //일자 선택 하였을 경우
        dateSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });

        //시작시간 선택하였을 경우
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime();
            }
        });

        //끝나는 시간 선택하였을 경우
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndTime();
            }
        });

        //만족도에 대한 점수를 매겼을 경우
        ratingBarUpdateSchedule.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                satisfaction = rating;
            }
        });


    }

    private void showDetailsOtherSchedule() {

        position = getIntent().getIntExtra("itemPosition", 0);
        Schedule correspondingSchedule = (Schedule) (getIntent().getSerializableExtra("CorrespondingSchedule"));

        if (correspondingSchedule != null) {
            title.setText((correspondingSchedule.getTitle()));
            yearOfSchedule = correspondingSchedule.getYear();
            monthOfSchedule = correspondingSchedule.getMonth();
            dayOfMonthOfSchedule = correspondingSchedule.getDayOfMonth();
            hourOfStart = correspondingSchedule.getHourOfStart();
            minuteOfStart = correspondingSchedule.getMinuteOfStart();
            hourOfEnd = correspondingSchedule.getHourOfEnd();
            minuteOfEnd = correspondingSchedule.getMinuteOfEnd();
            place.setText(correspondingSchedule.getPlace());

            content.setText(correspondingSchedule.getContent());

            //2020.03.18+(s)
            imageFilePath = (correspondingSchedule.getImageFilePath());
            //유저가 일정에 사진을 첨부 안 할 수도 있으므로 4배 늘리면 안된다.
            if (imageFilePath != null) {
                File file = new File(correspondingSchedule.getImageFilePath());
                //파일 경로에 실제로 파일이 존재 한다면, 그 일정에 해당하는 사진파일을 불러들이고
                if(file.exists() && (!file.isDirectory())){
                    //로컬에 존재하는 파일을 그대로 읽어올 때 쓴다.
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    //비트맵을 다른 일정 수정 액티비티에 뿌려준다.
                    ivImage.setImageBitmap(bitmap);
                }else{
                    //파일 경로에 실제로 파일이 존재 하지 않는다면, 그 일정에는 default 달력 사진을 넣는다.
                    ivImage.setImageResource(R.drawable.ic_calendar);
                }
            }
            satisfaction = (correspondingSchedule.getSatisfaction());
            ratingBarUpdateSchedule.setRating(satisfaction);

            //2020.03.18+(e)
        }
    }

    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {
        toPreviousMenu();
    }

    private void toPreviousMenu() {

        Intent intent = new Intent(ViewDetailsActivity.this, ViewByDateActivity.class);
        startActivity(intent);
    }

    private void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                yearOfSchedule = year;
                monthOfSchedule = month + 1;
                dayOfMonthOfSchedule = dayOfMonth;
                textViewDate.setText(year + "년" + (month + 1) + "월" + dayOfMonth + "일");

            }
        }, 2020, 2, 11);

        datePickerDialog.setMessage("날짜 선택");
        datePickerDialog.show();
    }

    private void showStartTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourOfStart = hourOfDay;
                minuteOfStart = minute;
                textViewStartTime.setText(hourOfDay + "시" + minute + "분");
            }
        }, 9, 00, true);

        timePickerDialog.setMessage("시간 선택");
        timePickerDialog.show();
    }

    private void showEndTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourOfEnd = hourOfDay;
                minuteOfEnd = minute;
                textViewEndTime.setText(hourOfDay + "시" + minute + "분");
            }
        }, 11, 00, true);

        timePickerDialog.setMessage("시간 선택");
        timePickerDialog.show();
    }


}
