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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        recyclerView = findViewById(R.id.recycler_view_events);
        allEventsList = new ArrayList<>();
        userAttendingEventsList = new ArrayList<>();

        // Initialize Firestore and Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();


        allEventsList = ShowAllEvents.getAllEvents();
        userAttendingEventsIds = ShowAllEvents.getAttendingEventIds();

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
                    Toast.makeText(ShowMyEvents.this, "To delete, swipe in the other direction", Toast.LENGTH_SHORT).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    Event swipedRightEvent = userAttendingEventsList.get(position);
//                    //grab eventId
                    String swipedRightEventId = swipedRightEvent.getEventId();
                    //check if eventId is on user's attending array
                    Toast.makeText(ShowMyEvents.this, "Sorry to miss you :(", Toast.LENGTH_SHORT).show();


                    //THIS LINE IS CAUSING AN UNKNOWN CRASH!
                    //userAttendingEventsList.remove(swipedRightEvent);


                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                            DocumentReference userListReference = db.collection("users").document(userId);
                            userListReference.update("attending", FieldValue.arrayRemove(swipedRightEventId));
                        }
                    });
                    recyclerView.getAdapter().notifyItemChanged(position);
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
