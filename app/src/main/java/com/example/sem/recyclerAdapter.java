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
    private static RecyclerViewClickListener itemListener;


    public interface RecyclerViewClickListener {
        void recyclerViewListClicked(View v, int position);
    }

    public recyclerAdapter(ArrayList<Event> eventsList, RecyclerViewClickListener recyclerViewClickListener) {
        this.eventsList = eventsList;
        this.itemListener = recyclerViewClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventTitle;
        public MyViewHolder(final View view){
            super(view);
            eventTitle = view.findViewById(R.id.textview_event_title);
            view.setOnClickListener(this);
        }
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
        String title = eventsList.get(position).getTitle();
        holder.eventTitle.setText(title);

    }

    @Override
    public int getItemCount() {
        if(eventsList != null) {
            return eventsList.size();
        }
        return 0;
    }

}
