package com.example.sem

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
import java.util.*

class EventFormActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var eventImage: ImageView
    private lateinit var eventTitle: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventTime: EditText
    private lateinit var eventLocation: EditText
    private lateinit var attendanceDetails: EditText
    private lateinit var eventTypeSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var gradeSpinner: Spinner
    private lateinit var chipGroupGrades: ChipGroup

    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1

//    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_form)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        eventImage = findViewById(R.id.eventImage)
        eventTitle = findViewById(R.id.eventTitle)
        eventDescription = findViewById(R.id.eventDescription)
        eventDate = findViewById(R.id.eventDate)
        eventTime = findViewById(R.id.eventTime)
        eventLocation = findViewById(R.id.eventLocation)
        attendanceDetails = findViewById(R.id.attendanceDetails)
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner)
        submitButton = findViewById(R.id.submitBtn)
        gradeSpinner = findViewById(R.id.gradeSpinner)
        chipGroupGrades = findViewById(R.id.chipGroupGrades)

        val grades = arrayOf("Class 9", "Class 10", "Class 11", "Class 12")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grades)
        var isSpinnerInitialized = false
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeSpinner.adapter = gradeAdapter


        // Image upload
        eventImage.setOnClickListener {
            openImageChooser()
        }

        // Date input
        eventDate.inputType = InputType.TYPE_NULL
        eventDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Time input
        eventTime.inputType = InputType.TYPE_NULL
        eventTime.setOnClickListener {
            showTimePickerDialog()
        }

        gradeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isSpinnerInitialized) {
                    val selectedGrade = parent.getItemAtPosition(position) as String
                    addGradeChip(selectedGrade)
                } else {
                    isSpinnerInitialized = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Submit button click listener
        submitButton.setOnClickListener {
            submitForm()
        }
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dateString = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                eventDate.setText(dateString)
            }, year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                eventTime.setText(timeString)
            }, hour, minute, false
        )
        timePickerDialog.show()
    }

    private fun addGradeChip(grade: String) {
        // Check if the grade is already selected to avoid duplicates
        val isAlreadySelected = (0 until chipGroupGrades.childCount)
            .map { chipGroupGrades.getChildAt(it) as Chip }
            .any { it.text == grade }

        if (!isAlreadySelected) {
            // Create a new Chip
            val chip = Chip(this)
            chip.text = grade
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                chipGroupGrades.removeView(chip) // Remove the chip when 'x' is clicked
            }

            // Add the Chip to the ChipGroup
            chipGroupGrades.addView(chip)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    selectedImageUri = data?.data
                    eventImage.setImageURI(selectedImageUri)
                }
            }
        }
    }

    data class Event(
        val imageUri: Uri?,
        val title: String,
        val description: String,
        val date: String,
        val time: String,
        val location: String,
        val audience: String,
        val attendanceDetails: String,
        val eventType: String
    )

    private fun submitForm() {
        // Gather event details as you did before
        val title = eventTitle.text.toString()
        val description = eventDescription.text.toString()
        val date = eventDate.text.toString()
        val time = eventTime.text.toString()
        val location = eventLocation.text.toString()
        val attendanceDetailsText = attendanceDetails.text.toString()
        val eventType = eventTypeSpinner.selectedItem.toString()

        // Check if all fields are filled
        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()
            || location.isEmpty() || attendanceDetailsText.isEmpty() || eventType.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
        } else {
            // Create an event object to store in Firestore
            val event = hashMapOf(
                "title" to title,
                "description" to description,
                "date" to date,
                "time" to time,
                "location" to location,
                "attendanceDetails" to attendanceDetailsText,
                "eventType" to eventType
            )

            // Add the event to Firestore
//            db.collection("events")
//                .add(event)
//                .addOnSuccessListener { documentReference ->
//                    Log.d("EventForm", "Event added with ID: ${documentReference.id}")
//                    Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show()
//                    finish() // Close the form after submission
//                }
//                .addOnFailureListener { e ->
//                    Log.w("EventForm", "Error adding event", e)
//                    Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show()
//                }
            // Log the event data instead of saving to Firestore
            Log.d("EventForm", "Event Details: $event")
            Toast.makeText(this, "Event details logged", Toast.LENGTH_SHORT).show()

            // You can also print individual details if you prefer
            Log.d("EventForm", "Title: $title")
            Log.d("EventForm", "Description: $description")
            Log.d("EventForm", "Date: $date")
            Log.d("EventForm", "Time: $time")
            Log.d("EventForm", "Location: $location")
            Log.d("EventForm", "Attendance Details: $attendanceDetailsText")
            Log.d("EventForm", "Event Type: $eventType")

            Toast.makeText(this, "Event Created", Toast.LENGTH_LONG).show()

            finish()

        }
    }

}
