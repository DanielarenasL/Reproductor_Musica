package com.harshRajpurohit.musicPlayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Aplicacion : Application() {
    companion object {
        const val CHANNEL_ID = "Canal"
        const val PLAY = "Reproducir"
        const val NEXT = "Siguiente"
        const val PREVIOUS = "Anterior"
        const val EXIT = "Salir"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "Reproduciendo", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "Este es un canal importante para mostrar canciones."

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}