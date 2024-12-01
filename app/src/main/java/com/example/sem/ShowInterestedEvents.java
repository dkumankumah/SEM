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

import com.example.sem.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ShowInterestedEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    private ArrayList<Event> allEventsList;
    private ArrayList<Event> userInterestedEventsList;
    private ArrayList<String> userInterestedEventsIds;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        userInterestedEventsList = new ArrayList<>();

        // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();


        allEventsList = ShowAllEvents.getAllEvents();
        userInterestedEventsIds = ShowAllEvents.getInterestedEventIds();

        for(Event event : allEventsList){
            String checkId = event.getEventId();
            if(userInterestedEventsIds.contains(checkId)){
                userInterestedEventsList.add(event);
            }
        }
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
                    recyclerView.getAdapter().notifyItemChanged(position);
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
                    recyclerView.getAdapter().notifyItemChanged(position);
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
}
