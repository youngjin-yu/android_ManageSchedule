package com.myproject.manageyourschedule.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.thread.CountdownThread;
import com.myproject.manageyourschedule.thread.SendEmailThread;
import com.myproject.manageyourschedule.util.GMailSender;

import java.lang.ref.WeakReference;

import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserEmail;

public class FindPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, codeEditText;
    private Button sendCodeBtn, okBtn;
    private TextInputLayout codeLayout, emailLayout;
    private TextView limitedTimeTextView;
    // 이메일 인증 타이머
    private CountDownTimer countDownTimer;
    // 이메일 보내기
    public GMailSender gMailSender;
    // 이메일 인증 코드
    public static String code;
    public static int limitNumber;
    Handler handler;
    public static Boolean flag = true;
    Thread countdownThread;
    private String TAG = "FindPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);

        emailLayout = findViewById(R.id.emailInputLayout);
        emailEditText = findViewById(R.id.emailText);
        codeEditText = findViewById(R.id.inputCodeEditText);
        sendCodeBtn = findViewById(R.id.sendCodeButton);
        okBtn = findViewById(R.id.okButton);
        codeLayout = findViewById(R.id.inputCodeLayout);
        limitedTimeTextView = findViewById(R.id.timeTextView);


        //인증코드 확인 버튼은 처음에 비활성화
        okBtn.setEnabled(false);
        okBtn.setTextColor(Color.parseColor("#C0C0C0")); //회색

        //인터넷 사용을 위한 인증
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 하지 않았다면 비밀번호 찾는 것이 불가능 하므로
                if (!(arrayListUserEmail.contains((emailEditText.getText()).toString()))) {
                    emailLayout.setError("아직 회원가입을 하지 않으셨습니다.");
                    return;
                }
                //이메일 형식인지 체크하여 이메일 형식이 아니면 빠져나온다.
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
                    codeLayout.setError("이메일 형식으로 입력해주세요");
                    return;
                } else {
                    codeLayout.setError(null);
                    //인증코드 메일을 보낸다.
                    String receiverEmail = (emailEditText.getText()).toString();
                    //sendEmail(receiverEmail,limitedTimeTextView, okBtn);
                    sendEmail(receiverEmail);
                    // 인증번호 확인 버튼 활성화
                    okBtn.setEnabled(true);
                    okBtn.setTextColor(Color.parseColor("#000000"));
                    Log.v("이메일 인증", "타이머 시작");
                }
            }
        });

        //인증코드 체크 버튼 클릭
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력한 코드와 보낸 인증 코드 같으면
                if (codeEditText.getText().toString().equals(code)) {

                    // 타이머 종료!
                    //countDownTimer.cancel();
                    //flag를 이용한 스레드 종료
                    flag = false;
                    // "인증 성공" 글자 출력
                    limitedTimeTextView.setText("인증 성공");

                    // 인증 코드 보내기 비활성화
                    sendCodeBtn.setEnabled(false);
                    sendCodeBtn.setTextColor(Color.parseColor("#C0C0C0")); // 회색

                    // 확인 버튼 비활성화
                    okBtn.setEnabled(false);
                    okBtn.setTextColor(Color.parseColor("#C0C0C0")); //회색


                    Intent intent = new Intent(FindPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("userEmail", (emailEditText.getText()).toString());
                    startActivity(intent);


                } else {
                    Toast.makeText(getApplicationContext(), "인증코드 일치 하지 않습니다! 다시 한번 확인해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendEmail(final String receiverEmail) {

        String email = receiverEmail;

        sendCodeBtn.setEnabled(false);
        sendCodeBtn.setTextColor(Color.parseColor("#C0C0C0"));

        flag = true;
        limitNumber = 180;

        Runnable sendEmailRunnable = new SendEmailThread(gMailSender, email);
        Thread sendEmailThread = new Thread(sendEmailRunnable);
        sendEmailThread.start();

        handler = new MyHandler(this);

        Runnable countdownRunnable = new CountdownThread(handler);
        countdownThread = new Thread(countdownRunnable);
        countdownThread.start();

    }

    private static class MyHandler extends Handler {
        private final WeakReference<FindPasswordActivity> mActivity;

        public MyHandler(FindPasswordActivity activity) {
            mActivity = new WeakReference<FindPasswordActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final FindPasswordActivity activity = mActivity.get();
            if (activity != null) {
                /**
                 * 넘겨받은 what값을 이용해 실행할 작업을 분류합니다
                 */
                if (msg.what == 1) {
                    Log.d("What Number : ", "What is 1");
                } else if (msg.what == 2) {
                    Log.d("What Number : ", "What is 2");
                    //제한시간 3분으로 설정
                    int emailAuthCount = limitNumber;

                    if (emailAuthCount != 0) {
                        if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                            activity.limitedTimeTextView.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                        } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                            activity.limitedTimeTextView.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                        }
                    }
                    //emailAuthCount == 0 인 경우
                    else {
                        // 인증 확인 버튼 비활성화
                        activity.okBtn.setEnabled(false);
                        activity.okBtn.setTextColor(Color.parseColor("#C0C0C0"));
                    }
                }
            }
        }
    }
}
