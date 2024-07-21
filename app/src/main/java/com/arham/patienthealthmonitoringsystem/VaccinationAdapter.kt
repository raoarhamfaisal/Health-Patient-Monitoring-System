package com.arham.patienthealthmonitoringsystem

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView

class VaccinationAdapter(
    private val onDelete: (String) -> Unit,
    private val activityResultLauncher: ActivityResultLauncher<Intent> // Add this parameter
) : RecyclerView.Adapter<VaccinationAdapter.VaccinationViewHolder>() {
    private var vaccinations = listOf<Vaccination>()
    fun setVaccinations(list: List<Vaccination>) {
        vaccinations = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vaccination, parent, false)
        return VaccinationViewHolder(view, onDelete,activityResultLauncher)
    }

    override fun onBindViewHolder(holder: VaccinationViewHolder, position: Int) {
        holder.bind(vaccinations[position])
    }

    override fun getItemCount() = vaccinations.size

    class VaccinationViewHolder(
        itemView: View,
        private val onDelete: (String) -> Unit,
        private val activityResultLauncher: ActivityResultLauncher<Intent>
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvVaccineName: TextView = itemView.findViewById(R.id.tvVaccineName)
        private val tvVaccineDate: TextView = itemView.findViewById(R.id.tvVaccineDate)
        private val tvNextDueDate: TextView = itemView.findViewById(R.id.tvNextDueDate)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDelete)
        private val btnEdit: TextView = itemView.findViewById(R.id.btnEdit)

        fun bind(vaccination: Vaccination) {
            tvVaccineName.text = vaccination.name
            tvVaccineDate.text = vaccination.date
            tvNextDueDate.text = vaccination.nextDueDate

            btnDelete.setOnClickListener { onDelete(vaccination.id) }
            btnEdit.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, AddEditVaccinationActivity::class.java).apply {
                    putExtra("VACCINATION_ID", vaccination.id)
                }
                // Use launcher to start activity
                activityResultLauncher.launch(intent)
            }
        }
    }
}
