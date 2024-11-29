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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowMyEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> allEventsList;
    private ArrayList<Event> userAttendingEventsList;
    private ArrayList<String> userAttendingEventsIds;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        userAttendingEventsIds = new ArrayList<>();
        userAttendingEventsList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_events);

        //populate allEventsList by fetching data from Firestore
        fetchEventData();

        //retrieve eventIds from firebase and store in an array called "attendingEventIds"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String data = childSnapshot.getValue(String.class);
                    userAttendingEventsIds.add(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });

        //check to see if the IDs on the allEventsList are included userAttendingEventsIds is
        for(Event event : allEventsList){
            String checkId = event.getEventId();
            if(userAttendingEventsIds.contains(checkId)){
                userAttendingEventsList.add(event);
            }
        }



        setAdapter();


        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            DocumentReference userRef = db.collection("users").document(userId);

            switch(direction){
                case ItemTouchHelper.LEFT:
                    //do left action
                    Event swipedLeftEvent = userAttendingEventsList.get(position);
                    String swipedLeftEventId = swipedLeftEvent.getEventId();
                    userAttendingEventsList.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    Toast.makeText(recyclerView.getContext(), "You are no longer attending this event :(", Toast.LENGTH_SHORT).show();
                    //https://firebase.google.com/docs/firestore/manage-data/add-data#java_24
                    DocumentReference userListReference = db.collection("users").document(userId);
                    // Atomically remove eventId from the "attending" array field.
                    userListReference.update("attending", FieldValue.arrayRemove(swipedLeftEventId));
                    break;

            }
        }
    };

    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = allEventsList.get(position);
        Intent intent = new Intent(ShowMyEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent); // Pass the serializable event
        startActivity(intent);
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(userAttendingEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

}

//public class ShowMyEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
//    private ArrayList<Event> allEventsList;
//    private ArrayList<Event> myEventsList;
//    ArrayList<String> attendingEventIds;
//    private RecyclerView recyclerView;
//    private FirebaseFirestore db;
//    private ArrayList<String> userFollowingEventsList = new ArrayList<>();
//    private String userId;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.my_events);
//        recyclerView = findViewById(R.id.recycler_view_events);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        allEventsList = new ArrayList<>();
//        attendingEventIds = new ArrayList<>();
//
//        // Initialize Firestore and Firebase
//        FirebaseApp.initializeApp(this);
//        db = FirebaseFirestore.getInstance();
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
//
//        //retrieve eventIds from firebase and store in an array called "attendingEventIds"
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                    String data = childSnapshot.getValue(String.class);
//                    attendingEventIds.add(data);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle any errors
//            }
//        });
//
//        fetchEventData();
//
//        for(Event event : allEventsList){
//            myEventsList = new ArrayList<>();
//            String eventId = event.getEventId();
//            if(attendingEventIds.contains(eventId)){
//                myEventsList.add(event);
//            }
//        }
//
////        Log.d("arrayData", attendingEventIds.toString());
//        Log.d("arrayData", "line 89 alleventsList" + allEventsList.toString());
//        Log.d("arrayData", myEventsList.toString());
//
//        setAdapter();
//
//        //implement swipe left/right
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//
//    }
//
//    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            int position = viewHolder.getAdapterPosition();
//
//            switch(direction){
//                case ItemTouchHelper.LEFT:
//                    //do left action
//                    Event swipedLeftEvent = myEventsList.get(position);
//                    String swipedLeftEventId = swipedLeftEvent.getEventId();
//                    myEventsList.remove(position);
//                    recyclerView.getAdapter().notifyItemRemoved(position);
//                    Toast.makeText(recyclerView.getContext(), "You are no longer attending this event :(", Toast.LENGTH_SHORT).show();
//                    //https://firebase.google.com/docs/firestore/manage-data/add-data#java_24
//                    DocumentReference userListReference = db.collection("users").document(userId);
//                    // Atomically remove eventId from the "attending" array field.
//                    userListReference.update("attending", FieldValue.arrayRemove(swipedLeftEventId));
//                    break;
//
//            }
//        }
//    };
//
//    public void recyclerViewListClicked(View v, int position) {
//        Event selectedEvent = myEventsList.get(position);
//        Intent intent = new Intent(ShowMyEvents.this, EventOnClick.class);
//        intent.putExtra("selected_event", selectedEvent);
//        startActivity(intent);
//    }
//
//    // Method to fetch events from Firestore
//    private void fetchEventData() {
//        db.collection("eventsForTest")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot documents = task.getResult();
//                        if (!documents.isEmpty()) {
//                            for (QueryDocumentSnapshot document : documents) {
//                                // Convert Firestore document to Event object
//                                Event event = document.toObject(Event.class);
//                                allEventsList.add(event);
//                                // Log event details
//                                Log.d(TAG, "Event: " + event.getEventName() + ", Date: " + event.getEventDate());
//                            }
//                            // Notify RecyclerView adapter of data changes
//                            recyclerView.getAdapter().notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "No events found");
//                        }
//                    } else {
//                        Log.w(TAG, "Error getting events.", task.getException());
//                    }
//                });
//    }
//
//    private void setAdapter() {
//        recyclerAdapter adapter = new recyclerAdapter(myEventsList, this);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);
//    }
//}
