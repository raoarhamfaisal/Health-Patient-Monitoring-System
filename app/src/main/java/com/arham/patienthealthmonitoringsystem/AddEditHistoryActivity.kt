package com.arham.patienthealthmonitoringsystem

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.storage.FirebaseStorage
import android.content.Intent
import com.squareup.picasso.Picasso



class AddEditHistoryActivity : BaseActivity() {
    override val REQUEST_CODE_ADD_HISTORY = 1
    private lateinit var etIllness: EditText
    private lateinit var etMedication: EditText
    private lateinit var etDoctorName: EditText
    private lateinit var etDoctorContact: EditText
    private lateinit var btnSave: Button
    private lateinit var ivPrescription: ImageView
    private lateinit var btnSelectImage: Button
    private var imageUri: Uri? = null
    private val storageReference = FirebaseStorage.getInstance().reference

    private val getContent: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            ivPrescription.setImageURI(uri)
            imageUri = uri
        }
    }


    private var historyId: String? = null  // Used for edits

    override fun getLayoutId() = R.layout.activity_add_edit_history

    override fun getToolbarTitle() = if (historyId == null) "Add Medical History" else "Edit Medical History"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setupToolbar()
        setupNotificationBanner()
        etIllness = findViewById(R.id.eIllness)
        etMedication = findViewById(R.id.eMedication)
        etDoctorName = findViewById(R.id.eDoctorName)
        etDoctorContact = findViewById(R.id.eDoctorContact)
        btnSave = findViewById(R.id.btnSave)
        // Initialize views
        ivPrescription = findViewById(R.id.ivPrescription)
        btnSelectImage = findViewById(R.id.btnSelectImage)

        btnSelectImage.setOnClickListener {
            getContent.launch("image/*")
        }
        // Check if we're editing an existing entry
        historyId = intent.getStringExtra("HISTORY_ID")
        if (historyId != null) {
            loadHistoryData(historyId!!)
            supportActionBar?.title = "Edit Medical History"  // Set the title for editing
        } else {
            supportActionBar?.title = "Add Medical History"  // Set the title for adding
        }



        btnSave.setOnClickListener {
            if (historyId == null) {
                saveNewHistory()
            } else {
                updateHistory(historyId!!)
            }

        }
    }

    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
//        supportActionBar?.title =  if (historyId == null) "Add Medical History" else "Edit Medical History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun loadHistoryData(id: String) {
        FirebaseFirestore.getInstance().collection("medicalHistory").document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etIllness.setText(document.getString("illness"))
                    etMedication.setText(document.getString("medication"))
                    etDoctorName.setText(document.getString("doctorName"))
                    etDoctorContact.setText(document.getString("doctorContact"))
                    // Load the image using Picasso
                    val imageUrl = document.getString("prescriptionUrl")
                    if (imageUrl != null && imageUrl.isNotEmpty()) {
                        Picasso.get().load(imageUrl).into(ivPrescription)
                    }
                } else {
                    Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun saveNewHistory() {
        val illness = etIllness.text.toString()
        val medication = etMedication.text.toString()
        val doctorName = etDoctorName.text.toString()
        val doctorContact = etDoctorContact.text.toString()

        imageUri?.let { uri ->
            val imageRef = storageReference.child("prescriptions/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(uri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val data = hashMapOf(
                        "illness" to illness,
                        "medication" to medication,
                        "doctorName" to doctorName,
                        "doctorContact" to doctorContact,
                        "prescriptionUrl" to downloadUri.toString()
                    )
                    FirebaseFirestore.getInstance().collection("medicalHistory").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "History added successfully with image.", Toast.LENGTH_SHORT).show()

                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error adding history: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_LONG).show()
            }
        } ?: run {
            // Save data without image
            val data = hashMapOf(
                "illness" to illness,
                "medication" to medication,
                "doctorName" to doctorName,
                "doctorContact" to doctorContact
            )
            FirebaseFirestore.getInstance().collection("medicalHistory").add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "History added successfully without image.", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding history: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun updateHistory(id: String) {
        imageUri?.let { uri ->
            // Upload the new image if an image has been selected
            val imageRef = storageReference.child("prescriptions/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(uri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Create a map with the updated data including the new image URL
                    val data = hashMapOf(
                        "illness" to etIllness.text.toString(),
                        "medication" to etMedication.text.toString(),
                        "doctorName" to etDoctorName.text.toString(),
                        "doctorContact" to etDoctorContact.text.toString(),
                        "prescriptionUrl" to downloadUri.toString()
                    )
                    // Update the existing document with the new data
                    FirebaseFirestore.getInstance().collection("medicalHistory").document(id).set(data)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Update successful.", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish() // Close the activity and return to the previous screen
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to update history: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        } ?: run {
            // Update the document without changing the image
            val data = hashMapOf(
                "illness" to etIllness.text.toString(),
                "medication" to etMedication.text.toString(),
                "doctorName" to etDoctorName.text.toString(),
                "doctorContact" to etDoctorContact.text.toString()
            )
            FirebaseFirestore.getInstance().collection("medicalHistory").document(id).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Update successful.", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish() // Close the activity and return to the previous screen
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update history: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

    }

}
