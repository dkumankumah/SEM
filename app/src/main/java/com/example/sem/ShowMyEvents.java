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


public class ShowMyEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    public ArrayList<Event> allEventsList;
    private ArrayList<Event> userAttendingEventsList;
    private ArrayList<String> userAttendingEventsIds;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        userAttendingEventsList = new ArrayList<>();
        userAttendingEventsIds = new ArrayList<>();
        bottomNav = findViewById(R.id.bottomNav);


                // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();


        fetchEventData();
//        fetchUserRSVPlists();

//        setAdapter();


        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set the initial selected item
        bottomNav.setSelectedItemId(R.id.nav_list);

        // Navigation bar to all other activities
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Navigate to AdminDashboardActivity
                        Intent intent = new Intent(ShowMyEvents.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_list:
                        // Navigate to MyListActivity
                        return true;
                    case R.id.nav_following:
                        // Navigate to ShowInterestedEvents
                        Intent intent2 = new Intent(ShowMyEvents.this, ShowInterestedEvents.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_maps:
                        // Navigate to Maps
                        Intent intent3 = new Intent(ShowMyEvents.this, MapHostActivity.class);
                        intent3.putExtra("ALL_EVENTS", true);
                        startActivity(intent3);
                        return true;
                    case R.id.nav_account:
                        // Navigate to ProfileActivity
                        Intent intent4 = new Intent(ShowMyEvents.this, ProfileActivity.class);
                        startActivity(intent4);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

//    protected void onStart() {
//        super.onStart();
//        Log.d("allevents", String.valueOf(allEventsList.size()));
//        Log.d("myevents", String.valueOf(userAttendingEventsList.size()));
//        Log.d("ids", String.valueOf(userAttendingEventsIds.size()));
//
//        for(Event event : allEventsList){
//            String checkId = event.getEventId();
//            if(userAttendingEventsIds.contains(checkId)){
//                userAttendingEventsList.add(event);
//            }
//        }
//    }

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
                                allEventsList.add(event);
                                // Log event details
                                Log.d(TAG, "Event: " + event.getEventName() + ", Date: " + event.getEventDate());
                            }

                            fetchUserRSVPlists();
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

        userRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    ArrayList<String> attendingEventIds = (ArrayList<String>) document.get("attending");
                    Log.d("attendingEvents: ", attendingEventIds.toString());
                    userAttendingEventsIds.addAll(attendingEventIds);
                    Log.d("ids: ", String.valueOf(userAttendingEventsIds.size()));
                    Log.d("allevents", String.valueOf(allEventsList.size()));

                    for(Event event : allEventsList){
                        String checkId = event.getEventId();
                        if(userAttendingEventsIds.contains(checkId)){
                            userAttendingEventsList.add(event);
                        }
                    }

                    Log.d("myevents", String.valueOf(userAttendingEventsList.size()));

                    // Notify RecyclerView adapter of data changes
                    setAdapter();
                    recyclerView.getAdapter().notifyDataSetChanged();

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
            switch(direction) {
                case ItemTouchHelper.LEFT:
                    Toast.makeText(ShowMyEvents.this, "To delete, swipe in the other direction", Toast.LENGTH_SHORT).show();
                    recyclerView.getAdapter().notifyItemChanged(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    //get the swiped event
                    Event swipedRightEvent = userAttendingEventsList.get(position);
                    //grab eventId
                    String swipedRightEventId = swipedRightEvent.getEventId();
                    //remove the event from the list and update the adapter.
                    userAttendingEventsList.remove(swipedRightEvent);
                    recyclerView.getAdapter().notifyItemRemoved(position);

                    Toast.makeText(ShowMyEvents.this, "Sorry to miss you :(", Toast.LENGTH_SHORT).show();
                    //make reference to database storage location
                    DocumentReference userRef = db.collection("users").document(userId);
                    //deleting event from firebase
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            DocumentReference userListReference = db.collection("users").document(userId);
                            userListReference.update("attending", FieldValue.arrayRemove(swipedRightEventId));
                        }
                    });
                    break;
            }
        }
    };

    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = userAttendingEventsList.get(position);
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
