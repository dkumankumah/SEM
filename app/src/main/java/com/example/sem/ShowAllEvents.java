package com.example.sem;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowAllEvents extends AppCompatActivity {
    private ListView mListViewEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        ArrayList<Event> events = loadEventData();
        EventListAdapter adapter = new EventListAdapter(ShowAllEvents.this, events);

        mListViewEvents = findViewById(R.id.listview_events);
        mListViewEvents.setAdapter(adapter);
        mListViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Event selectedEvent = (Event) parent.getItemAtPosition(position);

                Intent intent = new Intent(ShowAllEvents.this, EventOnClick.class);
                intent.putExtra("selected_event", selectedEvent);
                startActivity(intent);
            }
        });
    }

    private ArrayList<Event> loadEventData(){
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event("title1", "address1", 111, 111, 111, 111, "academic", "mandatory", "Final Exams" ));
        events.add(new Event("title2", "address2", 222, 222, 222, 222, "charity", "optional", "River Cleanup" ));

        return events;
    }
}
