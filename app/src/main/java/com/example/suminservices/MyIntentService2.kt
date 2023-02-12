package com.example.suminservices

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log

class MyIntentService2 : IntentService(NAME) {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")

        /*снизу передаем true для того чтобы последний intent успешно доставлялся при перезапуске
        сервиса*/
        setIntentRedelivery(true)
    }

    override fun onHandleIntent(intent: Intent?) {//этот метод будет выполняться в другом потоке
        log("onHandleIntent")
        val page = intent?.getIntExtra(PAGE, 0)
        for (i in 1..5) {
            Thread.sleep(1000)
            log("Timer :$i   page: $page")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(message: String) {
        Log.d("SERVICE TAG", "My Intent Service $message")
    }

    companion object {
        private const val NAME = "MyIntentService"
        private const val PAGE = "page"

        fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyIntentService2::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }
}