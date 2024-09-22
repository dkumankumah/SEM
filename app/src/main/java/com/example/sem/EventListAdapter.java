package com.example.sem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<Event>{
    public EventListAdapter(Context context, ArrayList<Event> events){
        super(context, 0, events);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_event, parent, false);
        }

        Event event = getItem(position);

        TextView textViewTitle = convertView.findViewById(R.id.textview_event_title);
        TextView textViewAddress = convertView.findViewById(R.id.textview_event_address);
        TextView textViewZipCode = convertView.findViewById(R.id.textview_event_zipcode);
        TextView textViewDate = convertView.findViewById(R.id.textview_event_date);
        TextView textViewStartTime = convertView.findViewById(R.id.textview_event_start_time);
        TextView textViewEndTime = convertView.findViewById(R.id.textview_event_end_time);
        TextView textViewCategory = convertView.findViewById(R.id.textview_event_category);
        TextView textViewAttendanceType = convertView.findViewById(R.id.textview_event_attendance_type);
        TextView textViewDescription = convertView.findViewById(R.id.textview_event_description);

        textViewTitle.setText(String.valueOf(event.getTitle()));
        textViewAddress.setText(String.valueOf(event.getAddress()));
        textViewZipCode.setText(String.valueOf(event.getZipCode()));
        textViewDate.setText(String.valueOf(event.getDate()));
        textViewStartTime.setText(String.valueOf(event.getStartTime()));
        textViewEndTime.setText(String.valueOf(event.getEndTime()));
        textViewCategory.setText(String.valueOf(event.getCategory()));
        textViewAttendanceType.setText(String.valueOf(event.getAttendanceType()));
        textViewDescription.setText(String.valueOf(event.getDescription()));

        return convertView;
    }
}