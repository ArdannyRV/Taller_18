package com.epn.expensetracker.data.repository

import com.epn.expensetracker.data.local.MedicationReminderDao
import com.epn.expensetracker.data.local.MedicationReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio que centraliza el acceso a los datos de medicamentos.
 */
class MedicationReminderRepository(private val dao: MedicationReminderDao) {

    val todosLosMedicamentos: Flow<List<MedicationReminderEntity>> = dao.obtenerTodos()

    suspend fun obtenerMasReciente(): MedicationReminderEntity? {
        return dao.obtenerMasReciente()
    }

    suspend fun agregar(medicamento: MedicationReminderEntity) {
        dao.insertar(medicamento)
    }

    suspend fun actualizar(medicamento: MedicationReminderEntity) {
        dao.actualizar(medicamento)
    }

    suspend fun eliminar(medicamento: MedicationReminderEntity) {
        dao.eliminar(medicamento)
    }
}
