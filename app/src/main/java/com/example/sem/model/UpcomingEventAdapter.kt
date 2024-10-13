package com.example.sem.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sem.R

class UpcomingEventAdapter(private val events: List<Event>,
                           private val onItemClick: (Event) -> Unit) :
    RecyclerView.Adapter<UpcomingEventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.upcoming_eventName)
        val eventImage: ImageView = itemView.findViewById(R.id.upcomingevent_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.eventName.text = event.eventName
        holder.eventImage.setImageResource(R.drawable.event1)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    override fun getItemCount() = events.size
}

//class UpcomingEventAdapter(context: Context, events: List<Event>):
//    ArrayAdapter<Event?>(context, R.layout.item_upcoming_event, events) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//
//        var listViewElement = convertView
//        if (listViewElement == null) {
//            val inflater = LayoutInflater.from(context)
//            listViewElement = inflater.inflate(R.layout.item_upcoming_event, parent, false)
//        }
//
//        val event: Event? = getItem(position)
//        val eventName = listViewElement!!.findViewById<TextView>(R.id.upcoming_eventName)
//        val eventImage = listViewElement!!.findViewById<ImageView>(R.id.upcomingevent_image)
//
//        eventName.text =  event?.eventName
//        eventImage.setImageResource(R.drawable.event1)
//
//        return listViewElement
//
//
////        return super.getView(position, convertView, parent)
//    }
//
//}