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
    private TextView mTextViewFollowingType;
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
        mTextViewAttendanceType = findViewById(R.id.textview_number_attending);
        mTextViewFollowingType = findViewById(R.id.textview_number_following);
        mTextViewDescription = findViewById(R.id.textview_description);

        String title = "Event title: " + selectedEvent.getEventName();
        String address = "Street Address:  TODO convert coordinates to street address";
        String date = "Date: TODO parce date object for date of event ";
        String time = "Starting at: TODO parce date object for start time of event ";
        String category = "Type of Event: " + selectedEvent.getEventCategory();
        String description = "Details: " + selectedEvent.getEventDescription();
        String numberAttending = "Number Attending: TODO get number attending ";
        String numberFollowing = "Number Attending: TODO get number following ";

        mTextViewTitle.setText(title);
        mTextViewAddress.setText(address);
        mTextViewEventDate.setText(date);
        mTextViewTime.setText(time);
        mTextViewCategory.setText(category);
        mTextViewAttendanceType.setText(numberAttending);
        mTextViewFollowingType.setText(numberFollowing);
        mTextViewDescription.setText(description);

    }
}
