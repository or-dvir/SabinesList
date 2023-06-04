package com.hotmail.or_dvir.sabinesList

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class MyApplication : Application() {
    private companion object {
        val TAG: String = MyApplication::class.java.simpleName
    }

    private val exceptionHandler = CoroutineExceptionHandler { context, t ->
        Log.d(TAG, "a non-cancellable coroutine with context \"$context\" failed.\n${t.message}")
        Log.e(TAG, t.message, t)
    }

    val scopeThatShouldNotBeCancelled = CoroutineScope(SupervisorJob() + exceptionHandler)
}
