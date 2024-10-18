package com.example.sem;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sem.model.Event;

public class EventOnClick extends AppCompatActivity {
    private TextView mTextViewTitle;
    private TextView mTextViewAddress;
    private TextView mTextViewEventDate;
    private TextView mTextViewTime;
    private TextView mTextViewCategory;
    private TextView mTextViewAttendanceType;
    private TextView mTextViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_on_click);

        Event selectedEvent = getIntent().getParcelableExtra("selected_event");

        mTextViewTitle = findViewById(R.id.textview_title);
        mTextViewAddress = findViewById(R.id.textview_address);
        mTextViewEventDate = findViewById(R.id.textview_date);
        mTextViewTime = findViewById(R.id.textview_time);
        mTextViewCategory = findViewById(R.id.textview_category);
        mTextViewAttendanceType = findViewById(R.id.textview_attendance_type);
        mTextViewDescription = findViewById(R.id.textview_description);

//        String title = "Event title: " + selectedEvent.getTitle();
//        String address = "Street Address: " + selectedEvent.getAddress() + selectedEvent.getZipCode();
//        String date = "Date: " + selectedEvent.getDate();
//        String time = "From " + selectedEvent.getStartTime() + " until: " + selectedEvent.getEndTime();
//        String category = "Type of Event: " + selectedEvent.getCategory();
//        String attendance = "Your attendance is " + selectedEvent.getAttendanceType();
//        String description = "Details: " + selectedEvent.getDescription();

//        mTextViewTitle.setText(title);
//        mTextViewAddress.setText(address);
//        mTextViewEventDate.setText(date);
//        mTextViewTime.setText(time);
//        mTextViewCategory.setText(category);
//        mTextViewAttendanceType.setText(attendance);
//        mTextViewDescription.setText(description);

    }
}
