package com.epn.expensetracker.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.epn.expensetracker.MainActivity
import com.epn.expensetracker.R
import com.epn.expensetracker.data.local.AppDatabase
import com.epn.expensetracker.data.repository.MedicationReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver que se ejecuta cuando la alarma se dispara.
 * Funciona incluso con la app completamente cerrada.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Usar goAsync() para permitir operaciones asíncronas seguras
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Instanciar repositorio temporalmente para obtener el medicamento
                val database = AppDatabase.getInstance(context)
                val repository = MedicationReminderRepository(database.medicationDao())
                val ultimoMedicamento = repository.obtenerMasReciente()

                val nombre = ultimoMedicamento?.nombre ?: "tus medicamentos"
                val dosis = ultimoMedicamento?.dosis ?: "la dosis indicada"

                val mensaje = "Es momento de tomar $nombre - $dosis"

                mostrarNotificacion(context, mensaje)

                // Reprogramar para el día siguiente
                val hora = ReminderPreferences.obtenerHora(context)
                val minuto = ReminderPreferences.obtenerMinuto(context)
                ReminderScheduler.programarRecordatorio(context, hora, minuto)
            } finally {
                // Siempre hay que llamar a finish() al usar goAsync()
                pendingResult.finish()
            }
        }
    }

    private fun mostrarNotificacion(context: Context, mensaje: String) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal (obligatorio desde Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de Medicamentos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios diarios de toma de medicamentos"
                enableVibration(true)
                // Patrón de vibración prolongado
                vibrationPattern = longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000)
                // Usar sonido de alarma
                val alarmSound = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                val audioAttributes = android.media.AudioAttributes.Builder()
                    .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(alarmSound, audioAttributes)
            }
            notificationManager.createNotificationChannel(canal)
        }

        // Intent para abrir la app al tocar la notificación
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmSound = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)

        val notificacion = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💊 Hora de tu medicamento")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000, 500, 1000))
            .build()
            
        // FLAG_INSISTENT repite el sonido y vibración hasta que se atienda
        notificacion.flags = notificacion.flags or android.app.Notification.FLAG_INSISTENT

        notificationManager.notify(NOTIFICATION_ID, notificacion)
    }

    companion object {
        // Cambiamos el ID del canal para asegurar que Android aplique el nuevo sonido y vibración
        const val CHANNEL_ID = "medication_reminder_channel_v2"
        const val NOTIFICATION_ID = 1002
    }
}
