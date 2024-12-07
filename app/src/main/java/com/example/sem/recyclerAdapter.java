package com.example.sem;

import static androidx.core.content.ContextCompat.getColor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.sem.model.Event;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
        private TextView eventDate;
        private ImageView rsvpImage;
        private ImageView eventImage;
        private CardView cardView;

        public MyViewHolder(final View view){
            super(view);
            eventTitle = view.findViewById(R.id.textview_event_title);
            eventCategory = view.findViewById(R.id.textview_event_category);
            eventDate = view.findViewById(R.id.textView_event_date);
            rsvpImage = view.findViewById(R.id.event_status);
            eventImage = view.findViewById(R.id.event_image);
            cardView = view.findViewById(R.id.eventCard);
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
        holder.eventDate.setText(eventsList.get(position).getEventDate());
        holder.eventImage.setImageResource(R.drawable.placeholderimage);
        userAttendingEventIds = ShowAllEvents.getAttendingEventIds();
        userFollowingEventIds = ShowAllEvents.getInterestedEventIds();

        if (userAttendingEventIds != null && userFollowingEventIds != null) {
            if (userAttendingEventIds.contains(eventsList.get(position).getEventId())) {
                holder.rsvpImage.setImageResource(R.drawable.baseline_fact_check_24);
            } else if (userFollowingEventIds.contains(eventsList.get(position).getEventId())) {
                holder.rsvpImage.setImageResource(R.drawable.watching);
            }
        }

        // Resolve and set the card background color based on the category
        int backgroundColor;
        switch (category) {
            case "Academics":
                backgroundColor = getColor(holder.itemView.getContext(), R.color.colorAcademics);
                break;
            case "Extracurricular":
                backgroundColor = getColor(holder.itemView.getContext(), R.color.colorExtracurricular);
                break;
            case "Clubs":
                backgroundColor = getColor(holder.itemView.getContext(), R.color.colorClubs);
                break;
            default:
                backgroundColor = getColor(holder.itemView.getContext(), R.color.colorOthers);
                break;
        }
        holder.cardView.setCardBackgroundColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        if(eventsList != null) {
            return eventsList.size();
        }
        return 0;
    }
}

