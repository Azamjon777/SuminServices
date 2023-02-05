package com.example.suminservices

import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobWorkItem
import android.content.ComponentName
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.suminservices.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.simpleService.setOnClickListener {
            startService(MyService.newIntent(this, 25))

            //здесь метод для остановления сервиса из presentation слоя
            stopService(MyForegroundService.newIntent(this))
            createNotificationManager()
        }

        binding.foregroundService.setOnClickListener {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(MyForegroundService.newIntent(this))
            }*/
            //можно 2-мя способами сверху и ниже. ContextCompat все за нас сделает
            ContextCompat.startForegroundService(
                this, MyForegroundService.newIntent(
                    this
                )
            )
        }

        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyIntentService.newIntent(this)
            )
        }

        binding.jobScheduler.setOnClickListener {
            val componentName = ComponentName(this, MyJobService::class.java)

            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                //снизу мы передаем номер страницы, в которую хотим загрузить данные

                /*снизу теперь можно вызывать разные методы-ограничители
                например мы хотим чтобы наш сервис работал только на устройстве которое заряжается*/
                .setRequiresCharging(true)//вот таким образом

                //также мы бы хотели чтобы сервис работал только, если устройство подключено к wi-fi
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build()//вызываем в самом конце

            //теперь нужно запланировать выполнение сервиса
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

            /*jobScheduler.schedule(jobInfo)  //чтобы образовать список выполняемых списков, которые
            ждут свою очередь, а не выполняться убивая предыдущих, нужно вызвать schedule()*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = MyJobService.newIntent(page++)
                jobScheduler.enqueue(jobInfo, JobWorkItem(intent))
            }
        }
    }

    private fun createNotificationManager() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(1, createNotification())
    }

    private fun createNotification() = NotificationCompat.Builder(
        this,
        MyForegroundService.CHANNEL_ID
    )
        .setContentTitle("title")
        .setContentText("text")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .build()
}