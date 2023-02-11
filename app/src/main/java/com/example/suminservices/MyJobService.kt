package com.example.suminservices

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.*

@SuppressLint("SpecifyJobSchedulerIdRange")
class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    //возвращаемый тип Boolean озночает, выполняется ли наша работа все еще или нет
    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            coroutineScope.launch {
                var workItem = params?.dequeueWork()//здесь из очереди берется 1-ый сервис
                while (workItem != null) {//работаем пока не останется обьектов на очереди
                    val page = workItem.intent.getIntExtra(PAGE, 0)
                    for (i in 1..5) {//здесь в течении 5 секунд будут загружатся данные
                        delay(1000)
                        log("Timer $i. Page is $page")
                    }
                    /*снизу код для того чтобы завершить тот сервис, который выполнялся, а не весь*/
                    params?.completeWork(workItem)
                    workItem = params?.dequeueWork()
                }
                /*//снизу передаем обновление данных сервиса в фоне заного 'true'
            jobFinished(params, true)
            здесь jobFinished обозначает что весь сервис закончил свою работу и никаких
            очередей у нас не осталось, поэтому мы ее убираем. Потому что у нас очереди из
            обьектов. jobFinished вызываем самым последним, после цыкла while()*/
                jobFinished(params, false)/*перезапускать его не будем, так как внутри
                больше ничего не будет, поэтому возвращаем false*/
            }
        }

        /*снизу возвращаем true, потому что в нашем случае наша работа с корутинами еще не закончена
        в других случаях если работа с сервисами закончатся нужно возвращать false*/
        return true
    }

    /* если система убила наш сервис, вызывается метод onStopJob() */
    override fun onStopJob(params: JobParameters?): Boolean {
        log("onStopJob")

        /*если мы хотим чтобы сервис выполнялся, после того как сервис убила наш сервис, то
        возвращаем true*/
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE TAG", "My Job Service $message")
    }

    companion object {
        const val JOB_ID = 7//здесь любое id. Неважно какое

        private const val PAGE = "page"

        /*PersistableBundle это обычный обьект в котором все значения хранятся парами
        можно было использовать Bundle, но PersistableBundle можно клась нечто простое и строки,
        это необходимо для того чтобы данные можно было без проблем считывать с диска*/
        fun newBundle(page: Int): PersistableBundle {
            return PersistableBundle().apply {
                putInt(PAGE, page)
            }
        }

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(PAGE, page)
            }
        }
    }
}