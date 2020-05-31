package com.myproject.manageyourschedule.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.service.NotifyService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.myproject.manageyourschedule.Constant.CAMERA_CODE;
import static com.myproject.manageyourschedule.Constant.GALLERY_CODE;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;
import static com.myproject.manageyourschedule.activities.LoginActivity.loginEmailTextInputEditText;
import static com.myproject.manageyourschedule.activities.LoginActivity.storageReference;
import static com.myproject.manageyourschedule.activities.MainActivity.adapter;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListSchedule;


public class StudyScheduleUpdateActivity extends AppCompatActivity {

    private EditText title;
    private EditText place;

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
    private RatingBar ratingBarUpdateStudySchedule;
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
    private String timeKey;
    //2020.03.19+(e)

    //2020.03.21+(s)
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //2020.03.21+(e)
    private String photoPathFromGallery;

    private Boolean isPermission = true;
    // 카메라에서 온 화면인지 앨범에서 온 화면인지 구분
    private Boolean isCamera = false;
    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;
    private int position;

    private int scheduleType;
    private String scheduleId;
    private String TAG = "StudyScheduleUpdateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_schedule_update);

        Button postDataBtn = findViewById(R.id.updateStudyScheduleButton);
        title = findViewById(R.id.titleOfStudySchedule_UpdateStudySchedule);
        place = findViewById(R.id.placeOfSchedule_UpdateStudySchedule);

        content = findViewById(R.id.contentOfSchedule_UpdateStudySchedule);
        textViewDate = findViewById(R.id.textViewDate_UpdateStudySchedule);
        textViewStartTime = (TextView) findViewById(R.id.textViewStartTime_UpdateStudySchedule);
        textViewEndTime = (TextView) findViewById(R.id.textViewEndTime_UpdateStudySchedule);
        Button startTimeBtn = findViewById(R.id.startTimeButton_UpdateStudySchedule);
        Button endTimeBtn = findViewById(R.id.endTimeButton_UpdateStudySchedule);
        Button dateSelectBtn = findViewById(R.id.dateSelectButton_UpdateStudySchedule);
        ImageView imageView = findViewById(R.id.postImageView_UpdateStudySchedule);
        //2020.03.18+(s)
        Button deleteDataBtn = findViewById(R.id.deleteStudyScheduleButton);
        ratingBarUpdateStudySchedule = findViewById(R.id.satisfactionUpdateStudySchedule);

        ivImage = findViewById(R.id.Uploaded_UpdateStudySchedule);
        //2020.03.18+(e)

        //리스트뷰 아이템 클릭 리스너 -> 일정 상세 페이지로 이동하여 데이터를 보여주는 부분
        showDetailsStudySchedule();

        //2020.04.08+(s) 제목과 장소는 5줄까지, 내용은 12줄까지 입력하도록 한다.
        title.addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (title.length() > 100)
                {
                    title.setText(previousString);
                    title.setSelection(title.length());
                }
            }
        });

        place.addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (place.length() > 100)
                {
                    place.setText(previousString);
                    place.setSelection(place.length());
                }
            }
        });

        content.addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (content.length() > 250)
                {
                    content.setText(previousString);
                    content.setSelection(content.length());
                }
            }
        });
        //2020.04.08+(e)

        final Schedule schedule = new Schedule();

        // 프로필사진 선택 클릭시
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence menu[] = new CharSequence[]{"사진 찍기", "앨범에서 선택"};

                //다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(StudyScheduleUpdateActivity.this);

                //위에 문자 배열과 다이얼로그 리스트를 합침.
                builder.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isPermission.equals(true)) {
                            if (which == 0) {
                                // 사진 찍기
                                takePhoto();
                            } else {
                                // 갤러리 이동
                                goToAlbum();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                });
                // 다이얼로그 화면에 보이기
                builder.show();
            }
        });


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
        ratingBarUpdateStudySchedule.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                satisfaction = rating;
            }
        });

        //등록 버튼 눌렀을 때
        postDataBtn.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View v) {
                                               if (((title.getText().toString()).equals("")) || ((content.getText().toString()).equals(""))) {
                                                   AlertDialog.Builder builder = new AlertDialog.Builder(StudyScheduleUpdateActivity.this);
                                                   builder.setMessage("일정의 제목과 내용을 입력해 주세요.");
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
                                               } else {
                                                   schedule.setId(scheduleId);
                                                   schedule.setType(scheduleType);
                                                   schedule.setTitle(title.getText().toString());
                                                   schedule.setYear(yearOfSchedule);
                                                   schedule.setMonth(monthOfSchedule);
                                                   schedule.setDayOfMonth(dayOfMonthOfSchedule);
                                                   schedule.setHourOfStart(hourOfStart);
                                                   schedule.setMinuteOfStart(minuteOfStart);
                                                   schedule.setHourOfEnd(hourOfEnd);
                                                   schedule.setMinuteOfEnd(minuteOfEnd);
                                                   schedule.setPlace(place.getText().toString());

                                                   schedule.setContent(content.getText().toString());

                                                   schedule.setImageFilePath(imageFilePath);
                                                   //2020.03.18+(s)
                                                   schedule.setSatisfaction(satisfaction);

                                                   schedule.setUserEmail((loginEmailTextInputEditText.getText().toString()));
                                                   schedule.setTimeKey(timeKey);
                                                   //2020.03.18+(e)

                                                   //업무 일정인 경우
                                                   Intent updateIntent = new Intent();
                                                   updateIntent.putExtra("updateStudyScheduleData", schedule);
                                                   updateIntent.putExtra("itemPosition", position);
                                                   setResult(RESULT_OK, updateIntent);
                                                   finish();


                                               }
                                           }

                                       }
        );

        //2020.03.27+(s)
        //일정삭제 버튼 눌렀을 경우
        deleteDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*//2020.03.21+(s)
                preferences = getSharedPreferences("StudySchedule", MODE_PRIVATE);
                editor = preferences.edit();


                editor.remove(("userEmail@google.com") + "★" + 0 + "★" + position);

                int checkPosition = position;
                while (checkPosition <= arrayListSchedule.size()) {
                    if (preferences.getString
                            (("userEmail@google.com") + "★" + 0 + "★" + (checkPosition + 1), null) != null) {
                        String originalValue = preferences.getString(("userEmail@google.com") + "★" + 0 + "★" + (checkPosition + 1), null);
                        editor.putString(("userEmail@google.com") + "★" + 0 + "★" + (checkPosition), originalValue);
                    }

                    //제일 아래에 있던 아이템은 삭제한다.
                    if (checkPosition == arrayListSchedule.size()) {
                        editor.remove(("userEmail@google.com") + "★" + 0 + "★" + checkPosition);
                    }
                    checkPosition++;
                }

                editor.apply();*/
                Schedule schedule = arrayListSchedule.get(position);

                //2020.04.14+(s)
                deleteImageFromCloudStorage(schedule);
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
                //2020.04.14+(e)
                arrayListSchedule.remove(position);

                /*DatabaseReference ref;
                ref = database.getReference(schedule.getId());
                ref.removeValue();
                refTimestamp.child(schedule.getTimeKey()).removeValue();*/

                // listview 갱신.
                adapter.notifyDataSetChanged();

                //2020.03.31+(s)

                Intent intentService = new Intent(StudyScheduleUpdateActivity.this, NotifyService.class);
                startService(intentService);
                //2020.03.31+(e)

                Intent intent = new Intent(StudyScheduleUpdateActivity.this, StudyScheduleActivity.class);
                startActivity(intent);
            }
        });
        //2020.03.27+(e)
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

    private void showDetailsStudySchedule() {

        position = getIntent().getIntExtra("itemPosition", 0);
        Schedule correspondingSchedule = (Schedule) (getIntent().getSerializableExtra("CorrespondingSchedule"));

        if (correspondingSchedule != null) {
            scheduleType = correspondingSchedule.getType();
            scheduleId = correspondingSchedule.getId();
            title.setText((correspondingSchedule.getTitle()));
            yearOfSchedule = correspondingSchedule.getYear();
            monthOfSchedule = correspondingSchedule.getMonth();
            dayOfMonthOfSchedule = correspondingSchedule.getDayOfMonth();
            hourOfStart = correspondingSchedule.getHourOfStart();
            minuteOfStart = correspondingSchedule.getMinuteOfStart();
            hourOfEnd = correspondingSchedule.getHourOfEnd();
            minuteOfEnd = correspondingSchedule.getMinuteOfEnd();
            textViewDate.setText((yearOfSchedule)+"년 "+monthOfSchedule+"월 "+dayOfMonthOfSchedule+"일");
            textViewStartTime.setText(hourOfStart+"시 "+minuteOfStart+"분");
            textViewEndTime.setText(hourOfEnd+"시 "+minuteOfEnd+"분");
            place.setText(correspondingSchedule.getPlace());

            content.setText(correspondingSchedule.getContent());
            timeKey=correspondingSchedule.getTimeKey();

            //2020.03.18+(s)
            imageFilePath = (correspondingSchedule.getImageFilePath());
            //유저가 일정에 사진을 첨부 안 할 수도 있으므로 4배 늘리면 안된다.
            if (imageFilePath != null) {
                File file = new File(correspondingSchedule.getImageFilePath());
                //파일 경로에 실제로 파일이 존재 한다면, 그 일정에 해당하는 사진파일을 불러들이고
                if(file.exists() && (!file.isDirectory())){
                    //로컬에 존재하는 파일을 그대로 읽어올 때 쓴다.
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
                    //비트맵을 업무 일정 수정 액티비티에 뿌려준다.
                    ivImage.setImageBitmap(bitmap);
                }else{
                    //파일 경로에 실제로 파일이 존재 하지 않는다면, 그 일정에는 default 달력 사진을 넣는다.
                    ivImage.setImageResource(R.drawable.ic_calendar);
                }
            }
            satisfaction = (correspondingSchedule.getSatisfaction());
            ratingBarUpdateStudySchedule.setRating(satisfaction);

            //2020.03.18+(e)
        }
    }

    //2020.03.19+(s)
    // 카메라 권한 설정 메소드

    private void takePhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }
        }
    }

    //
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //이미지 이름
        String mImageCaptureName = timeStamp + ".png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
    }
    //

    //카메라에서 가져오기
    private void getPictureForPhoto() {

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;
        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        // 2020.03.20+
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
        Date time = new Date();
        String timeNow = format1.format(time);
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        rotatedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, (resizedBitmap.getWidth()) / 4, (resizedBitmap.getHeight()) / 4, true);

        //tempFile = saveBitmapToJpeg(this, rotatedBitmap, "resizedCameraPhoto" + timeNow);
        tempFile = SaveBitmapToJPG(rotatedBitmap,currentPhotoPath,"");
        imageFilePath = tempFile.getAbsolutePath();
    }

    //2020.04.09+(s)
    private String makeImageFilePath() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        //이미지 이름
        String mImageCaptureName = timeStamp + ".png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName);
        photoPathFromGallery = storageDir.getAbsolutePath();
        return photoPathFromGallery;
    }
    //2020.04.09+(e)

    //2020.04.09+(s)    비트맵을 이미지 파일로 변환하는 부분
    public File SaveBitmapToJPG(Bitmap bitmap, String strFilePath, String filename) {

        File file = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!file.exists()) {
            file.mkdirs();
        }

        File fileItem = new File(strFilePath + filename);
        OutputStream outStream = null;

        try {
            fileItem.createNewFile();
            outStream = new FileOutputStream(fileItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileItem;
    }
    //2020.04.09+(e)

    public static File saveBitmapToJpeg(Context context, Bitmap bitmap, String name) {

        File storage = context.getFilesDir(); // 이 부분이 임시파일 저장 경로

        String fileName = name + ".jpg";  // 파일이름은 마음대로!

        File tempFile = new File(storage, fileName);

        try {
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;   // 임시파일 저장경로를 리턴해주면 끝!
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    //갤러리에서 가져오기
                    sendPicture(data.getData());
                    break;
                case CAMERA_CODE:
                    //카메라에서 가져오기
                    getPictureForPhoto();
                    break;
                default:
                    break;
            }
        }
    }

    //갤러리에서 가져오기
    private void sendPicture(Uri imgUri) {
        // path 경로
        String imagePath = getRealPathFromURI(imgUri);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        // 2020.03.20+
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
        Date time = new Date();
        String timeNow = format1.format(time);
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        rotatedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, (resizedBitmap.getWidth()) / 4, (resizedBitmap.getHeight()) / 4, true);

        tempFile = saveBitmapToJpeg(this, rotatedBitmap, "resizedGalleryPicture" + timeNow);//삭제예정
        //tempFile = SaveBitmapToJPG(rotatedBitmap,currentPhotoPath,"");
        try {
            tempFile = SaveBitmapToJPG(rotatedBitmap,makeImageFilePath(),"");
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageFilePath = tempFile.getAbsolutePath();
    }


    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }


    //2020.03.19+(e)


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

