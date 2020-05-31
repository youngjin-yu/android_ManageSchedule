package com.myproject.manageyourschedule.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.adapters.RecyclerViewByDateAdapter;

import static com.myproject.manageyourschedule.activities.MainActivity.arrayListAllSchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.recyclerViewByDateAdapter;

public class ViewByDateActivity extends AppCompatActivity {

    private String tag = "ViewByDateActivity";
    private Boolean isOkayClicked = null;
    // 리사이클러뷰
    RecyclerView recyclerViewByDate = null;
    private int yearOfSchedule;
    private int monthOfSchedule;
    private int dayOfMonthOfSchedule;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbydate);
        recyclerViewByDate = findViewById(R.id.recyclerViewByDate);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewByDate.setLayoutManager(layoutManager);

        recyclerViewByDate.setAdapter(recyclerViewByDateAdapter);


        setItemClickListener();
        // LoginActivity Destory
        //finish();

        //setItemClickListener();
    }

    //리싸이클러뷰 아이템 클릭 리스너 -> 일정 상세 페이지로 이동
    private void setItemClickListener() {

        recyclerViewByDateAdapter.setOnItemClickListener(new RecyclerViewByDateAdapter.OnSelectedItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Schedule selectedSchedule = arrayListAllSchedule.get(position);

                Intent intent = new Intent(ViewByDateActivity.this, ViewDetailsActivity.class);
                intent.putExtra("CorrespondingSchedule", selectedSchedule);
                intent.putExtra("itemPosition", position);
                startActivity(intent);

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(tag, "onResume 입니다.");

    }





    //뒤로가기 눌렀을 경우
    @Override
    public void onBackPressed() {
        toMain();
    }

    private void toMain() {

        Intent intent = new Intent(ViewByDateActivity.this, MainActivity.class);
        startActivity(intent);
        //2020.04.01+ activity가 종료 될 수 있도록 finish()를 호출한다.
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //arrayListAllSchedule 리스트 모두 삭제
        arrayListAllSchedule.clear();

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                recyclerViewByDateAdapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
        Log.d(tag, "onDestroy 입니다.");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(tag, "onStop 입니다.");
    }

}
