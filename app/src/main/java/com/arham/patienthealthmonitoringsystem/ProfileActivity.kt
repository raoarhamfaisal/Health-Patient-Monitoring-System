package com.arham.patienthealthmonitoringsystem

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : BaseActivity() {
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etBloodType: EditText
    private lateinit var btnSave: Button
    override val REQUEST_CODE_ADD_HISTORY = 1
    override fun getLayoutId() = R.layout.activity_profile

    override fun getToolbarTitle() = "Profile Management"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setupToolbar()
        setupNotificationBanner()
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etBloodType = findViewById(R.id.etBloodType)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            saveProfileToFirestore()
        }

        fetchProfileFromFirestore()
    }
    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title =  "Profile Management"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }


    private fun saveProfileToFirestore() {
        val name = etName.text.toString()
        val age = etAge.text.toString()
        val bloodType = etBloodType.text.toString()

        if (name.isEmpty() || age.isEmpty() || bloodType.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
            return
        }

        val profile = hashMapOf(
            "name" to name,
            "age" to age,
            "bloodType" to bloodType
        )

        FirebaseFirestore.getInstance().collection("profiles")
            .document("patientProfile")  // Use a unique ID for each patient if multiple profiles are needed
            .set(profile)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun fetchProfileFromFirestore() {
        FirebaseFirestore.getInstance().collection("profiles")
            .document("patientProfile")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    etName.setText(document.getString("name"))
                    etAge.setText(document.getString("age"))
                    etBloodType.setText(document.getString("bloodType"))
                } else {
                    Toast.makeText(this, "No profile found", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
