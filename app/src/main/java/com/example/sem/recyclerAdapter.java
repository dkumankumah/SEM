package com.example.sem;

import static android.view.View.INVISIBLE;
import static androidx.core.content.ContextCompat.getColor;

import android.content.Intent;
import android.util.Log;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<Event> eventsList;
    private  RecyclerViewClickListener itemListener;
    private ValueEventListener valueEventListener;
    private ArrayList<String> userAttendingEventIds;
    private ArrayList<String> userFollowingEventIds;
    private String eventsListType;
    private String userRole;

    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);

        void deleteEvent(@NotNull Event currentEvent, int adapterPosition);
    }

    public recyclerAdapter(ArrayList<Event> eventsList, RecyclerViewClickListener recyclerViewClickListener, String eventListType, String userRole) {
        this.eventsList = eventsList;
        this.itemListener = recyclerViewClickListener;
        this.eventsListType = eventListType;
        this.userRole = userRole;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventTitle;
        private TextView eventCategory;
        private TextView eventDate;
        private ImageView rsvpImage;
        private ImageView eventImage;
        private CardView cardView;
        private ImageView editButton;
        private ImageView deleteButton;


        public MyViewHolder(final View view){
            super(view);
            eventTitle = view.findViewById(R.id.textview_event_title);
            eventCategory = view.findViewById(R.id.textview_event_category);
            eventDate = view.findViewById(R.id.textView_event_date);
            rsvpImage = view.findViewById(R.id.event_status);
            eventImage = view.findViewById(R.id.event_image);
            cardView = view.findViewById(R.id.eventCard);

            editButton = view.findViewById(R.id.edit_button);
            deleteButton = view.findViewById(R.id.delete_button);
            view.setOnClickListener(this);

            if(userRole.equals("STUDENT")) {
                editButton.setVisibility(INVISIBLE);
                deleteButton.setVisibility(INVISIBLE);
            }
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
        Event currentEvent = eventsList.get(position);
        String title = eventsList.get(position).getEventName();
        String category = eventsList.get(position).getEventCategory();
        holder.eventTitle.setText(title);
        holder.eventCategory.setText(category);
        holder.eventDate.setText(eventsList.get(position).getEventDate());
        holder.eventImage.setImageResource(R.drawable.placeholderimage);


        if(Objects.equals(eventsListType, "myEvents")) {
            holder.rsvpImage.setImageResource(R.drawable.baseline_fact_check_24);
        }
        else if(Objects.equals(eventsListType, "interested")) {
            holder.rsvpImage.setImageResource(R.drawable.watching);
        }
        else {
            userAttendingEventIds = ShowAllEvents.getAttendingEventIds();
            userFollowingEventIds = ShowAllEvents.getInterestedEventIds();

            if (userAttendingEventIds != null && userFollowingEventIds != null) {
                if (userAttendingEventIds.contains(eventsList.get(position).getEventId())) {
                    holder.rsvpImage.setImageResource(R.drawable.baseline_fact_check_24);
                } else if (userFollowingEventIds.contains(eventsList.get(position).getEventId())) {
                    holder.rsvpImage.setImageResource(R.drawable.watching);
                }
            }
        }

        AdminDashboardActivity adminDashboardActivity = new AdminDashboardActivity();
        String userRole = adminDashboardActivity.getRole();
        if(userRole.equals("ADMIN")) {

            // Set up delete button click listener
            holder.deleteButton.setOnClickListener(v -> {
                ShowAllEvents activity = (ShowAllEvents) v.getContext();
                activity.deleteEvent(currentEvent, holder.getAdapterPosition());
            });

            // Set up edit button click listener
            holder.editButton.setOnClickListener(v -> {
                ShowAllEvents activity = (ShowAllEvents) v.getContext();
                Intent intent = new Intent(activity, EventFormActivity.class);
                intent.putExtra("docId", currentEvent.getDocId());
                intent.putExtra("event", currentEvent);
                activity.startActivity(intent);
            });

        }

        // Resolve and set the card background color based on the category
        int backgroundColor;
        switch (category) {
            case "Academics":
                backgroundColor = getColor(holder.itemView.getContext(), R.color.colorAcademics);
                break;
            case "Athletics":
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

