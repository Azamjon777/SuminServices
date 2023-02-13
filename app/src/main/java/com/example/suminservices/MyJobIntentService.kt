package com.example.suminservices

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

/*В JobIntentService можно работать и в API>26 и API<26. Но минус в том, что тут нельзя задавать
ограничения. Этот сервис выполняется в фоновом потоке*/
class MyJobIntentService : JobIntentService() {
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        log("onHandleWork")
        val page = intent.getIntExtra(PAGE, 0)
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
        Log.d("SERVICE TAG", "My JobIntent Service $message")
    }

    companion object {
        private const val PAGE = "page"
        private const val JOB_ID = 123

        fun enqueue(context: Context, page: Int) {
            //enqueueWork это метод класса JobIntentService
            enqueueWork(
                context,
                MyJobIntentService::class.java,
                JOB_ID,
                newIntent(context, page)
            )
        }

        private fun newIntent(context: Context, page: Int): Intent {
            return Intent(context, MyJobIntentService::class.java).apply {
                putExtra(PAGE, page)
            }
        }
    }
}