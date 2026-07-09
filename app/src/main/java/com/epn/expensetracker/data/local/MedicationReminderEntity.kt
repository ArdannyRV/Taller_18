package com.epn.expensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa un recordatorio de medicamento en la base de datos.
 */
@Entity(tableName = "medicamentos")
data class MedicationReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val dosis: String,
    val tipoMedicamento: String,
    val fecha: Long = System.currentTimeMillis()
)
