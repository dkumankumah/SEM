package com.example.sem;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventOnClick extends AppCompatActivity {
    private TextView mTextViewTitle;
    private TextView mTextViewAddress;
    private TextView mTextViewZipCode;
    private TextView mTextViewEventDate;
    private TextView mTextViewStartTime;
    private TextView mTextViewEndTime;
    private TextView mTextViewCategory;
    private TextView mTextViewAttendanceType;
    private TextView mTextViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_on_click);

        Event selectedEvent = (Event) getIntent().getSerializableExtra("selected_event");

        mTextViewTitle = findViewById(R.id.textview_title);
        mTextViewAddress = findViewById(R.id.textview_address);
        mTextViewZipCode = findViewById(R.id.textview_zipcode);
        mTextViewEventDate = findViewById(R.id.textview_date);
        mTextViewStartTime = findViewById(R.id.textview_start_time);
        mTextViewEndTime = findViewById(R.id.textview_end_time);
        mTextViewCategory = findViewById(R.id.textview_category);
        mTextViewAttendanceType = findViewById(R.id.textview_attendance_type);
        mTextViewDescription = findViewById(R.id.textview_description);

        mTextViewTitle.setText(String.valueOf(selectedEvent.getTitle()));
        mTextViewAddress.setText(String.valueOf(selectedEvent.getAddress()));
        mTextViewZipCode.setText(String.valueOf(selectedEvent.getZipCode()));
        mTextViewEventDate.setText(String.valueOf(selectedEvent.getDate()));
        mTextViewStartTime.setText(String.valueOf(selectedEvent.getStartTime()));
        mTextViewEndTime.setText(String.valueOf(selectedEvent.getEndTime()));
        mTextViewCategory.setText(String.valueOf(selectedEvent.getCategory()));
        mTextViewAttendanceType.setText(String.valueOf(selectedEvent.getAttendanceType()));
        mTextViewDescription.setText(String.valueOf(selectedEvent.getDescription()));

    }
}
