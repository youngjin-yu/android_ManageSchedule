package com.myproject.manageyourschedule;

//상수만 있는 클래스
public class Constant {

    public static final int RC_SIGN_IN = 1;

    public static final int STUDY_SCHEDULE_UPDATE = 101;    //수정하는 부분 REQUEST CODE

    public static final int OTHER_SCHEDULE_UPDATE = 102;

    public static final int STUDY_SCHEDULE_ADD = 103;

    public static final int OTHER_SCHEDULE_ADD = 104;

    public static final int SHOW_ALL_SCHEDULE = 105;

    public static final int STUDY_SCHEDULE = 0;    //업무 일정

    public static final int OTHER_SCHEDULE = 1;    //업무 외 일정

    public static final int PICK_FROM_ALBUM = 1;   // 앨범

    public static final int PICK_FROM_CAMERA = 2;  // 카메라

    public static final int CAMERA_CODE = 1111;

    public static final int GALLERY_CODE = 1112;

    public static final int MULTIPLE_PERMISSIONS = 6666;

    public static final int Beginning = 0;
    //노티피케이션 채널 id
    public static final String CHANNEL_ID = "notifyServiceChannel";
    //노티피케이션 채널 id
    public static final String CHANNEL_ID_START_SCHEDULE = "notifyStartScheduleServiceChannel";
    //노티피케이션 채널 id
    public static final String CHANNEL_ID_END_SCHEDULE = "notifyEndScheduleServiceChannel";
    //노티피케이션 id
    public static final int NOTIFICATION_ID = 1234;
    //노티피케이션 count id
    public static final int NOTIFICATION_COUNT_ID = 4321;
    //일정 시작 노티피케이션
    public static final int NOTIFICATION_START_SCHEDULE_ID=7777;
    //일정 종료 노티피케이션
    public static final int NOTIFICATION_END_SCHEDULE_ID=8888;
    //노티피케이션 already done schedule notify id : 이미 수행한 일정을 알려주는 노티피케이션
    public static final int NOTIFICATION_ALREADY_DONE_NOTIFY_ID = 9999;

    public static String notifyTitle = "알림 허용";

    public static String notifyDescription = "일정 알림을 허용하였습니다.";

    //StudyScheduleActivity를 몇 번 들어갔는지를 저장하는 변수
    public static int entranceStudySchedule = 0;
    //OtherScheduleActivity를 몇 번 들어갔는지를 저장하는 변수
    public static int entranceOtherSchedule = 0;
    //MainActivity를 몇 번 들어갔는지를 저장하는 변수
    public static int entranceMainActivity = 0;
    //LoginActivity를 몇 번 들어갔는지를 저장하는 변수
    public static int entranceLoginActivity = 0;
    //공부 일정 개수 count 변수 : 시간을 비교하여 현재시간 이후인 공부 일정만 count 한다.
    public static int countStudySchedule = 0;
    //다른 일정 개수 count 변수 : 시간을 비교하여 현재시간 이후인 다른 일정만 count 한다.
    public static int countOtherSchedule = 0;

    //나만의 브로드캐스트 리시버 만들기
    //Notify만 하는 action
    public static final String MY_ACTION = "com.myproject.manageyourschedule.action.ACTION_NOTIFY";
    //일정을 세서 Notify 하는 action
    public static final String COUNT_ACTION = "com.myproject.manageyourschedule.action.ACTION_COUNTNOTIFY";
    //일정 시작 알림
    public static final String MY_SCHEDULE_START = "com.myproject.manageyourschedule.action.ACTION_NOTIFYSCHEDULESTART";
    //일정 종료 알림
    public static final String MY_SCHEDULE_END = "com.myproject.manageyourschedule.action.ACTION_NOTIFYSCHEDULEEND";
    //서비스를 실행 시킬지 말지를 결정하는 변수
    public static Boolean isServiceRun = false;

    public static final int PLACE_SELECT = 1115;

}
