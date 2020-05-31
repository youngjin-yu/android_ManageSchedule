package com.myproject.manageyourschedule.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.myproject.manageyourschedule.Constant.CAMERA_CODE;
import static com.myproject.manageyourschedule.Constant.GALLERY_CODE;
import static com.myproject.manageyourschedule.Constant.OTHER_SCHEDULE;
import static com.myproject.manageyourschedule.Constant.PLACE_SELECT;
import static com.myproject.manageyourschedule.Constant.STUDY_SCHEDULE;
import static com.myproject.manageyourschedule.activities.LoginActivity.loginEmailTextInputEditText;

public class AddScheduleActivity extends AppCompatActivity {


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
    //2020.03.21+ 업무 일정 인지 업무 외 일정 인지를 나타내는 변수로 static 변수로 만든다.
    public static int whichSchedule = -1;//처음에는 어느 일정 선택할지 모르므로 초기화 시킨다.
    private static final String TAG = "AddScheduleActivity";

    private RadioButton radioButtonStudy;
    private RadioButton radioButtonOther;

    private Boolean isPermission = true;
    // 받아올 이미지를 저장할 파일 변수
    private File tempFile;
    //2020.03.19+(s)
    //업로드 되는 이미지뷰
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

    //2020.04.09+(s) 저장되는 실제 경로
    private String savedRealPath;
    private String photoPathFromGallery;
    //2020.04.09+(e)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);

        Button postDataBtn = findViewById(R.id.postDataButton);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        title = findViewById(R.id.titleOfSchedule);
        place = findViewById(R.id.placeOfSchedule);

        content = findViewById(R.id.contentOfSchedule);
        textViewDate = findViewById(R.id.textViewDate);
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);
        Button startTimeBtn = findViewById(R.id.startTimeButton);
        Button endTimeBtn = findViewById(R.id.endTimeButton);
        Button dateSelectBtn = findViewById(R.id.dateSelectButton);
        ImageView imageView = findViewById(R.id.postImageView);
        ivImage = findViewById(R.id.UploadedImage);

        radioButtonStudy = findViewById(R.id.radioButtonStudy);
        radioButtonOther = findViewById(R.id.radioButtonOther);

        //2020.04.08+(s) 제목과 장소는 100글자까지, 내용은 250글자까지 입력하도록 한다.
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
                AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);

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

        //라디오 버튼 선택하였을 시 실행되는 부분
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);

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

        //등록 버튼 눌렀을 때
        postDataBtn.setOnClickListener(new View.OnClickListener() {

                                           @Override
                                           public void onClick(View v) {
                                               if (((title.getText().toString()).equals("")) || ((content.getText().toString()).equals(""))) {
                                                   AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
                                                   builder.setMessage("일정의 제목과 내용을 반드시 입력해 주세요.");
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
                                                   SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd-HH:mm:ss:SSS");
                                                   Date time = new Date();
                                                   String time1 = format.format(time);
                                                   //final String schedule_id = UUID.randomUUID().toString();
                                                   schedule.setId(time1);
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
                                                   //schedule.setImageFile(tempFile);
                                                   //2020.03.18+(s)
                                                   schedule.setSatisfaction(0);

                                                   schedule.setUserEmail((loginEmailTextInputEditText.getText().toString()));
                                                   //2020.03.18+(e)
                                                   //업무 일정인 경우
                                                   if (radioButtonStudy.isChecked()) {

                                                       schedule.setType(STUDY_SCHEDULE);
                                                       Intent intent = new Intent(AddScheduleActivity.this, StudyScheduleActivity.class);
                                                       intent.putExtra("studyScheduleData", schedule);
                                                       startActivity(intent);
                                                       // 업무 외 일정인 경우
                                                   } else if (radioButtonOther.isChecked()) {

                                                       schedule.setType(OTHER_SCHEDULE);
                                                       Intent intent = new Intent(AddScheduleActivity.this, OtherScheduleActivity.class);
                                                       intent.putExtra("otherScheduleData", schedule);
                                                       startActivity(intent);
                                                   } else {
                                                       AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
                                                       builder.setMessage("일정을 선택해 주십시오.");
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
                                           }

                                       }
        );

    }

    //라디오 버튼 선택하였을 시 실행되는 부분
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if (i == (R.id.radioButtonStudy)) {
                whichSchedule = STUDY_SCHEDULE;
                Toast.makeText(AddScheduleActivity.this, "업무 일정을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
            } else if (i == (R.id.radioButtonOther)) {
                whichSchedule = OTHER_SCHEDULE;
                Toast.makeText(AddScheduleActivity.this, "업무 외 일정을 선택하셨습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    };


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
                    //업로드 되는 이미지 경로
                    Uri photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }
        }
    }

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

    //
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        //이미지 이름
        String mImageCaptureName = timeStamp + ".png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/" + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();
        return storageDir;
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
        // ivImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        rotatedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, (resizedBitmap.getWidth()) / 4, (resizedBitmap.getHeight()) / 4, true);//resize(this,Uri.fromFile(new File(currentPhotoPath)),200);

        //tempFile = saveBitmapToJpeg(this, rotatedBitmap, "resizedCameraPhoto" + timeNow);//삭제 예정
        //

        tempFile = SaveBitmapToJPG(rotatedBitmap,currentPhotoPath,"");
        imageFilePath = tempFile.getAbsolutePath();
    }

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

    /*public static File saveBitmapToJpeg(Context context, Bitmap bitmap, String name, String filePath) {

        //File storage = context.getFilesDir(); // 이 부분이 임시파일 저장 경로

        String fileName = name + ".jpg";  // 파일이름은 마음대로!

        File tempFile = new File(filePath, fileName);

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
    }*/

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
                case PLACE_SELECT:
                    String selectedPlace =  data.getStringExtra("PlaceSelected");
                    place.setText(selectedPlace);
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
        //ivImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivImage.setImageBitmap((rotate(bitmap, exifDegree)));//이미지 뷰에 비트맵 넣기
        rotatedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = rotate(bitmap, exifDegree);
        resizedBitmap = Bitmap.createScaledBitmap(resizedBitmap, (resizedBitmap.getWidth()) / 4, (resizedBitmap.getHeight()) / 4, true);//resize(this,Uri.fromFile(new File(currentPhotoPath)),200);

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

    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {

        isCancel();
    }

    // 취소 확인용 다이얼로그 창
    private void isCancel() {
        // 확인용 다이얼로그 창
        AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
        builder.setMessage("취소하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //유저가 장소를 선택하였을 경우 실행되는 메소드
    public void onClickPlace(View view) {
        /*Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=startLat%20startLng&daddr=endLat%20endLng&hl=en");
        Intent it = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(it);*/
        Intent intent = new Intent(AddScheduleActivity.this, MapActivity.class);

        startActivityForResult(intent, PLACE_SELECT);
    }

}
