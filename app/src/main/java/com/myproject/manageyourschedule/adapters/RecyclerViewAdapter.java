package com.myproject.manageyourschedule.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.manageyourschedule.DTO.Schedule;
import com.myproject.manageyourschedule.R;

import java.io.File;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Schedule> myScheduleArrayList;

    /*//RecyclerView에서 유저가 선택한 라디오 버튼의 위치
    public static int recyclerViewRadioBtnPosition = -1;*/

    //private int lastSelectedPosition = -1;

    //아이템 클릭 리스너 변수
    private OnItemClickListener onItemClickListener;

    //아이템 클릭 인터페이스
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    //아이템 클릭 리스너 설정
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //생성자에서 데이터 리스트 객체를 전달받는다.
    public RecyclerViewAdapter(ArrayList<Schedule> myScheduleList) {
        this.myScheduleArrayList = myScheduleList;
    }


    //item view를 위한 ViewHolder 객체를 생성하여 return
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //item 1개 view
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_schedule, parent, false);
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_other_schedule, parent, false);
        RecyclerViewAdapter.ViewHolder viewHolder = new RecyclerViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    //ArrayList에서 position에 해당하는 데이터를 ViewHolder의 item view에 표시
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {

        Schedule schedule = myScheduleArrayList.get(position);
        holder.uploadImage.setImageResource(R.drawable.ic_calendar);
        if ((schedule != null)) {
            holder.title.setText(schedule.getTitle());
            holder.content.setText(schedule.getContent());

            holder.yearOtherSchedule.setText(String.valueOf(schedule.getYear()));
            holder.monthOtherSchedule.setText(String.valueOf(schedule.getMonth()));
            holder.dayOfMonthOtherSchedule.setText(String.valueOf(schedule.getDayOfMonth()));
            holder.hourOfStartOtherSchedule.setText(String.valueOf(schedule.getHourOfStart()));
            holder.minuteOfStartOtherSchedule.setText(String.valueOf(schedule.getMinuteOfStart()));
            holder.hourOfEndOtherSchedule.setText(String.valueOf(schedule.getHourOfEnd()));
            holder.minuteOfEndOtherSchedule.setText(String.valueOf(schedule.getMinuteOfEnd()));
            holder.ratingBarOtherSchedule.setRating(schedule.getSatisfaction());

            //holder.radioButton.setChecked(lastSelectedPosition == position);


            //2020.03.23 수정
            if (((schedule.getImageFilePath()) != null)) {
                File file = new File(schedule.getImageFilePath());
                //파일 경로에 실제로 파일이 존재 한다면, 그 일정에 해당하는 사진파일을 불러들이고
                if(file.exists() && (!file.isDirectory())){
                    Bitmap myBitmap = BitmapFactory.decodeFile((schedule.getImageFilePath()));
                    holder.uploadImage.setImageBitmap(myBitmap);
                }else{
                    //파일 경로에 실제로 파일이 존재 하지 않는다면, 그 일정에는 default 달력 사진을 넣는다.
                    holder.uploadImage.setImageResource(R.drawable.ic_calendar);
                }

            }
        }


    }

    //ArrayList의 size(데이터 개수)를 return
    @Override
    public int getItemCount() {
        return myScheduleArrayList.size();
    }

    //item view를 저장하는 ViewHolder 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;
        ImageView uploadImage;

        TextView yearOtherSchedule;
        TextView monthOtherSchedule;
        TextView dayOfMonthOtherSchedule;
        TextView hourOfStartOtherSchedule;
        TextView minuteOfStartOtherSchedule;
        TextView hourOfEndOtherSchedule;
        TextView minuteOfEndOtherSchedule;
        RatingBar ratingBarOtherSchedule;

        //view 객체에 대한 참조
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTextOtherSchedule);
            content = itemView.findViewById(R.id.contentTextOtherSchedule);
            uploadImage = itemView.findViewById(R.id.uploadImageOtherSchedule);

            yearOtherSchedule = itemView.findViewById(R.id.yearOtherSchedule);
            monthOtherSchedule = itemView.findViewById(R.id.monthOtherSchedule);
            dayOfMonthOtherSchedule = itemView.findViewById(R.id.dayOfMonthOtherSchedule);
            hourOfStartOtherSchedule = itemView.findViewById(R.id.hourOfStartOtherSchedule);
            minuteOfStartOtherSchedule = itemView.findViewById(R.id.minuteOfStartOtherSchedule);
            hourOfEndOtherSchedule = itemView.findViewById(R.id.hourOfEndOtherSchedule);
            minuteOfEndOtherSchedule = itemView.findViewById(R.id.minuteOfEndOtherSchedule);
            ratingBarOtherSchedule = itemView.findViewById(R.id.satisfactionOtherSchedule);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    // notifyDataSetChanged()에 의해 리사이클러뷰가 아이템뷰를 갱신하는 과정에서,
                    // 뷰홀더가 참조하는 아이템이 어댑터에서 삭제되면 getAdapterPosition() 메서드는 NO_POSITION을 리턴하기 때문입니다.
                    if (pos != RecyclerView.NO_POSITION) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }
}
