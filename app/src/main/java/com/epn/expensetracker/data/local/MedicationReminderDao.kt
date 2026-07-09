package com.epn.expensetracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Define las operaciones disponibles para la tabla de medicamentos.
 */
@Dao
interface MedicationReminderDao {

    @Query("SELECT * FROM medicamentos ORDER BY fecha DESC")
    fun obtenerTodos(): Flow<List<MedicationReminderEntity>>

    @Query("SELECT * FROM medicamentos ORDER BY fecha DESC LIMIT 1")
    suspend fun obtenerMasReciente(): MedicationReminderEntity?

    @Insert
    suspend fun insertar(medicamento: MedicationReminderEntity)

    @Update
    suspend fun actualizar(medicamento: MedicationReminderEntity)

    @Delete
    suspend fun eliminar(medicamento: MedicationReminderEntity)
}
