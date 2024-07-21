package com.arham.patienthealthmonitoringsystem

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity :BaseActivity() {
    private lateinit var auth: FirebaseAuth
    override val REQUEST_CODE_ADD_HISTORY = 1

    override fun getLayoutId() = R.layout.activity_main

    override fun getToolbarTitle() = "Patient Health Monitoring System"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupNotificationBanner()
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()

        setupButtons()
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnMedicalHistory = findViewById<Button>(R.id.btnMedicalHistory)
        val btnVaccination = findViewById<Button>(R.id.btnVaccination)


        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnMedicalHistory.setOnClickListener {
            startActivity(Intent(this, MedicalHistoryActivity::class.java))
        }

        btnVaccination.setOnClickListener {
            startActivity(Intent(this, VaccinationActivity::class.java))
        }


    }
    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Patient Health Monitoring System"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }
    private fun setupButtons() {
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logoutUser()
        }

    }

    private fun logoutUser() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
