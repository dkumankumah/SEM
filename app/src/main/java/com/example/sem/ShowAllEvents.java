package com.example.sem;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowAllEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> eventsList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);
        recyclerView = findViewById(R.id.recycler_view_events);
        eventsList = new ArrayList<>();

        eventsList = loadEventData();
        setAdapter();
    }
    @Override
    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = eventsList.get(position);
        Intent intent = new Intent(ShowAllEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent);
        startActivity(intent);
    }



    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(eventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Event> loadEventData(){
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event(11111, "title1", "address1", 111, 111, 111, 111, "academic", "mandatory", "Final Exams" ));
        events.add(new Event(22222, "title2", "address2", 222, 222, 222, 222, "charity", "optional", "River Cleanup" ));

        return events;
    }
}
