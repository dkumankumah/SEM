package com.example.sem;


import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sem.model.Event;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ShowAllEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    public static ArrayList<Event> allEventsList;
    public static ArrayList<String> userAttendingEventsIds;
    private ArrayList<String> userInterestedEventsIds;
    private RecyclerView recyclerView;
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
        fetchUserRSVPlists();

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

    // Method to fetch events from Firestore
    private void fetchEventData() {
        db.collection("eventsForTest")
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

    private void fetchUserRSVPlists(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                DocumentSnapshot document = task.getResult();
                userAttendingEventsIds = new ArrayList<>();
                userInterestedEventsIds = new ArrayList<>();
                ArrayList<String> attendingEventIds = (ArrayList<String>) document.get("attending");
                for(String str : attendingEventIds){
                    userAttendingEventsIds.add(str);
                }
                ArrayList<String> interestedEventIds = (ArrayList<String>) document.get("following");
                for(String str : interestedEventIds){
                    userInterestedEventsIds.add(str);
                }
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
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            switch(direction) {
                case ItemTouchHelper.LEFT:
                    //do left action (RSVP Yes)
                    Event swipedLeftEvent = allEventsList.get(position);
                    //grab eventId
                    String swipedLeftEventId = swipedLeftEvent.getEventId();
                    //check if eventId is on user's attending array
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                DocumentSnapshot document = task.getResult();
                                ArrayList<String> eventIds = (ArrayList<String>) document.get("attending");
                                for(String str : eventIds){
                                    userAttendingEventsIds.add(str);
                                }
                            //https://firebase.google.com/docs/firestore/manage-data/add-data#java_24
                            DocumentReference userListReference = db.collection("users").document(userId);
                            // Atomically add a new eventId to the "attending" array field.
                            userListReference.update("attending", FieldValue.arrayUnion(swipedLeftEventId));
                        }
                    });
                    if(userAttendingEventsIds.contains(swipedLeftEventId)){
                        Toast.makeText(ShowAllEvents.this, "You are attending this event.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ShowAllEvents.this, "See you there!", Toast.LENGTH_SHORT).show();
                        userAttendingEventsIds.add(swipedLeftEventId);
                    }
                    recyclerView.getAdapter().notifyItemChanged(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    //do left action (add to "following")
                    Event swipedRightEvent = allEventsList.get(position);
                    //grab eventId
                    String swipedRightEventId = swipedRightEvent.getEventId();
                    //check if eventId is on user's attending array
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            DocumentSnapshot document = task.getResult();
                            userInterestedEventsIds = (ArrayList<String>) document.get("following");
                                if(userInterestedEventsIds.contains(swipedRightEventId)){
                                    Toast.makeText(recyclerView.getContext(), "You are following this event.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    userInterestedEventsIds.add(swipedRightEventId);
                                    Toast.makeText(recyclerView.getContext(), "Subscribed to updates!", Toast.LENGTH_SHORT).show();
                                    userInterestedEventsIds.add(swipedRightEventId);
                                    DocumentReference userListReference = db.collection("users").document(userId);
                                    // Atomically add a new eventId to the "attending" array field.
                                    userListReference.update("following", FieldValue.arrayUnion(swipedRightEventId));
                                }
                        }
                    });
                    recyclerView.getAdapter().notifyItemChanged(position);
                    break;
            }
        }

    };

    public void recyclerViewListClicked(View v, int position) {
//        Event selectedEvent = allEventsList.get(position);
//        Intent intent = new Intent(ShowAllEvents.this, EventOnClick.class);
//        intent.putExtra("selected_event", selectedEvent); // Pass the serializable event
//        startActivity(intent);
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(allEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public static ArrayList<Event> getAllEvents(){
        return allEventsList;
    }

    public static ArrayList<String> getAttendingEventIds(){
        return userAttendingEventsIds;
    }
}
