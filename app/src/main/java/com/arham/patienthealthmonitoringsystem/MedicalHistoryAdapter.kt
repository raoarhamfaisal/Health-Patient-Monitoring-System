package com.arham.patienthealthmonitoringsystem

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MedicalHistoryAdapter(
    private val onDelete: (String) -> Unit,
    private val activityResultLauncher: ActivityResultLauncher<Intent> // Add this parameter
) : RecyclerView.Adapter<MedicalHistoryAdapter.HistoryViewHolder>() {
    private var historyList = listOf<MedicalHistory>()

    fun setHistoryList(list: List<MedicalHistory>) {
        historyList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medical_history, parent, false)
        return HistoryViewHolder(view, activityResultLauncher,onDelete)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount() = historyList.size

    class HistoryViewHolder(itemView: View,
                            private val activityResultLauncher: ActivityResultLauncher<Intent>,private val onDelete: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvIllness: TextView = itemView.findViewById(R.id.itemIllness)
        private val tvMedication: TextView = itemView.findViewById(R.id.itemMedication)
        private val tvDoctorName: TextView = itemView.findViewById(R.id.itemDoctorName)
        private val tvDoctorContact: TextView = itemView.findViewById(R.id.itemDoctorContact)
        private val btnDelete: Button = itemView.findViewById(R.id.itembtnDelete)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val ivPrescription: ImageView = itemView.findViewById(R.id.ivPrescription)

        fun bind(history: MedicalHistory) {
            tvIllness.text = history.illness
            tvMedication.text = history.medication
            tvDoctorName.text = history.doctorName
            tvDoctorContact.text = history.doctorContact

            if (history.prescriptionUrl.isNotEmpty()) {
                Picasso.get().load(history.prescriptionUrl).placeholder(R.drawable.placeholder_image).into(ivPrescription)
            } else {
                ivPrescription.setImageResource(R.drawable.placeholder_image) // Fallback image
            }
            btnDelete.setOnClickListener { onDelete(history.id) }
            btnEdit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AddEditHistoryActivity::class.java).apply {
                    putExtra("HISTORY_ID", history.id)
                }
                // Use launcher to start activity
                activityResultLauncher.launch(intent)
            }
        }
    }
}
