package com.myproject.manageyourschedule.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;

import java.io.File;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<Schedule> mySchedule;

    /*//listView에서 유저가 선택한 라디오 버튼의 위치
    public static int listViewRadioBtnPosition = -1;*/

    public ListViewAdapter(ArrayList<Schedule> mySchedule) {
        this.mySchedule = mySchedule;
    }

    //아이템의 개수를 표시
    @Override
    public int getCount() {
        return mySchedule.size();
    }

    //몇번째 어떤 아이템이 있는지
    @Override
    public Object getItem(int position) {
        return mySchedule.get(position);
    }

    //position을 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Context context = parent.getContext();
        //view holder 패턴을 적용
        if (convertView == null) {
            holder = new ViewHolder();
            //convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_schedule, parent, false);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_study_schedule, parent, false);

            ImageView uploadImage = convertView.findViewById(R.id.uploadImage);
            TextView titleText = convertView.findViewById(R.id.titleText);
            TextView contentText = convertView.findViewById(R.id.contentText);

            TextView yearStudySchedule = convertView.findViewById(R.id.yearStudySchedule);
            TextView monthStudySchedule = convertView.findViewById(R.id.monthStudySchedule);
            TextView dayOfMonthStudySchedule = convertView.findViewById(R.id.dayOfMonthStudySchedule);
            TextView hourOfStartStudySchedule = convertView.findViewById(R.id.hourOfStartStudySchedule);
            TextView minuteOfStartStudySchedule = convertView.findViewById(R.id.minuteOfStartStudySchedule);
            TextView hourOfEndStudySchedule = convertView.findViewById(R.id.hourOfEndStudySchedule);
            TextView minuteOfEndStudySchedule = convertView.findViewById(R.id.minuteOfEndStudySchedule);
            RatingBar ratingBarStudySchedule = convertView.findViewById(R.id.satisfactionStudySchedule);

            holder.contentText = contentText;
            holder.titleText = titleText;
            holder.uploadImage = uploadImage;

            holder.yearStudySchedule = yearStudySchedule;
            holder.monthStudySchedule = monthStudySchedule;
            holder.dayOfMonthStudySchedule = dayOfMonthStudySchedule;
            holder.hourOfStartStudySchedule = hourOfStartStudySchedule;
            holder.minuteOfStartStudySchedule = minuteOfStartStudySchedule;
            holder.hourOfEndStudySchedule = hourOfEndStudySchedule;
            holder.minuteOfEndStudySchedule = minuteOfEndStudySchedule;
            holder.ratingBarStudySchedule = ratingBarStudySchedule;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Schedule schedule = mySchedule.get(position);
        holder.uploadImage.setImageResource(R.drawable.ic_calendar);
        if ((schedule != null)) {
            holder.titleText.setText(schedule.getTitle());
            holder.contentText.setText(schedule.getContent());

            holder.yearStudySchedule.setText(String.valueOf(schedule.getYear()));
            holder.monthStudySchedule.setText(String.valueOf(schedule.getMonth()));
            holder.dayOfMonthStudySchedule.setText(String.valueOf(schedule.getDayOfMonth()));
            holder.hourOfStartStudySchedule.setText(String.valueOf(schedule.getHourOfStart()));
            holder.minuteOfStartStudySchedule.setText(String.valueOf(schedule.getMinuteOfStart()));
            holder.hourOfEndStudySchedule.setText(String.valueOf(schedule.getHourOfEnd()));
            holder.minuteOfEndStudySchedule.setText(String.valueOf(schedule.getMinuteOfEnd()));
            holder.ratingBarStudySchedule.setRating(schedule.getSatisfaction());


            //2020.03.23 수정
            if (((schedule.getImageFilePath()) != null)) {
                File file = new File(schedule.getImageFilePath());
                //파일 경로에 실제로 파일이 존재 한다면, 그 일정에 해당하는 사진파일을 불러들이고
                if(file.exists() && (!file.isDirectory())){
                    Bitmap myBitmap = BitmapFactory.decodeFile((schedule.getImageFilePath()));
                    //holder.uploadImage = (convertView).findViewById(R.id.uploadImage);
                    holder.uploadImage.setImageBitmap(myBitmap);
                }else{
                    //파일 경로에 실제로 파일이 존재 하지 않는다면, 그 일정에는 default 달력 사진을 넣는다.
                    holder.uploadImage.setImageResource(R.drawable.ic_calendar);
                }
            }
        }
        return convertView;
    }


    static class ViewHolder {
        ImageView uploadImage;
        TextView titleText;
        TextView contentText;

        TextView yearStudySchedule;
        TextView monthStudySchedule;
        TextView dayOfMonthStudySchedule;
        TextView hourOfStartStudySchedule;
        TextView minuteOfStartStudySchedule;
        TextView hourOfEndStudySchedule;
        TextView minuteOfEndStudySchedule;
        RatingBar ratingBarStudySchedule;
    }
}
