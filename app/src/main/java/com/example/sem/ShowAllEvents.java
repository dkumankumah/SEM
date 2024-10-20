package com.example.sem;


import static android.content.ContentValues.TAG;

import static java.lang.String.valueOf;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.sem.model.Event;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowAllEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> eventsList;
    private ArrayList<Event> allEventsList;
    private RecyclerView recyclerView;
    public static ArrayList<Event> myEventsList = new ArrayList<>();
    public static ArrayList<Event> interestedEventsList = new ArrayList<>();
    private FirebaseFirestore db;
    private Button showMyEventsButton;
    private Button showInterestedEventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);
        recyclerView = findViewById(R.id.recycler_view_events);
        showMyEventsButton = (Button)findViewById(R.id.show_my_events);
        showInterestedEventsButton = (Button)findViewById(R.id.show_interested_events);
        allEventsList = new ArrayList<>();

       // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_events);

        fetchEventData();

        setAdapter();


        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        showMyEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllEvents.this, ShowMyEvents.class);
                startActivity(intent);
            }
        });

        showInterestedEventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowAllEvents.this, ShowInterestedEvents.class);
                startActivity(intent);
            }
        });
    }

    // Method to fetch 20 events from Firestore
    private void fetchEventData() {
        db.collection("events")
                .limit(20) // Fetch the first 20 events
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (!documents.isEmpty()) {
                            for (QueryDocumentSnapshot document : documents) {
                                // Convert Firestore document to Event object
                                Event event = document.toObject(Event.class);
                                allEventsList.add(event);
                                // Log event details
                                Log.d(TAG, "Event: " + event.getEventName() + ", Date: " + event.getEventDate());
                            }
                            // Notify RecyclerView adapter of data changes
                            recyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "No events found");
                        }
                    } else {
                        Log.w(TAG, "Error getting events.", task.getException());
                    }
                });
    }

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch(direction) {
                case ItemTouchHelper.LEFT:
                    //do left action (RSVP Yes)
                    Event swipedLeftEvent = allEventsList.get(position);
                    myEventsList.add(swipedLeftEvent);
                    if (interestedEventsList.contains(swipedLeftEvent)) {
                        interestedEventsList.remove(swipedLeftEvent);
                    }
                    //update imageview to have a checkmark
                    recyclerView.getAdapter().notifyItemChanged(position);
                    Toast toastLeft = Toast.makeText(recyclerView.getContext(), "See you there!", Toast.LENGTH_SHORT);
                    toastLeft.show();
                    break;
                case ItemTouchHelper.RIGHT:
                    //do right action
                    Event swipedRightEvent = allEventsList.get(position);
                    interestedEventsList.add(swipedRightEvent);
                    if(myEventsList.contains(swipedRightEvent)){
                        myEventsList.remove(swipedRightEvent);
                    }
                    //update imageview to eyeball
                    recyclerView.getAdapter().notifyItemChanged(position);
                    Toast toastRight = Toast.makeText(recyclerView.getContext(), "Subscribed to Updates!", Toast.LENGTH_SHORT);
                    toastRight.show();
                    break;
            }
        }

    };

    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = allEventsList.get(position);
        Intent intent = new Intent(ShowAllEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent); // Pass the serializable event
        startActivity(intent);
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(allEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public static ArrayList<Event> getMyEventsList(){
        return myEventsList;
    }
    public static ArrayList<Event> getFollowingEventsList(){
        return interestedEventsList;
    }
}
