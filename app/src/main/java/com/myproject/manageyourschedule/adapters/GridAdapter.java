package com.myproject.manageyourschedule.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myproject.manageyourschedule.R;
import com.myproject.manageyourschedule.viewholder.ViewHolder;

import java.util.Calendar;
import java.util.List;

public class GridAdapter extends BaseAdapter {

    private final List<String> list;
    //XML레이아웃 파일에서 뷰를 생성할때 LayoutInflater를 사용한다.
    private final LayoutInflater inflater;

    private Calendar mCal;

    /**
     * 생성자
     *
     * @param context
     * @param list
     */
    public GridAdapter(Context context, List<String> list) {
        this.list = list;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Calendar getmCal() {
        return mCal;
    }

    public void setmCal(Calendar mCal) {
        this.mCal = mCal;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //ViewHolder: ItemView의 각 요소를 바로 엑세스 할 수 있도록 저장해두고 사용하기 위한 객체
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
            holder = new ViewHolder();

            holder.setTvItemGridView((TextView)convertView.findViewById(R.id.tv_item_gridview));

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.getTvItemGridView().setText(getItem(position));

        //해당 날짜 텍스트 컬러,배경 변경
        mCal = (Calendar.getInstance());
        //오늘 day 가져옴
        Integer today = mCal.get(Calendar.DAY_OF_MONTH);
        String sToday = String.valueOf(today);
        if (sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
            holder.getTvItemGridView().setTextColor(Color.parseColor("#ff8800"));
        }
        return convertView;
    }
}
