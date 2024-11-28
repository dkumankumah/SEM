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

import com.example.sem.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ShowAllEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> allEventsList;
    private ArrayList<String> userAttendingEventsList = new ArrayList<>();
    private ArrayList<String> userFollowingEventsList;
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
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                userAttendingEventsList = (ArrayList<String>) document.get("attending");
                                ArrayList <String> userSwipedLeftEventIds = new ArrayList<>();
                                if(document.exists()){
                                    if(userAttendingEventsList.contains(swipedLeftEventId)){
                                        Toast.makeText(recyclerView.getContext(), "You are attending this event.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(recyclerView.getContext(), "See you there!", Toast.LENGTH_SHORT).show();

                                        //this is not updating properly at firebase
                                        userAttendingEventsList.add(swipedLeftEventId);
                                        //get a reference to the path users -> uid -> attending
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("attending");
                                        //set new node with userAttendingEventsList
                                        databaseReference.setValue(userAttendingEventsList);
                                    }
                                }
                                else{
                                    //document does not exist.  failure, do nothing.
                                }
                            }
                            else{
                                //task was not successful.  failure, do nothing
                            }
                        }
                    });

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
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    userAttendingEventsList = (ArrayList<String>) document.get("attending");
                                    if(userAttendingEventsList.contains(swipedRightEventId)){
                                        Toast.makeText(recyclerView.getContext(), "You are following this event.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        userAttendingEventsList.add(swipedRightEventId);
                                        Toast.makeText(recyclerView.getContext(), "Subscribed to updates!", Toast.LENGTH_SHORT).show();
                                        //TO DO add the eventID into user's firebase "following" field
                                    }
                                }
                                else{
                                    //document does not exist.  failure, do nothing.
                                }
                            }
                            else{
                                //task was not successful.  failure, do nothing
                            }
                        }
                    });
                    recyclerView.getAdapter().notifyItemChanged(position);
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
}
