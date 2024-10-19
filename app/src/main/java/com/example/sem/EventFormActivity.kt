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
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class EventFormActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var eventImage: ImageView
    private lateinit var eventTitle: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventDate: EditText
    private lateinit var eventTime: EditText
    private lateinit var eventLocation: EditText
    private lateinit var eventTypeSpinner: Spinner
    private lateinit var submitButton: Button
    private lateinit var gradeSpinner: Spinner
    private lateinit var chipGroupGrades: ChipGroup
    private lateinit var eventManager: EditText
    private lateinit var attendingCount: EditText

    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1

    private val db = Firebase.firestore

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
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner)
        submitButton = findViewById(R.id.submitBtn)
        gradeSpinner = findViewById(R.id.gradeSpinner)
        chipGroupGrades = findViewById(R.id.chipGroupGrades)
        eventManager = findViewById(R.id.eventManager)
        attendingCount = findViewById(R.id.attendingCount)

        // Initialize Firebase DB Connection
        FirebaseApp.initializeApp(this)

        // Set up grades spinner with a prompt
        val grades = arrayOf("Select Grade", "Class 9", "Class 10", "Class 11", "Class 12")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grades)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        gradeSpinner.adapter = gradeAdapter

        // Set up event type spinner with a prompt
        val eventTypes = arrayOf("Select Event Type", "Seminar", "Workshop", "Lecture", "Social", "Other")
        val eventTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        eventTypeSpinner.adapter = eventTypeAdapter

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
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                if (position != 0) {
                    val selectedGrade = parent.getItemAtPosition(position) as String
                    addGradeChip(selectedGrade)
                    // Reset spinner selection to prompt
                    gradeSpinner.setSelection(0)
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

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dateString = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                eventDate.setText(dateString)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
                eventTime.setText(timeString)
            },
            hour,
            minute,
            false
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

    private fun parseDateTimeToTimestamp(dateStr: String, timeStr: String): Date? {
        val dateTimeStr = "$dateStr $timeStr"
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return try {
            sdf.parse(dateTimeStr)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun generateEventId(): String {
        // For simplicity, generate an event ID using current timestamp and a random number
        val timestamp = System.currentTimeMillis()
        val randomNumber = (1000..9999).random()
        return "EVT$randomNumber$timestamp"
    }

    private fun submitForm() {
        // Gather event details
        val title = eventTitle.text.toString()
        val description = eventDescription.text.toString()
        val dateStr = eventDate.text.toString()
        val timeStr = eventTime.text.toString()
        val location = eventLocation.text.toString()
        val eventType = eventTypeSpinner.selectedItem.toString()
        val eventManagerText = eventManager.text.toString()
        val attendingCountText = attendingCount.text.toString()

        // Check if all required fields are filled
        if (title.isEmpty() || description.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()
            || location.isEmpty() || eventManagerText.isEmpty() || attendingCountText.isEmpty()
            || eventTypeSpinner.selectedItemPosition == 0
        ) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        // Parse date and time into a timestamp
        val eventTimestamp = parseDateTimeToTimestamp(dateStr, timeStr)
        if (eventTimestamp == null) {
            Toast.makeText(this, "Invalid date or time format", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate dateCreated timestamp
        val dateCreatedTimestamp = Date()

        // Collect 'forClass' as an array of numbers from the ChipGroup
        val selectedGrades = mutableListOf<Int>()
        for (i in 0 until chipGroupGrades.childCount) {
            val chip = chipGroupGrades.getChildAt(i) as Chip
            val gradeText = chip.text.toString()
            val gradeNumber = gradeText.filter { it.isDigit() }.toIntOrNull()
            if (gradeNumber != null) {
                selectedGrades.add(gradeNumber)
            }
        }

        if (selectedGrades.isEmpty()) {
            Toast.makeText(this, "Please select at least one grade", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert attendingCount to number
        val attendingCountNumber = attendingCountText.toIntOrNull()
        if (attendingCountNumber == null || attendingCountNumber <= 0) {
            Toast.makeText(this, "Attending Count must be a positive number", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate 'eventId'
        val eventId = generateEventId()

        // Create an event object with required fields
        val event = hashMapOf(
            "attendingCount" to attendingCountNumber,
            "dateCreated" to dateCreatedTimestamp,
            "eventDate" to eventTimestamp,
            "eventDescription" to description,
            "eventId" to eventId,
            "eventManager" to eventManagerText,
            "eventName" to title,
            "forClass" to selectedGrades,
            "location" to location,
            "eventType" to eventType
        )

        // Upload image if selected
        if (selectedImageUri != null) {
            // Show a progress dialog or indicator here if needed

            // Upload the image to Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("event_images/$eventId.jpg")
            val uploadTask = imageRef.putFile(selectedImageUri!!)

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnSuccessListener {
                // Get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Include imageUrl in event data
                    event["eventImageUrl"] = imageUrl

                    // Now proceed to add the event to Firestore
                    addEventToFirestore(event)
                }.addOnFailureListener { exception ->
                    Log.w("EventForm", "Failed to get download URL", exception)
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.w("EventForm", "Image upload failed", exception)
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            // No image selected, proceed to add event without imageUrl
            addEventToFirestore(event)
        }
    }

    private fun addEventToFirestore(event: HashMap<String, Any>) {
        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                Log.d("EventForm", "Event added with ID: ${documentReference.id}")
                Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show()
                finish() // Close the form after submission
            }
            .addOnFailureListener { e ->
                Log.w("EventForm", "Error adding event", e)
                Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show()
            }
    }
}
