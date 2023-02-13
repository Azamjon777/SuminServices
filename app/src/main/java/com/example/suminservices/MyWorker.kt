package com.example.suminservices

import android.content.Context
import android.util.Log
import androidx.work.*

class MyWorker(context: Context, private val workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    //doWork выполняется по умолчанию в другом потоке
    override fun doWork(): Result {
        log("doWork")
        //workerParameters.inputData.getInt(PAGE, 0)
        for (i in 0..5) {
            Thread.sleep(1000)
            log("Timer $i")
        }
        return Result.success()
    }

    private fun log(message: String) {
        Log.d("SERVICE TAG", "MyWorkerService, $message")
    }

    companion object {
        private const val PAGE = "page"
        const val WORK_NAME = "work name"

        fun myRequest(page: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<MyWorker>().apply {
                setInputData(
                    workDataOf(PAGE to page)//здесь создается обьект Pair(ключ, значение)
                )
                setConstraints(makeConstraints())
            }.build()   //здесь создается request
        }

        private fun makeConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiresCharging(true)
                .build()
        }
    }
}