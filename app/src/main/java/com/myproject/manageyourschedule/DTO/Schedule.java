package com.myproject.manageyourschedule.DTO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;


@IgnoreExtraProperties
public class Schedule implements Serializable {

    private String id;      //일정의 id
    private int type;       //일정의 종류
    private String title;   //일정 제목
    private int year;       //일정의 연도
    private int month;      //일정의 월
    private int dayOfMonth; //일정의 일
    private int hourOfStart;//일정 시작 시간
    private int minuteOfStart;//일정 시작 분
    private int hourOfEnd;  //일정 종료 시간
    private int minuteOfEnd;//일정 종료 분
    private String place;   //일정 장소
    private String content; //일정 내용
    private String imageFilePath;//일정 이미지 파일 경로
    private float satisfaction;//목표 대비 수행 비율
    private String timeKey;  //일정의 time에 대한 key
    private String userEmail;//어떤 유저의 일정인지에 관한 정보



    public Schedule(String id, int type, String title, int year, int month, int dayOfMonth, int hourOfStart, int minuteOfStart, int hourOfEnd, int minuteOfEnd, String place, String content, String imageFilePath, float satisfaction, String timeKey, String userEmail) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hourOfStart = hourOfStart;
        this.minuteOfStart = minuteOfStart;
        this.hourOfEnd = hourOfEnd;
        this.minuteOfEnd = minuteOfEnd;
        this.place = place;

        this.content = content;
        this.imageFilePath = imageFilePath;
        this.satisfaction = satisfaction;

        this.timeKey = timeKey;
        this.userEmail = userEmail;

    }

    public Schedule() {

    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getHourOfStart() {
        return hourOfStart;
    }

    public void setHourOfStart(int hourOfStart) {
        this.hourOfStart = hourOfStart;
    }

    public int getMinuteOfStart() {
        return minuteOfStart;
    }

    public void setMinuteOfStart(int minuteOfStart) {
        this.minuteOfStart = minuteOfStart;
    }

    public int getHourOfEnd() {
        return hourOfEnd;
    }

    public void setHourOfEnd(int hourOfEnd) {
        this.hourOfEnd = hourOfEnd;
    }

    public int getMinuteOfEnd() {
        return minuteOfEnd;
    }

    public void setMinuteOfEnd(int minuteOfEnd) {
        this.minuteOfEnd = minuteOfEnd;
    }


    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public float getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(float satisfaction) {
        this.satisfaction = satisfaction;
    }


    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }


    @Override
    public String toString() {
        return "Schedule{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", dayOfMonth=" + dayOfMonth +
                ", hourOfStart=" + hourOfStart +
                ", minuteOfStart=" + minuteOfStart +
                ", hourOfEnd=" + hourOfEnd +
                ", minuteOfEnd=" + minuteOfEnd +
                ", place='" + place + '\'' +
                ", content='" + content + '\'' +
                ", imageFilePath='" + imageFilePath + '\'' +
                ", satisfaction=" + satisfaction +
                ", timeKey='" + timeKey + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
