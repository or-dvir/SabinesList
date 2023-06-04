package com.hotmail.or_dvir.sabinesList.database.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal suspend inline fun <T : Any> shouldNotBeCancelled(
    dispatcher: CoroutineDispatcher,
    scopeThatShouldNotBeCancelled: CoroutineScope,
    crossinline operation: suspend (coroutineScope: CoroutineScope) -> T
): T {
    return withContext(dispatcher) {
        scopeThatShouldNotBeCancelled.async {
            operation(this)
        }.await()
    }
}
