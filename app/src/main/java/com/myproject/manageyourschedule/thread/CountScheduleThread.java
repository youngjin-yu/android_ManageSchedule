package com.myproject.manageyourschedule.thread;


import com.myproject.manageyourschedule.DTO.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import static com.myproject.manageyourschedule.Constant.countOtherSchedule;
import static com.myproject.manageyourschedule.Constant.countStudySchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListOtherSchedule;
import static com.myproject.manageyourschedule.activities.MainActivity.arrayListSchedule;

public class CountScheduleThread implements Runnable {


    @Override
    public void run() {
        countStudySchedule = 0;
        countOtherSchedule = 0;
        Iterator<Schedule> iterator = arrayListSchedule.iterator();
        while (iterator.hasNext()) {
            StringBuffer stringBuffer = new StringBuffer();
            Schedule schedule = iterator.next();
            String yearOfSchedule = String.format("%04d", schedule.getYear());
            String monthOfSchedule = String.format("%02d", schedule.getMonth());
            String dayOfSchedule = String.format("%02d", schedule.getDayOfMonth());
            String hourOfSchedule = String.format("%02d", schedule.getHourOfStart());
            String minuteOfSchedule = String.format("%02d", schedule.getMinuteOfStart());
            String secondOfSchedule = "00";
            stringBuffer.append(yearOfSchedule).append("-").append(monthOfSchedule).append("-").append(dayOfSchedule).append(" ")
                    .append(hourOfSchedule).append(":").append(minuteOfSchedule).append(":").append(secondOfSchedule);

            Date dateOfSchedule = null;
            Date dateNow = null;
            Date currentTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dateNow = simpleDateFormat.parse(simpleDateFormat.format(currentTime));
                dateOfSchedule = simpleDateFormat.parse(String.valueOf(stringBuffer));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dateOfSchedule.compareTo(dateNow) >= 0) {
                countStudySchedule++;
            }
        }


        Iterator<Schedule> iteratorOtherSchedule = arrayListOtherSchedule.iterator();
        while (iteratorOtherSchedule.hasNext()) {
            StringBuffer stringBuffer = new StringBuffer();
            Schedule schedule = iteratorOtherSchedule.next();
            String yearOfSchedule = String.format("%04d", schedule.getYear());
            String monthOfSchedule = String.format("%02d", schedule.getMonth());
            String dayOfSchedule = String.format("%02d", schedule.getDayOfMonth());
            String hourOfSchedule = String.format("%02d", schedule.getHourOfStart());
            String minuteOfSchedule = String.format("%02d", schedule.getMinuteOfStart());
            String secondOfSchedule = "00";
            stringBuffer.append(yearOfSchedule).append("-").append(monthOfSchedule).append("-").append(dayOfSchedule).append(" ")
                    .append(hourOfSchedule).append(":").append(minuteOfSchedule).append(":").append(secondOfSchedule);

            Date dateOfSchedule = null;
            Date dateNow = null;
            Date currentTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                dateNow = simpleDateFormat.parse(simpleDateFormat.format(currentTime));
                dateOfSchedule = simpleDateFormat.parse(String.valueOf(stringBuffer));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (dateOfSchedule.compareTo(dateNow) >= 0) {
                countOtherSchedule++;
            }
        }
    }
}

