package com.example.sem;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sem.model.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
    private BottomNavigationView bottomNav;

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
        bottomNav = findViewById(R.id.bottomNav);

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

        // Set the initial selected item
        bottomNav.setSelectedItemId(R.id.nav_following);

        // Navigation bar to all other activities
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Navigate to AdminDashboardActivity
                        Intent intent = new Intent(EventOnClick.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_list:
                        // Navigate to MyListActivity
                        Intent intent2 = new Intent(EventOnClick.this, ShowMyEvents.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_following:
                        // Navigate to ShowInterestedEvents
                        Intent intent3 = new Intent(EventOnClick.this, ShowInterestedEvents.class);
                        startActivity(intent3);
                        return true;
                    case R.id.nav_maps:
                        // Navigate to Maps
                        return true;
                    case R.id.nav_account:
                        // Navigate to ProfileActivity
                        Intent intent4 = new Intent(EventOnClick.this, ProfileActivity.class);
                        startActivity(intent4);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


}
