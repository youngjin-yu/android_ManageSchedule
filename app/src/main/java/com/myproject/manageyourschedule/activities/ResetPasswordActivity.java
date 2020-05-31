package com.myproject.manageyourschedule.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.myproject.manageyourschedule.DTO.User;
import com.myproject.manageyourschedule.R;

import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUser;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserEmail;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserId;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserPassword;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText changePasswordEditText;
    private TextInputEditText confirmChangePasswordEditText;
    private TextInputLayout changePasswordLayout;
    private TextInputLayout confirmChangePasswordLayout;
    private String userEmail;
    private Button resetPasswordOkBtn;
    private String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //비밀번호 재설정을 실시
        changePasswordEditText = findViewById(R.id.changePassword);
        confirmChangePasswordEditText = findViewById(R.id.confirmChangePassword);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        confirmChangePasswordLayout = findViewById(R.id.confirmChangePasswordLayout);
        resetPasswordOkBtn = findViewById(R.id.resetPasswordOkButton);

        //버튼 비활성화
        resetPasswordOkBtn.setEnabled(false);
        resetPasswordOkBtn.setTextColor(Color.parseColor("#C0C0C0")); //회색

        //FindPasswordActivity에서 전달받은 이메일 데이터
        Intent intent = getIntent();
        userEmail = intent.getExtras().getString("userEmail");

        //비밀번호는 8자리 이상으로 설정
        changePasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 8) {
                    changePasswordLayout.setError("비밀번호는 8자리 이상으로 설정해주세요");
                } else {
                    changePasswordLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //비밀번호 재확인
        confirmChangePasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(changePasswordEditText.getText().toString())) {
                    confirmChangePasswordLayout.setError("비밀번호가 일치 하지 않습니다!");

                } else {
                    resetPasswordOkBtn.setEnabled(true);
                    resetPasswordOkBtn.setTextColor(Color.parseColor("#000000"));
                    confirmChangePasswordLayout.setError(null);
                }
            }
        });
        //dialog.dismiss();


        resetPasswordOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비밀번호 변경이 완료된 경우
                //비밀번호와 비밀번호 확인이 같은 경우만
                if ((changePasswordEditText.getText().toString()).equals(confirmChangePasswordEditText.getText().toString())) {
                    int position = -1;
                    for(int i =0; i<arrayListUserEmail.size(); i++){
                        if(arrayListUserEmail.get(i).equals(userEmail)){
                            position = i;
                        }
                    }
                    arrayListUserPassword.set((position),(changePasswordEditText.getText().toString()));
                    User user = arrayListUser.get(position);
                    user.setUserPassword((changePasswordEditText.getText().toString()));
                    arrayListUser.set(position,user);

                    firebaseFirestore.collection("users").document(arrayListUserId.get(position))
                            .set(user)
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

                    Toast.makeText(getApplicationContext(), "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    //로그인 화면으로 이동
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "비밀번호와 비밀번호 확인이 같지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
        builder.setMessage("로그인 화면으로 이동하시겠습니까??");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
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


}
