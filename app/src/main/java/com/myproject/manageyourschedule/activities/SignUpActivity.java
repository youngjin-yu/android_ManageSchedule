package com.myproject.manageyourschedule.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.myproject.manageyourschedule.DTO.User;
import com.myproject.manageyourschedule.R;

import java.util.UUID;

import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUser;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserEmail;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserId;
import static com.myproject.manageyourschedule.activities.LoginActivity.arrayListUserPassword;
import static com.myproject.manageyourschedule.activities.LoginActivity.firebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpButton;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private EditText userNameText;
    private EditText signUpUserDateOfBirth;

    private TextInputLayout emailTextInputLayout, passwordTextInputLayout, confirmPasswordTextInputLayout;
    private TextInputLayout signUpUserDateOfBirthLayout;
    //2020.03.21+(s)
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //2020.03.21+(e)

    //2020.04.07+(s)
    private String TAG = "SignUpActivity";

    //2020.04.07+(e)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userNameText = findViewById(R.id.signUpUserName);
        signUpUserDateOfBirth = findViewById(R.id.signUpUserDateOfBirth);
        emailEditText = findViewById(R.id.signUpEmail);
        passwordEditText = findViewById(R.id.signUpPassword);
        confirmPasswordEditText = findViewById(R.id.confirmSignUpPassword);

        emailTextInputLayout = findViewById(R.id.signUpEmailLayout);
        passwordTextInputLayout = findViewById(R.id.signUpPasswordLayout);
        confirmPasswordTextInputLayout = findViewById(R.id.confirmSignUpPasswordLayout);
        signUpUserDateOfBirthLayout = findViewById(R.id.signUpUserDateOfBirthLayout);

        signUpButton = findViewById(R.id.signUpBtn);

        //2020.04.07+(s) 싱글톤 객체 생성

        //2020.04.07+(e)

        //생년월일 유효성 체크
        signUpUserDateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 8) {
                    signUpUserDateOfBirthLayout.setError("생년월일은 숫자 8자리로 설정해주세요");
                } else {
                    signUpUserDateOfBirthLayout.setError(null);
                }
            }
        });

        //이메일 유효성 체크
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
                    emailTextInputLayout.setError("이메일 형식으로 입력해주세요");
                } else {
                    emailTextInputLayout.setError(null);
                }
            }
        });

        //비밀번호는 8자리 이상으로 설정

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 8) {
                    passwordTextInputLayout.setError("비밀번호는 8자리 이상으로 설정해주세요");
                } else {
                    passwordTextInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //비밀번호 재확인
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(passwordEditText.getText().toString())) {
                    confirmPasswordTextInputLayout.setError("비밀번호가 일치 하지 않습니다!");
                } else {
                    confirmPasswordTextInputLayout.setError(null);

                }
            }
        });

        //회원가입 버튼 눌렀을 때
        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //유저 정보를 아무것도 입력하지 않았으면 회원가입 안되도록 한다.
                if ((userNameText.getText().toString() == "") || (signUpUserDateOfBirth.getText().toString() == "") || (emailEditText.getText().toString() == "") || (passwordEditText.getText().toString() == "") || (confirmPasswordEditText.getText().toString() == "")) {
                    return;
                }

                //2020.04.14+(s) 이미 회원가입한 상태라면 회원가입 안되게 한다.
                if (arrayListUserEmail.contains(emailEditText.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "이미 회원가입 하셨습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //2020.04.14+(e)
                //2020.03.21+(s)

                /*preferences = getSharedPreferences("Login", MODE_PRIVATE);
                editor = preferences.edit();
                if (preferences.contains((emailEditText.getText().toString()))) {
                    Toast.makeText(SignUpActivity.this, "이미 회원가입 하셨습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }*/

                /*if ((signUpHashMap.containsKey((emailEditText.getText().toString())))) {
                    Toast.makeText(SignUpActivity.this, "이미 회원가입 하셨습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }*/


                // 비밀번호와 비밀번호 확인이 일치하는 경우
                if (((confirmPasswordEditText.getText()).toString()).equals(passwordEditText.getText().toString())) {
                    //signUpHashMap.put(((emailEditText.getText()).toString()), ((passwordEditText.getText()).toString()));
                    //2020.03.21+(s)
                    /*String value = (userNameText.getText().toString()) + "★" + (signUpUserDateOfBirth.getText().toString()) + "★" + ((passwordEditText.getText()).toString());
                    editor.putString(((emailEditText.getText()).toString()), value);
                    editor.apply();*/
                    //UUID: 범용고유 식별자
                    String userId = UUID.randomUUID().toString();
                    User user = new User();
                    user.setUserName(userNameText.getText().toString());
                    user.setDateOfBirth(signUpUserDateOfBirth.getText().toString());
                    user.setUserEmail(emailEditText.getText().toString());
                    user.setUserId(userId);
                    user.setUserPassword(passwordEditText.getText().toString());
                    //"UserId"라는 키가 있으면 하부에 또다른 키인 push()를 함으로써 생성되는 고유 키값과 밸류는 UUID를 사용해서 생성되는 값을 설정한다.
                    //"UserId"라는 키에 밸류는 push()를 함으로써 생성되는 고유 키값과 UUID를 사용한 값을 밸류로 하는 쌍을 밸류로 넣는다.
                    /* del 2020.04.14 firestore로 변경하므로써 삭제함
                    DatabaseReference pushUserId = refUserId.push();
                    String key = pushUserId.getKey();
                    user.setIdKey(key);
                    database.getReference(userId).setValue(user);
                    pushUserId.setValue(user.getUserId());*/
                    //UUID를 사용하므로 써 생성되는 랜덤 값을 키로 하고 user라는 클래스 자체를 밸류로 하여 파이어베이스에 넣는다.

                    // 2020.04.12+(s)
                    arrayListUserId.add(userId);
                    arrayListUser.add(user);
                    arrayListUserEmail.add(user.getUserEmail());
                    arrayListUserPassword.add(user.getUserPassword());
                    firebaseFirestore.collection("users").document(userId)
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


                    // 2020.04.12+(e)

                    Toast.makeText(SignUpActivity.this, "회원가입이 되었습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    intent.putExtra("userEmail", ((emailEditText.getText()).toString()));
                    intent.putExtra("userPassword", ((passwordEditText.getText()).toString()));
                    startActivity(intent);
                    //2020.03.21+(e)
                } else {
                    Toast.makeText(SignUpActivity.this, "비밀번호를 확인해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //2020.03.21+(e)
    }


}
