package com.example.sem;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sem.model.Event;

import java.util.Date;

public class EventOnClick extends AppCompatActivity {
    private TextView mTextViewTitle;
    private TextView mTextViewAddress;
    private TextView mTextViewEventDate;
    private TextView mTextViewTime;
    private TextView mTextViewCategory;
    private TextView mTextViewAttendanceType;
    private TextView mTextViewFollowingType;
    private TextView mTextViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_on_click);

        // Get the intent from the sending activity
        Intent myIntent = getIntent();
        // Get the event object from the intent's extras
        Event selectedEvent = (Event) myIntent.getSerializableExtra("selected_event");



        mTextViewTitle = findViewById(R.id.textview_title);
        mTextViewAddress = findViewById(R.id.textview_address);
        mTextViewEventDate = findViewById(R.id.textview_date);
        mTextViewTime = findViewById(R.id.textview_time);
        mTextViewCategory = findViewById(R.id.textview_category);
        mTextViewDescription = findViewById(R.id.textview_description);

        String title = "Event title: " + selectedEvent.getEventName();
        String address = "Street Address: " + selectedEvent.getLocation();
        String date = "Date: " + selectedEvent.getEventDate();
        String time = "Starting at: " + selectedEvent.getEventTime();
        String category = "Type of Event: " + selectedEvent.getEventCategory();
        String description = "Details: " + selectedEvent.getEventDescription();

        mTextViewTitle.setText(title);
        mTextViewAddress.setText(address);
        mTextViewEventDate.setText(date);
        mTextViewTime.setText(time);
        mTextViewCategory.setText(category);
        mTextViewDescription.setText(description);

    }
}
