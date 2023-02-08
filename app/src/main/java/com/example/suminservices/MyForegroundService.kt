package com.example.suminservices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MyForegroundService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        createNotificationChannel()
        //теперь снизу отображаем уведомление
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            log("onStart")
            for (i in 0..100) {
                delay(1000)
                log("Timer $i")
            }
            stopSelf()//этот метод останавливает сервис изнутри, то есть в своем какомто потоке
            // а stopService останавливает сервис из активити например(снаружи)
        }
        return START_STICKY
        /*обычно onStartCommand возвращает одно из 3-х следующих комманд:
        return START_STICKY - Сервис начинает работу заного, когда приложение уничтожится
        return START_NOT_STICKY - Сервис, не запустится когда приложение уничтожится
        return START_REDELIVER_INTENT - Сервис продолжит работу с назначенного нами числа
        (например 25 как в нашем случае)*/
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun log(message: String) {
        Log.d("SERVICE TAG", "My Foreground Service $message")
    }

    private fun createNotificationChannel() {
        /*Для каждого notification, должен быть создан свой notificationChannel. Еще создавать
         NotificationChannel требуется начиная с 26 уровня, до этого данный код вызывать не нужно.
         Поэтому необходимо добавить проверку. К примечанию: С добавлением канала, у нас уведомление
         не будет убираться с уведомлений*/
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            //внутрь менеджера кладём канал
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("title")
        .setContentText("text")
        .setSmallIcon(R.drawable.wet)
        .build()

    companion object {
        const val CHANNEL_ID = "channel_foreground_id"
        const val CHANNEL_NAME = "FOREGROUND"
        private const val NOTIFICATION_ID = 1//id никогда не должно == 0

        fun newIntent(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)
        }
    }
}