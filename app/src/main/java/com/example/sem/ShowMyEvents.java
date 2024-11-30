package com.example.sem;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.EventListener;

public class ShowMyEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> allEventsList;
    private ArrayList<Event> userAttendingEventsList;
    private ArrayList<String> userAttendingEventsIds;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        userAttendingEventsList = new ArrayList<>();
        userAttendingEventsIds = new ArrayList<>();

        // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();


        fetchEventData();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //make reference to database to retreive data from attending events path
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    ArrayList<String> stringList = (ArrayList<String>) document.get("attending");
                    for(String str : stringList){
                        userAttendingEventsIds.add(str);
                    }
                    for(Event event : allEventsList){
                        String checkId = event.getEventId();
                        if(userAttendingEventsIds.contains(checkId)){
                            userAttendingEventsList.add(event);
                        }
                    }
                    // Notify RecyclerView adapter of data changes
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });

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
                    //remove from userAttendingEventsList
                    Event swipedLeftEvent = allEventsList.get(position);
                    userAttendingEventsList.remove(swipedLeftEvent);
                    //grab eventId
                    String swipedLeftEventId = swipedLeftEvent.getEventId();
                    //remove from userAttendingEventsIds
                    userAttendingEventsIds.remove(swipedLeftEventId);
                    //remove event ID from firebase "attending" array
                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                DocumentReference userListReference = db.collection("users").document(userId);
                                // Atomically remove eventId to the "attending" array field.
                                userListReference.update("attending", FieldValue.arrayRemove(swipedLeftEventId));
                                if(document.exists()){
                                    Toast.makeText(recyclerView.getContext(), "We will miss you :(", Toast.LENGTH_SHORT).show();
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
