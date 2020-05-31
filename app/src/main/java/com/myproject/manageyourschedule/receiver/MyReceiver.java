package com.myproject.manageyourschedule.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.myproject.manageyourschedule.Constant.COUNT_ACTION;
import static com.myproject.manageyourschedule.Constant.MY_ACTION;
import static com.myproject.manageyourschedule.Constant.MY_SCHEDULE_END;
import static com.myproject.manageyourschedule.Constant.MY_SCHEDULE_START;


// 안드로이드 스튜디오 오레오 버전 이후부터 변경된 점
// 앱은 더 이상 명시적 브로드캐스트를 제외한 리시버를 AndroidManifest.xml에 등록할 수 없습니다.
// 암시적 브로드캐스트 리시버는 런타임에 Context.registerReceiver()를 통해서만 등록이 가능합니다.
// 단, 서명 권한이 요구되는 브로드캐스트는 암시적 브로드캐스트 제한에서 제외됩니다.
// 이는 동일한 인증서로 서명된 앱으로만 브로드캐스트가 전송되기 때문입니다.
public class MyReceiver extends BroadcastReceiver {


    // TODO: 브로드캐스트 리시버
    @Override
    public void onReceive(Context context, Intent intent) {

        //
        if (intent.getAction().equals(MY_ACTION)) {
            //Toast.makeText(context, "일정 알림을 허용하였습니다.", Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals(COUNT_ACTION)) {
            //Toast.makeText(context, "일정 진행 상황을 알려드립니다.", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "알림이 도착하였습니다.", Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals(MY_SCHEDULE_START)) {
            //Toast.makeText(context, "일정 시작 알림이 도착하였습니다.", Toast.LENGTH_SHORT).show();
        }
        if (intent.getAction().equals(MY_SCHEDULE_END)) {
            //Toast.makeText(context, "일정 종료 알림이 도착하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
