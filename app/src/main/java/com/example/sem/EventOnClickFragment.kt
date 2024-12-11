package com.example.sem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sem.model.Event

class EventOnClickFragment : Fragment() {
    private lateinit var mTextViewTitle: TextView
    private lateinit var mTextViewAddress: TextView
    private lateinit var mTextViewEventDate: TextView
    private lateinit var mTextViewTime: TextView
    private lateinit var mTextViewCategory: TextView
    private lateinit var mTextViewDescription: TextView
    private lateinit var selectedEvent: Event
    private lateinit var showMapBtn : Button
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_on_click, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val i = requireActivity().intent

        selectedEvent = i.getSerializableExtra("event") as Event


        mTextViewTitle = view.findViewById(R.id.textview_title)
        mTextViewAddress = view.findViewById<TextView>(R.id.textview_address)
        mTextViewEventDate = view.findViewById<TextView>(R.id.textview_date)
        mTextViewTime = view.findViewById<TextView>(R.id.textview_time)
        mTextViewCategory = view.findViewById<TextView>(R.id.textview_category)
        mTextViewDescription = view.findViewById<TextView>(R.id.textview_description)
        showMapBtn = view.findViewById(R.id.btn_show_on_map)
        imageView = view.findViewById(R.id.imageView)

        val title = selectedEvent.eventName
        val address = selectedEvent.location
        val date = "Date: " + selectedEvent.eventDate
        val time = "Starting at: " + selectedEvent.eventTime
        val category = "Type of Event: " + selectedEvent.eventCategory
        val description = "Details: " + selectedEvent.eventDescription
        imageView.setImageResource(R.drawable.placeholderimage)

        mTextViewTitle.text = title
        mTextViewAddress.text = address
        mTextViewEventDate.text = date
        mTextViewTime.text = time
        mTextViewCategory.text = category
        mTextViewDescription.text = description

        showMapBtn.setOnClickListener {
            val fragment = MapsFragment()
            val bundle = Bundle()
            bundle.putSerializable("ADDRESS", selectedEvent.eventId)
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

}