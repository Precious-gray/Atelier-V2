package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.CalendarContract
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object FittingNotificationHelper {

    private const val CHANNEL_ID = "atelier_fittings_channel"
    private const val CHANNEL_NAME = "Couture Fittings & Appointments"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming bespoke haute couture client fittings"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showFittingScheduledNotification(
        context: Context,
        clientName: String,
        fittingDate: String,
        reminderNote: String = "Reminder scheduled in Atelier archives"
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            clientName.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_atelier_scissors_logo_1792658287419)
            .setContentTitle("Couture Fitting Confirmed: $clientName")
            .setContentText("Scheduled for $fittingDate. $reminderNote")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Atelier Couture Fitting scheduled with $clientName on $fittingDate.\nEnsure bespoke muslin pattern and fabric swatches are prepped in the studio.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(clientName.hashCode(), notification)
    }

    fun openCalendarEvent(context: Context, clientName: String, fittingDate: String, notes: String) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "Atelier Couture Fitting: $clientName")
                putExtra(CalendarContract.Events.DESCRIPTION, "Haute Couture bespoke fitting session.\nNotes: $notes")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "Atelier Haute Couture Studio")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback handled
        }
    }
}
