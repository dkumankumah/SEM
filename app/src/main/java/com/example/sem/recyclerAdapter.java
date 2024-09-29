package com.example.sem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    private ArrayList<Event> eventsList;

    public recyclerAdapter(ArrayList<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView eventTitle;
        public MyViewHolder(final View view){
            super(view);
            eventTitle = view.findViewById(R.id.textview_event_title);
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
        String title = eventsList.get(position).getTitle();
        holder.eventTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}
