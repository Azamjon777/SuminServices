package com.example.suminservices

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

//этот сервис можно запускать как хотим, либо startService() либо StartForegroundService()
//этот класс для того чтобы паралленльно не создавалось много потоков. И рнаследуемся от
// IntentService()
class MyIntentService : IntentService(NAME) {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        createNotificationChannel()
        //теперь снизу отображаем уведомление
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onHandleIntent(intent: Intent?) {//этот метод будет выполняться в другом потоке
        log("onHandleIntent")
        for (i in 1..5) {
            Thread.sleep(1000)
            log("Timer $i")
        }
        //после выполнения кода, сервис сам будет остановлен. И нам не унжно вызывать методы
        // stopSelf() или stopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE TAG", "My Intent Service $message")
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
        .setSmallIcon(R.drawable.ic_launcher_background)
        .build()

    companion object {
        const val CHANNEL_ID = "channel_intent_id"
        const val CHANNEL_NAME = "INTENT"
        private const val NOTIFICATION_ID = 1//id никогда не должно == 0
        private const val NAME = "MyIntentService"

        fun newIntent(context: Context): Intent {
            return Intent(context, MyIntentService::class.java)
        }
    }
}