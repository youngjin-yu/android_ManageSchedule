package com.myproject.manageyourschedule.thread;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import static com.myproject.manageyourschedule.activities.FindPasswordActivity.flag;
import static com.myproject.manageyourschedule.activities.FindPasswordActivity.limitNumber;

//로딩스레드
public class CountdownThread implements Runnable {

    private Handler handler;
    private String TAG = "SendEmailThread";

    public CountdownThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        //flag를 이용한 스레드 종료
        while (flag.equals(true) && limitNumber > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            limitNumber--;
            /**
             * sendEmptyMessage은 단순한 int형 What을 전달하기 때문에
             * Message객체의 생성이 필요가 없습니다
             */
            handler.sendEmptyMessage(1);

            /**
             * sendMessage는 message객체를 넘겨주며,
             * 이때 what의 값, arg1, arg2등 int형 값을 줄수도 있고
             * intent등의 객체 전체를 넘길수도 있다 (message.obj = 겍체)
             */
            Message message = Message.obtain();
            message.what = 2;
            handler.sendMessage(message);
            Log.d(TAG, "CountdownThread is not stopped...");
        }
        Log.d(TAG, "CountdownThread is stopped...");
    }
}
