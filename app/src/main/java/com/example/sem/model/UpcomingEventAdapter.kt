package com.example.sem.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sem.R

class UpcomingEventAdapter(
    private var events: List<Event>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<UpcomingEventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View, val onClick: (Event) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.upcoming_eventName)
        val eventImage: ImageView = itemView.findViewById(R.id.upcomingevent_image)
        // Only include these if they are present in your layout
        val eventTitle: TextView? = itemView.findViewById(R.id.eventTitle)
        val eventManager: TextView? = itemView.findViewById(R.id.eventManager)
        private lateinit var currentEvent: Event

        fun bind(event: Event) {
            currentEvent = event
            eventName.text = event.eventName
            eventTitle?.text = event.eventName // Adjust as needed
            eventManager?.text = event.eventManager
            eventImage.setImageResource(R.drawable.placeholderimage)

            itemView.setOnClickListener {
                onClick(currentEvent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upcoming_event, parent, false)
        return EventViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size

    fun updateEvents(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }
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