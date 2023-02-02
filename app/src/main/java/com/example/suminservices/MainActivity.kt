package com.example.suminservices

import android.app.Notification
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.suminservices.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, 25))
        }

        binding.foregroundService.setOnClickListener {
            showNotification()
        }
    }

    private fun showNotification() {
        //это наше уведомление о разрешении пользователю
        val notification = Notification.Builder(this)
            .setContentTitle("Title")
            .setContentText("text")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //теперь снизу отображаем уведомление
        notificationManager.notify(1, notification)
    }
}