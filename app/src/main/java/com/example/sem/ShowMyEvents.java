package com.example.sem;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class ShowMyEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    public ArrayList<Event> myEventsList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_events);
        recyclerView = findViewById(R.id.recycler_view_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myEventsList = ShowAllEvents.getMyEventsList();

        setAdapter();

        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set the initial selected item
//        bottomNav.setSelectedItemId(R.id.nav_account)

        // Navigation bar to all other activities
//        bottomNav.setOnItemSelectedListener { item ->
//                when(item.itemId) {
//            R.id.nav_home -> {
//                // Navigate to AccountActivity
//                startActivity(Intent(this, AdminDashboardActivity::class.java))
//                true
//            }
//            R.id.nav_list -> {
//                // Navigate to MyListActivity
//                true
//            }
//            R.id.nav_following -> {
//                // Navigate to FollowingActivity
//                startActivity(Intent(this, ShowInterestedEvents::class.java))
//                true
//            }
//            R.id.nav_maps -> {
//                // Navigate to Maps
//                true
//            }
//            R.id.nav_account -> {
//                // Navigate to FollowingActivity
//                startActivity(Intent(this, ProfileActivity::class.java))
//                true
//            }
//                else -> false
//            }
//        }

    }

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch(direction){
                case ItemTouchHelper.LEFT:
                    //do left action
                    myEventsList.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    Toast toastLeft = Toast.makeText(recyclerView.getContext(), "You are no longer attending this event :(", Toast.LENGTH_SHORT);
                    toastLeft.show();
                    break;

            }
        }

    };
    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = myEventsList.get(position);
        Intent intent = new Intent(ShowMyEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent);
        startActivity(intent);
    }



    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(myEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
