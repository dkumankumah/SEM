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

public class ShowInterestedEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> allEventsList;
    private ArrayList<Event> userInterestedEventsList;
    private ArrayList<String> userInterestedEventsIds;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        userInterestedEventsList = new ArrayList<>();
        userInterestedEventsIds = new ArrayList<>();
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
        bottomNav.setSelectedItemId(R.id.nav_following);

        // Navigation bar to all other activities
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Navigate to AdminDashboardActivity
                        Intent intent = new Intent(ShowInterestedEvents.this, AdminDashboardActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_list:
                        // Navigate to MyListActivity
                        Intent intent2 = new Intent(ShowInterestedEvents.this, ShowMyEvents.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_following:
                        // Navigate to ShowInterestedEvents
                        return true;
                    case R.id.nav_maps:
                        // Navigate to Maps
                        return true;
                    case R.id.nav_account:
                        // Navigate to ProfileActivity
                        Intent intent4 = new Intent(ShowInterestedEvents.this, ProfileActivity.class);
                        startActivity(intent4);
                        return true;
                    default:
                        return false;
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
//                    //do left action change RSVP to YES
                    Event swipedLeftEvent = userInterestedEventsList.get(position);
//                    //grab eventId
                    String swipedLeftEventId = swipedLeftEvent.getEventId();
//                    //check if eventId is on user's attending array
                    Toast.makeText(ShowInterestedEvents.this, "See you there!!", Toast.LENGTH_SHORT).show();
                    userInterestedEventsList.remove(swipedLeftEvent);
//                    userInterestedEventsList.remove(swipedLeftEvent);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            DocumentReference userListReference = db.collection("users").document(userId);
                            userListReference.update("following", FieldValue.arrayRemove(swipedLeftEventId));
                            userListReference.update("attending", FieldValue.arrayUnion(swipedLeftEventId));
                        }
                    });
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    break;
                case ItemTouchHelper.RIGHT:
                    Event swipedRightEvent = userInterestedEventsList.get(position);
                    //grab eventId
                    String swipedRightEventId = swipedRightEvent.getEventId();
                    //check if eventId is on user's attending array
                    Toast.makeText(ShowInterestedEvents.this, "You'll no longer receive updates!", Toast.LENGTH_SHORT).show();
                    userInterestedEventsList.remove(swipedRightEvent);
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            DocumentReference userListReference = db.collection("users").document(userId);
                            userListReference.update("following", FieldValue.arrayRemove(swipedRightEventId));
                        }
                    });
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    break;
            }
        }
    };

    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = userInterestedEventsList.get(position);
        Intent intent = new Intent(ShowInterestedEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent); // Pass the serializable event
        startActivity(intent);
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(userInterestedEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
                    ArrayList<String> attendingEventIds = (ArrayList<String>) document.get("following");
                    Log.d("attendingEvents: ", attendingEventIds.toString());
                    userInterestedEventsIds.addAll(attendingEventIds);;

                    for(Event event : allEventsList){
                        String checkId = event.getEventId();
                        if(userInterestedEventsIds.contains(checkId)){
                            userInterestedEventsList.add(event);
                        }
                    }

                    // Notify RecyclerView adapter of data changes
                    setAdapter();
                    recyclerView.getAdapter().notifyDataSetChanged();

                }

            }

        });
    }
}
