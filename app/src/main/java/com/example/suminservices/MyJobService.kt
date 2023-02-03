package com.example.suminservices

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    //возвращаемый тип Boolean озночает, выполняется ли наша работа все еще или нет
    override fun onStartJob(params: JobParameters?): Boolean {
        coroutineScope.launch {
            for (i in 1..100) {
                delay(1000)
                log("onStartCommand")
            }
            //снизу передаем обновление данных сервиса в фоне заного 'true'
            jobFinished(params, true)
        }
        /*снизу возвращаем true, потому что в нашем случае наша работа с корутинами еще не закончена
        в других случаях если работа с сервисами закончатся нужно возвращать false*/
        return true
    }

    /* если система убила наш сервис, вызывается метод onStopJob(), а если мы сами остановили сервис
    с помощью метода jobFinished(), то метод onStopJob() */
    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")

        /*если мы хотим чтобы сервис выполнялся, после того как сервис убила наш сервис, то
        возвращаем true*/
        return true
    }


    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE TAG", "My Job Service $message")
    }
}