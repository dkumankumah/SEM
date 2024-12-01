package com.example.sem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.sem.model.Event;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<Event> eventsList;
    private  RecyclerViewClickListener itemListener;
    private ValueEventListener valueEventListener;
    private ArrayList<String> userAttendingEventIds;
    private ArrayList<String> userFollowingEventIds;

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);
    }

    public recyclerAdapter(ArrayList<Event> eventsList, RecyclerViewClickListener recyclerViewClickListener) {
        this.eventsList = eventsList;
        this.itemListener = recyclerViewClickListener;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventTitle;
        private TextView eventCategory;
        private ImageView rsvpImage;
        public MyViewHolder(final View view){
            super(view);
            eventTitle = view.findViewById(R.id.textview_event_title);
            eventCategory = view.findViewById(R.id.textview_event_category);
            rsvpImage = view.findViewById(R.id.event_status);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_event, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String title = eventsList.get(position).getEventName();
        String category = eventsList.get(position).getEventCategory();
        holder.eventTitle.setText(title);
        holder.eventCategory.setText(category);
        userAttendingEventIds = ShowAllEvents.getAttendingEventIds();
        userFollowingEventIds = ShowAllEvents.getInterestedEventIds();
        if(userAttendingEventIds.contains(eventsList.get(position).getEventId())){
            holder.rsvpImage.setImageResource(R.drawable.baseline_fact_check_24);
        }
        else if(userFollowingEventIds.contains(eventsList.get(position).getEventId())){
            holder.rsvpImage.setImageResource(R.drawable.watching);
        }
    }

    @Override
    public int getItemCount() {
        if(eventsList != null) {
            return eventsList.size();
        }
        return 0;
    }
}

