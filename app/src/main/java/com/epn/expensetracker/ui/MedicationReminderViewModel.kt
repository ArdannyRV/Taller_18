package com.epn.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.epn.expensetracker.data.local.MedicationReminderEntity
import com.epn.expensetracker.data.repository.MedicationReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla principal de medicamentos.
 */
class MedicationReminderViewModel(
    private val repository: MedicationReminderRepository,
    recordatorioActivoInicial: Boolean = true,
    horaRecordatorioInicial: Int = 21,
    minutoRecordatorioInicial: Int = 0
) : ViewModel() {

    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre.asStateFlow()

    private val _dosis = MutableStateFlow("")
    val dosis: StateFlow<String> = _dosis.asStateFlow()

    private val _tipoMedicamentoSeleccionado = MutableStateFlow("Pastilla")
    val tipoMedicamentoSeleccionado: StateFlow<String> = _tipoMedicamentoSeleccionado.asStateFlow()

    private val _recordatorioActivo = MutableStateFlow(recordatorioActivoInicial)
    val recordatorioActivo: StateFlow<Boolean> = _recordatorioActivo.asStateFlow()

    private val _horaRecordatorio = MutableStateFlow(horaRecordatorioInicial)
    val horaRecordatorio: StateFlow<Int> = _horaRecordatorio.asStateFlow()

    private val _minutoRecordatorio = MutableStateFlow(minutoRecordatorioInicial)
    val minutoRecordatorio: StateFlow<Int> = _minutoRecordatorio.asStateFlow()

    val medicamentos = repository.todosLosMedicamentos

    val tiposMedicamento = listOf("Pastilla", "Jarabe", "Inyección", "Gotas", "Pomada", "Otro")

    fun actualizarNombre(valor: String) {
        _nombre.value = valor
    }

    fun actualizarDosis(valor: String) {
        _dosis.value = valor
    }

    fun seleccionarTipoMedicamento(tipo: String) {
        _tipoMedicamentoSeleccionado.value = tipo
    }

    fun cambiarEstadoRecordatorio(activo: Boolean) {
        _recordatorioActivo.value = activo
    }

    fun actualizarHoraRecordatorio(hora: Int, minuto: Int) {
        _horaRecordatorio.value = hora
        _minutoRecordatorio.value = minuto
    }

    fun guardarMedicamento() {
        if (_nombre.value.isBlank() || _dosis.value.isBlank()) return

        viewModelScope.launch {
            val nuevoMedicamento = MedicationReminderEntity(
                nombre = _nombre.value.trim(),
                dosis = _dosis.value.trim(),
                tipoMedicamento = _tipoMedicamentoSeleccionado.value
            )
            repository.agregar(nuevoMedicamento)

            _nombre.value = ""
            _dosis.value = ""
        }
    }

    fun eliminarMedicamento(medicamento: MedicationReminderEntity) {
        viewModelScope.launch {
            repository.eliminar(medicamento)
        }
    }
}

class MedicationReminderViewModelFactory(
    private val repository: MedicationReminderRepository,
    private val recordatorioActivo: Boolean,
    private val horaRecordatorio: Int,
    private val minutoRecordatorio: Int
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicationReminderViewModel::class.java)) {
            return MedicationReminderViewModel(
                repository,
                recordatorioActivo,
                horaRecordatorio,
                minutoRecordatorio
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
