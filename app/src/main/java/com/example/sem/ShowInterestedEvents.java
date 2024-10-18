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

import java.util.ArrayList;

public class ShowInterestedEvents extends AppCompatActivity implements recyclerAdapter.RecyclerViewClickListener {
    public ArrayList<Event> interestedEventsList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);
        recyclerView = findViewById(R.id.recycler_view_events);

        interestedEventsList = ShowAllEvents.getFollowingEventsList();

        setAdapter();

        //implement swipe left/right
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackMethod);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback callBackMethod = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    //do left action
                    interestedEventsList.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                    Toast toastLeft = Toast.makeText(recyclerView.getContext(), "TODO: place item on All Events List", Toast.LENGTH_SHORT);
                    toastLeft.show();
                    break;

            }
        }

    };

    public void recyclerViewListClicked(View v, int position) {
        Event selectedEvent = interestedEventsList.get(position);
        Intent intent = new Intent(ShowInterestedEvents.this, EventOnClick.class);
        intent.putExtra("selected_event", selectedEvent);
        startActivity(intent);
    }


    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(interestedEventsList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
