package com.example.sem;


import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
    public static ArrayList<String> userInterestedEventsIds;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNav;
    private String eventCategory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        Intent intent = getIntent();

        eventCategory = intent.getStringExtra("type");



        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        bottomNav = findViewById(R.id.bottomNav);

        // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_events);

        //fetch data from firebase
        fetchEventData();

        setAdapter();


        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set the initial selected item
        //bottomNav.setSelectedItemId(R.id.nav_list);

        // Navigation bar to all other activities
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Navigate to AdminDashboardActivity
                        Intent intent = new Intent(ShowAllEvents.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_list:
                        Intent intent1 = new Intent(ShowAllEvents.this, ShowMyEvents.class);
                        startActivity(intent1);
                        return true;
                        // Navigate to MyListActivity
                    case R.id.nav_following:
                        // Navigate to ShowInterestedEvents
                        Intent intent2 = new Intent(ShowAllEvents.this, ShowInterestedEvents.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_maps:
                        // Navigate to Maps
                        return true;
                    case R.id.nav_account:
                        // Navigate to ProfileActivity
                        Intent intent4 = new Intent(ShowAllEvents.this, ProfileActivity.class);
                        startActivity(intent4);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    // Method to fetch events from Firestore
    public void fetchEventData() {
        db.collection("eventsForTest")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (!documents.isEmpty()) {
                            for (QueryDocumentSnapshot document : documents) {
                                // Convert Firestore document to Event object
                                Event event = document.toObject(Event.class);
                                if(eventCategory.equals("All")){
                                    allEventsList.add(event);
                                }
                                else if(event.getEventCategory().equals(eventCategory)) {
                                    allEventsList.add(event);
                                }
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
                    fetchUserRSVPlists();
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
                // Notify RecyclerView adapter of data changes
                setAdapter();
                recyclerView.getAdapter().notifyDataSetChanged();
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
                    Event swipedLeftEvent = allEventsList.get(position);
                    String swipedLeftId = swipedLeftEvent.getEventId();

                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            ArrayList<String> attendingEventIds = (ArrayList<String>) document.get("attending");
                            ArrayList<String> followingEventIds = (ArrayList<String>) document.get("following");
                            for(String str : attendingEventIds){
                                userAttendingEventsIds.add(str);
                            }
                            for(String str : followingEventIds){
                                userInterestedEventsIds.add(str);
                            }
                            if(userAttendingEventsIds.contains(swipedLeftId)){
                                Toast.makeText(ShowAllEvents.this, "You are attending this event.", Toast.LENGTH_SHORT).show();
                                recyclerView.getAdapter().notifyItemChanged(position);
                            }
                            else{
                                userAttendingEventsIds.add(swipedLeftId);
                                Toast.makeText(ShowAllEvents.this, "See you there!", Toast.LENGTH_SHORT).show();
                                //https://firebase.google.com/docs/firestore/manage-data/add-data#java_24
                                DocumentReference userListReference = db.collection("users").document(userId);
                                // Atomically add a new eventId to the "attending" array field.
                                userListReference.update("attending", FieldValue.arrayUnion(swipedLeftId));
                                recyclerView.getAdapter().notifyItemChanged(position);
                                if(userInterestedEventsIds.contains(swipedLeftId)){
                                    userInterestedEventsIds.remove(swipedLeftId);
                                    // Atomically add a new eventId to the "attending" array field.
                                    userListReference.update("following", FieldValue.arrayRemove(swipedLeftId));

                                }
                            }

                        }
                    });
                    break;
                case ItemTouchHelper.RIGHT:
                    Event swipedRightEvent = allEventsList.get(position);
                    String swipedRightId = swipedRightEvent.getEventId();
                    //check if it's already on interestedeventslist
                    if(userInterestedEventsIds.contains(swipedRightId)){
                        //yes say "you're already following this event"
                        Toast.makeText(ShowAllEvents.this, "You are following this event.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //if no, check if it's on the attending events list
                        if (userAttendingEventsIds.contains(swipedRightId)) {
                            //if yes say "you're already attending this event"
                            Toast.makeText(ShowAllEvents.this, "You are attending this event.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //if no, add to interestedeventsList and interestedevents db
                            userInterestedEventsIds.add(swipedRightId);
                            // Atomically add a new eventId to the "attending" array field.
                            Toast.makeText(ShowAllEvents.this, "Subscribed to updates!", Toast.LENGTH_SHORT).show();
                            DocumentReference userListReference = db.collection("users").document(userId);
                            userListReference.update("following", FieldValue.arrayUnion(swipedRightId));

                        }
                        }
                    recyclerView.getAdapter().notifyItemChanged(position);
                    break;
            }
        }
    };



    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = allEventsList.get(position);
        Intent intent = new Intent(this, MapHostActivity.class);
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
    public static ArrayList<String> getAttendingEventIds(){
        return userAttendingEventsIds;
    }

    public static ArrayList<String> getInterestedEventIds(){
        return userInterestedEventsIds;
    }
}
