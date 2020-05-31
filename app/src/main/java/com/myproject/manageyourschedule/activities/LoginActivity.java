package com.myproject.manageyourschedule.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myproject.manageyourschedule.DTO.User;
import com.myproject.manageyourschedule.R;

import java.util.ArrayList;
import java.util.List;

import static com.myproject.manageyourschedule.Constant.Beginning;
import static com.myproject.manageyourschedule.Constant.MULTIPLE_PERMISSIONS;
import static com.myproject.manageyourschedule.Constant.entranceLoginActivity;

public class LoginActivity extends AppCompatActivity {

    //2020.03.21+(s)
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //logInEmail은 key로 필요하므로 static 변수로 만든다.
    public static TextInputEditText loginEmailTextInputEditText;
    //2020.03.21+(e)


    private TextInputEditText loginPasswordTextInputEditText;
    private Button loginBtn, signupBtn, findpasswordBtn;
    //private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "LoginActivity";

    //2020.04.07+(s)
    /*private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;*/
    //2020.04.07+(e)
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();

    //2020.04.14+(s)
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    public static StorageReference storageReference = firebaseStorage.getReference();
    //2020.04.14+(e)

    public static DatabaseReference refSwitch = database.getReference("SwitchInfo");
    public static DatabaseReference refUserId = database.getReference("UserId");
    public static ArrayList<String> arrayListUserId = new ArrayList<>();
    public static ArrayList<User> arrayListUser = new ArrayList<>();
    public static ArrayList<String> arrayListUserEmail = new ArrayList<>();
    public static ArrayList<String> arrayListUserPassword = new ArrayList<>();
    public static ArrayList<String> arrayListScheduleId = new ArrayList<>();
    public static ArrayList<String> arrayListUserIdTemporary = new ArrayList<>();
    public static int position = -1;
    private CheckBox checkBoxIdSave;
    //멀티 퍼미션 (권한) 설정 정보
    private String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    //public static User[] user = {new User()};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로그인 버튼
        loginBtn = findViewById(R.id.login_button);
        // 회원가입 버튼
        signupBtn = findViewById(R.id.signup_button);
        // 비밀번호 찾기 버튼
        findpasswordBtn = findViewById(R.id.findpassword_button);

        loginEmailTextInputEditText = findViewById(R.id.loginEmail);
        loginPasswordTextInputEditText = findViewById(R.id.loginPassword);
        checkBoxIdSave = findViewById(R.id.loginIdSave);


        checkPermissions();


        if (!checkPermissions()) {
            Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. ", Toast.LENGTH_SHORT).show();
            checkPermissions();
        }


        //2020.04.07+(s) 싱글톤 객체 생성
        //firebaseAuth = FirebaseAuth.getInstance();
        //2020.04.07+(e)
        /*// Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);*/
        //2020.03.21+(s)
        // SharedPreferences 객체 생성, MODE_PRIVATE: 이 앱에서만 사용, LoginIdSave 이라는 이름을 가진 SharedPreferences를 생성한다.
        preferences = getSharedPreferences("LoginIdSave", MODE_PRIVATE);
        editor = preferences.edit();
        //2020.03.21+(e)
        //2020.04.14+(s)
        getDataFromSharedPreference();

        checkBoxIdSave.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    // TODO : CheckBox is checked.
                    editor.putBoolean("isChecked", true);
                    editor.putString("editTextId", loginEmailTextInputEditText.getText().toString());
                    editor.apply();
                } else {
                    // TODO : CheckBox is unchecked.
                    editor.putBoolean("isChecked", false);
                    editor.putString("editTextId", "");
                    editor.apply();
                }
            }
        });

        getDataFromFirebase();


        //2020.04.14+(e)
        //만약 회원가입 되었다면 회원가입 액티비티에서 아이디와 비밀번호를 intent로 받아온다.
        String userEmail = getIntent().getStringExtra("userEmail");
        String userPassword = getIntent().getStringExtra("userPassword");
        if ((userEmail != null) & (userPassword != null)) {
            loginEmailTextInputEditText.setText(userEmail);
            loginPasswordTextInputEditText.setText(userPassword);
        }


        // 로그인 버튼 눌렀을 때
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((loginEmailTextInputEditText.getText().toString().equals("")) || (loginPasswordTextInputEditText.getText().toString().equals(""))) {
                    Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!(arrayListUserEmail.contains((loginEmailTextInputEditText.getText().toString())))) {
                    Toast.makeText(LoginActivity.this, "회원가입을 먼저 진행하여 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int position = -1;
                for (int i = 0; i < arrayListUserEmail.size(); i++) {
                    if (arrayListUserEmail.get(i).equals(loginEmailTextInputEditText.getText().toString())) {
                        position = i;
                    }
                }

                if ((arrayListUserEmail.get(position).equals(loginEmailTextInputEditText.getText().toString())) && (arrayListUserPassword.get(position).equals((loginPasswordTextInputEditText.getText().toString())))) {

                    Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginIntent);
                    // LoginActivity Destory
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                //2020.04.12+(e)


            }
        });

        // 회원가입 버튼 눌렀을 때
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent loginIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(loginIntent);

                // LoginActivity Destory
                //finish();

            }
        });

        // 비밀번호 찾기 버튼 눌렀을 때
        findpasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent findPasswordIntent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(findPasswordIntent);

                // LoginActivity Destory
                //finish();

            }
        });

        /*//로그인 인터페이스 리스너
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                //로그인
                }else{
                    //로그아웃
                }
           }
        };*/


    }

    private void getDataFromSharedPreference() {
        //String value = preferences.getString((loginEmailTextInputEditText.getText().toString()), null);
        String text = preferences.getString("editTextId", "");
        Boolean chk = preferences.getBoolean("isChecked", false);

        if (chk.equals(true)) {
            loginEmailTextInputEditText.setText(text);
            checkBoxIdSave.setChecked(true);
        }
    }

    private void getDataFromFirebase() {
        if (entranceLoginActivity == Beginning) {
            firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            arrayListUserId.add(document.getId());
                        }
                        Log.d(TAG, arrayListUserId.toString());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                    for (int i = 0; i < arrayListUserId.size(); i++) {
                        firebaseFirestore.collection("users")
                                .document(arrayListUserId.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        User user = task.getResult().toObject(User.class);
                                        if (user != null) {

                                            arrayListUser.add(user);
                                            arrayListUserEmail.add(user.getUserEmail());
                                            arrayListUserPassword.add(user.getUserPassword());
                                            arrayListUserIdTemporary.add(user.getUserId());
                                            Log.d(TAG, String.valueOf(arrayListUser));
                                            if (arrayListUserId.size() == arrayListUserIdTemporary.size()) {
                                                arrayListUserId.clear();
                                                arrayListUserId.addAll(arrayListUserIdTemporary);
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
        entranceLoginActivity++;
    }

    /*private void isLoginSuccess(){
        //로그인이 정상적으로 성공이 되었는지 안 되었는지 판단하는 메소드
        firebaseAuth.signInWithEmailAndPassword(loginEmailTextInputEditText.getText().toString(),loginPasswordTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //로그인이 실패하였을 때
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/


    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {
        isExit();
    }

    // 종료 확인 다이얼로그 창
    private void isExit() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    /*private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }*/

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String email = account.getEmail();
            String token = account.getIdToken();
            Log.d("Email:", email);
            Log.d("IdToken:", token);
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, pm)) {
                    //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
                } else {
                    //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
                }
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    //권한 요청 결과
    //동의 했을 경우와 거부 했을 경우를 앱의 정책에 따라 구현합니다.
    //ex ) 동의했을 경우 : 카메라 실행 , 거부 했을 경우 : 토스트로 동의 필요 알림
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (permissions[i].equals(this.permissions[i])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                                showToast_PermissionDeny();

                            }
                        }
                    }
                } else {

                    showToast_PermissionDeny();
                }
                return;
            }
        }

    }


    private void showToast_PermissionDeny() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. ", Toast.LENGTH_SHORT).show();
        finish();
    }
}


