package com.hotmail.or_dvir.sabinesList.database.repositories.firestoreImpl

import android.util.Log
import androidx.annotation.VisibleForTesting
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.hotmail.or_dvir.sabinesList.database.repositories.shouldNotBeCancelled
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

internal class UnauthenticatedException : Exception("User is not logged in.")
internal class CollectionUnavailableException : Exception("Could not access collection.")

internal abstract class BaseFirestoreRepository(
    protected val firestore: FirebaseFirestore,
    protected val auth: FirebaseAuth,
    private val scopeThatShouldNotBeCancelled: CoroutineScope,
    private val dispatcher: CoroutineDispatcher
) {
    protected abstract val logTag: String

    protected fun Throwable.log() {
        val msg = when (this) {
            is UnauthenticatedException -> "[Local Exception] User Unauthenticated"
            is CollectionUnavailableException -> "[Local Exception] Firebase Collection Unavailable"
            is FirebaseFirestoreException -> "[Firestore Exception code: ${this.code}]: $message"
            else -> message
        }
        Log.e(logTag, msg ?: "Unknown error", this)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    internal suspend inline fun doOnCollection(
        crossinline getCollection: () -> CollectionReference?,
        crossinline action: suspend (CollectionReference) -> Task<*>
    ): Result<Unit> = shouldNotBeCancelled(dispatcher, scopeThatShouldNotBeCancelled) {
        if (auth.currentUser == null) {
            val e = UnauthenticatedException()
            e.log()
            return@shouldNotBeCancelled Result.failure(e)
        }

        val collection = getCollection() ?: run {
            val e = CollectionUnavailableException()
            e.log()
            return@shouldNotBeCancelled Result.failure(e)
        }

        try {
            action(collection).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // cancellation exceptions must be propagated
            // so other calling coroutines can properly cancel themselves.
            if (e is CancellationException) throw e

            e.log()
            Result.failure(e)
        }
    }
}
