package com.arham.patienthealthmonitoringsystem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class VaccinationActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VaccinationAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val db = FirebaseFirestore.getInstance()
    override val REQUEST_CODE_ADD_HISTORY = 1
    override fun getLayoutId() = R.layout.activity_vaccination

    override fun getToolbarTitle() = "Vaccination Records"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNotificationBanner()

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fetchVaccinations()  // Refresh the list because data might have changed
            }
        }

        val fabAddVaccination = findViewById<FloatingActionButton>(R.id.fabAddVaccination)
        recyclerView = findViewById(R.id.recyclerViewVaccinations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Pass resultLauncher when creating adapter
        adapter = VaccinationAdapter( {vaccinationId -> deleteVaccination(vaccinationId) }, resultLauncher)
        recyclerView.adapter = adapter

        fabAddVaccination.setOnClickListener {
            val intent = Intent(this, AddEditVaccinationActivity::class.java)
            resultLauncher.launch(intent)
        }

        fetchVaccinations()
    }

    private fun fetchVaccinations() {
        db.collection("vaccinations")
            .get()
            .addOnSuccessListener { documents ->
                val vaccinations = documents.map { doc ->
                    Vaccination(
                        doc.id,
                        doc.getString("name") ?: "",
                        doc.getString("date") ?: "",
                        doc.getString("nextDueDate") ?: ""
                    )
                }
                adapter.setVaccinations(vaccinations)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching documents: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteVaccination(vaccinationId: String) {
        db.collection("vaccinations").document(vaccinationId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Vaccination deleted", Toast.LENGTH_SHORT).show()
                fetchVaccinations()  // Refresh the list after delete
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting vaccination: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
