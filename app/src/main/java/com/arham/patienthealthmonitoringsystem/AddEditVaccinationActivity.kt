package com.arham.patienthealthmonitoringsystem

import android.app.AlarmManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Locale

class AddEditVaccinationActivity : BaseActivity() {
    override val REQUEST_CODE_ADD_HISTORY = 1
    private lateinit var etVaccineName: EditText
    private lateinit var etVaccineDate: EditText
    private lateinit var etNextDueDate: EditText
    private lateinit var btnSaveVaccine: Button
    override fun getLayoutId() = R.layout.activity_add_edit_vaccination
    private var vaccinationId: String? = null  // Used for edits
    override fun getToolbarTitle() = if (vaccinationId == null) "Add Vaccination Record" else "Edit Vaccination Record"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setupToolbar()
        setupNotificationBanner()

        etVaccineName = findViewById(R.id.etVaccineName)
        etVaccineDate = findViewById(R.id.etVaccineDate)
        etNextDueDate = findViewById(R.id.etNextDueDate)
        btnSaveVaccine = findViewById(R.id.btnSaveVaccine)
        etVaccineDate.setOnClickListener { showDateTimePicker(it as EditText) }
        etNextDueDate.setOnClickListener { showDateTimePicker(it as EditText) }
        vaccinationId = intent.getStringExtra("VACCINATION_ID")
        if (vaccinationId != null) {
            loadVaccinationData(vaccinationId!!)
            supportActionBar?.title = "Edit Vaccination Record"  // Set the title for editing
        } else {
            supportActionBar?.title = "Add Vaccination Record"  // Set the title for adding
        }



        btnSaveVaccine.setOnClickListener {
            if (vaccinationId == null) {
                saveNewVaccination()
            } else {
                updateVaccination(vaccinationId!!)
            }
        }
    }
    private fun scheduleNotification(vaccineDate: String) {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
        val date = sdf.parse(vaccineDate)
        val alarmTime = date.time - AlarmManager.INTERVAL_DAY // 24 hours before

        val intent = Intent(this, AlertReceiver::class.java)
        // Specify FLAG_IMMUTABLE for PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
    }

    private fun showDateTimePicker(editText: EditText) {
        val currentCalendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            val timeCalendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                timeCalendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute)
                val format = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
                editText.setText(format.format(timeCalendar.time))
            }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), false).show()
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
//        supportActionBar?.title =  if (historyId == null) "Add Medical History" else "Edit Medical History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun loadVaccinationData(id: String) {
        FirebaseFirestore.getInstance().collection("vaccinations").document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etVaccineName.setText(document.getString("name"))
                    etVaccineDate.setText(document.getString("date"))
                    etNextDueDate.setText(document.getString("nextDueDate"))
                } else {
                    Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveNewVaccination() {
        val newVaccination = hashMapOf(
            "name" to etVaccineName.text.toString(),
            "date" to etVaccineDate.text.toString(),
            "nextDueDate" to etNextDueDate.text.toString()
        )
        FirebaseFirestore.getInstance().collection("vaccinations")
            .add(newVaccination)
            .addOnSuccessListener {
                Toast.makeText(this, "Vaccination saved successfully", Toast.LENGTH_SHORT).show()
                scheduleNotification(etNextDueDate.text.toString())
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save vaccination: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateVaccination(id: String) {
        val updatedVaccination = hashMapOf(
            "name" to etVaccineName.text.toString(),
            "date" to etVaccineDate.text.toString(),
            "nextDueDate" to etNextDueDate.text.toString()
        )
        FirebaseFirestore.getInstance().collection("vaccinations").document(id)
            .set(updatedVaccination)
            .addOnSuccessListener {
                Toast.makeText(this, "Vaccination updated successfully", Toast.LENGTH_SHORT).show()
                scheduleNotification(etNextDueDate.text.toString())
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update vaccination: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
