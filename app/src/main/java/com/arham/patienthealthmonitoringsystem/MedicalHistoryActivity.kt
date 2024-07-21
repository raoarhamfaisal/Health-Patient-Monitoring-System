package com.arham.patienthealthmonitoringsystem
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class MedicalHistoryActivity : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MedicalHistoryAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val db = FirebaseFirestore.getInstance()
    // Define a request code for starting the activity
    override val REQUEST_CODE_ADD_HISTORY = 1
    override fun getLayoutId() = R.layout.activity_medical_history

    override fun getToolbarTitle() = "Medical History"



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        setupNotificationBanner()

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fetchHistory()  // Refresh the list because data might have changed
            }
        }

        val fabAddHistory = findViewById<FloatingActionButton>(R.id.fabAddHistory)
        recyclerView = findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)
// Pass resultLauncher when creating adapter
        adapter = MedicalHistoryAdapter({ historyId -> deleteHistoryEntry(historyId) }, resultLauncher)
        recyclerView.adapter = adapter

        fabAddHistory.setOnClickListener {
            val intent = Intent(this, AddEditHistoryActivity::class.java)
            resultLauncher.launch(intent)
        }


        fetchHistory()
    }



    private fun fetchHistory() {
        db.collection("medicalHistory")
            .get()
            .addOnSuccessListener { result ->
                val historyList = result.map { doc ->
                    MedicalHistory(
                        id = doc.id,
                        illness = doc.getString("illness") ?: "",
                        medication = doc.getString("medication") ?: "",
                        doctorName = doc.getString("doctorName") ?: "",
                        doctorContact = doc.getString("doctorContact") ?: "",
                        prescriptionUrl = doc.getString("prescriptionUrl") ?: "" // Make sure this matches the field name in Firestore
                    )
                }
                adapter.setHistoryList(historyList)
            }
    }


    private fun deleteHistoryEntry(historyId: String) {
        db.collection("medicalHistory").document(historyId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Entry deleted successfully", Toast.LENGTH_SHORT).show()
                fetchHistory()  // Refresh the list
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting entry: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
