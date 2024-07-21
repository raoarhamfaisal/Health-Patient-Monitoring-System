package com.arham.patienthealthmonitoringsystem

data class MedicalHistory(
    val id: String,
    val illness: String,
    val medication: String,
    val doctorName: String,
    val doctorContact: String,
    val prescriptionUrl: String  // Add this if it's not already there
)
