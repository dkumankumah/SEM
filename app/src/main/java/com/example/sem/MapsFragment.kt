package com.example.sem

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sem.model.Event
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Arrays

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var value: String? = null

    // initialize variables
    private lateinit var mMap: GoogleMap
    private lateinit var db: FirebaseFirestore
    private lateinit var geocoder: Geocoder
    private lateinit var selectedEvent: Event
    private lateinit var events : List<Event>
    private lateinit var allEvents : Button


    private var mFloatingActionButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        value = arguments?.getString("ADDRESS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        // Initialize firestore database
        db = FirebaseFirestore.getInstance()

        // Initialize geocoder
        geocoder = Geocoder(requireContext())

        // initialize view
        setupSearchView(view)
        setupFloatingActionButton(view)

        allEvents = view.findViewById<Button>(R.id.allEvents)

        allEvents.setOnClickListener {
            mMap.clear()
            loadEvents()
            allEvents.setVisibility(View.INVISIBLE)
        }

        allEvents.setVisibility(View.INVISIBLE)

        value?.let { Log.d("VALUE", it) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadEvents()
    }

    private fun loadEvents() {
        // retrieving apartments from database
        db.collection("eventsForTest").get()
            .addOnSuccessListener { documents ->
                events = documents.mapNotNull { it.toObject(Event::class.java) }
                events.forEach { event ->
                    // get geocode for every single apartment and mark it
                    val location = geocodeAddress(event)
                    location?.let {
                        addMarkerForEvent(event, it)
                    }
                }

                // Zoom in the first apartment
                if (events.isNotEmpty()) {
                    selectedEvent = if(value == null) {
                        events[0]
                    } else {
                        events.find { e -> e.eventId == value  }!!;
                    }
                    val location = selectedEvent?.let { geocodeAddress(it) }
                    location?.let {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 15f)
                        mMap.animateCamera(cameraUpdate)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("MapsFragment", "Error getting documents: ", exception)
            }
    }

    // Geocode the giving apartment and return the Latitude and Longitude
    private fun geocodeAddress(event: Event): LatLng? {
        return try {
            val addresses = geocoder.getFromLocationName(event.location, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                LatLng(address.latitude, address.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MapsFragment", "Geocoding failed", e)
            null
        }
    }

    // Adding a marker on the given postition
    private fun addMarkerForEvent(event: Event, position: LatLng) {
        mMap.addMarker(
            MarkerOptions()
            .position(position)
            .title(event.eventName)
            .snippet(event.eventCategory)
        )
    }

    private fun searchLocation(locationName: String) {
        try {
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
            } else {
                Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("MapsFragment", "Search failed", e)
        }
    }

    private fun setupSearchView(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.idSearchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchLocation(it) }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun setupFloatingActionButton(view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val optionsMenu = view.findViewById<View>(R.id.options_menu)
        fab.setOnClickListener {
            optionsMenu.visibility = if (optionsMenu.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        view.findViewById<View>(R.id.option_north_philly).setOnClickListener {
            allEvents.setVisibility(View.VISIBLE)
            val northPhillyZips: ArrayList<Int> = ArrayList()
            northPhillyZips.addAll(
                Arrays.asList(
                    19120,
                    19121,
                    19122,
                    19123,
                    19125,
                    19126,
                    19130,
                    19132,
                    19133,
                    19134,
                    19137,
                    19140,
                    19141
                )
            );

            mMap.clear()

            events.forEach{event ->
                val eventZipCode = extractZipCode(event.location)?.toIntOrNull()
                if (eventZipCode != null && northPhillyZips.contains(eventZipCode)) {
                    geocodeAddress(event)?.let { it1 -> addMarkerForEvent(event, it1) }
                }else{

                }
            }
        }


        view.findViewById<View>(R.id.option_south_philly).setOnClickListener{
            allEvents.setVisibility(View.VISIBLE)
            val southPhillyZips: ArrayList<Any> = ArrayList<Any>()
            southPhillyZips.addAll(
                Arrays.asList(19112, 19145, 19146, 19147, 19148)
            );

            mMap.clear()

            events.forEach{event ->
                val eventZipCode = extractZipCode(event.location)?.toIntOrNull()
                if (eventZipCode != null && southPhillyZips.contains(eventZipCode)) {
                    geocodeAddress(event)?.let { it1 -> addMarkerForEvent(event, it1) }
                }else{

                }
            }


        }

        view.findViewById<View>(R.id.option_west_philly).setOnClickListener {
            allEvents.setVisibility(View.VISIBLE)
            //zoom to 39.973654, -75.226556 radius 3 miles
            val westPhillyZips: ArrayList<Any> = ArrayList<Any>()
            westPhillyZips.addAll(
                Arrays.asList(19104, 19131, 19139, 19142, 19151)
            );
            mMap.clear()

            events.forEach{event ->
                val eventZipCode = extractZipCode(event.location)?.toIntOrNull()
                if (eventZipCode != null && westPhillyZips.contains(eventZipCode)) {
                    geocodeAddress(event)?.let { it1 -> addMarkerForEvent(event, it1) }
                }else{

                }
            }
        }
    }

    private fun extractZipCode(address: String): String? {
        val regex = Regex("\\b\\d{5}\\b")
        val matchResult = regex.find(address)
        return matchResult?.value
    }
}