package com.example.sem;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowAllEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> allEventsList;
    private RecyclerView recyclerView;
    public static ArrayList<Event> myEventsList;
    public ArrayList<Event> followingEventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);
        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        myEventsList = new ArrayList<>();
        followingEventsList = new ArrayList<>();

        allEventsList = loadAllEventData();

        setAdapter();

        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch(direction){
                case ItemTouchHelper.LEFT:
                    //do left action
                    Event swipedEvent = allEventsList.get(position);
                    myEventsList.add(swipedEvent);
                    allEventsList.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    Intent intent = new Intent(ShowAllEvents.this, ShowMyEvents.class);
                    intent.putExtra("swiped_event", swipedEvent);
                    startActivity(intent);
                    break;
                case ItemTouchHelper.RIGHT:
                    //do right action
                    allEventsList.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    Toast toastRight = Toast.makeText(recyclerView.getContext(), "TODO: place vent on Interested list", Toast.LENGTH_SHORT);
                    toastRight.show();
                    break;
            }
        }

    };
    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = allEventsList.get(position);
        Intent intent = new Intent(ShowAllEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent);
        startActivity(intent);
    }



    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(allEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Event> loadAllEventData(){
        ArrayList<Event> events = new ArrayList<>();
        events.add(new Event(11111, "title1", "address1", 111, 111, 111, 111, "academic", "mandatory", "Final Exams" ));
        events.add(new Event(22222, "title2", "address2", 222, 222, 222, 222, "charity", "optional", "River Cleanup" ));
        events.add(new Event(33333, "title3", "address3", 333, 333, 333, 333, "extracurricular", "optional", "Prom" ));
        events.add(new Event(44444, "title4", "address4", 444, 444, 444, 444, "academic", "mandatory", "River Cleanup" ));
        events.add(new Event(55555, "title5", "address5", 555, 555, 555, 555, "athletics", "optional", "Football versus Carver" ));
        events.add(new Event(66666, "title6", "address6", 666, 666, 666, 666, "athletics", "optional", "Soccer versus Holy Cross" ));
        events.add(new Event(77777, "title7", "address7", 777, 777, 777, 777, "extracurricular", "optional", "APC performance" ));
        return events;
    }
    public static ArrayList<Event> getMyEventsList(){
        return myEventsList;
    }
    public ArrayList<Event> getFollowingEventsList(){
        return followingEventsList;
    }
}
