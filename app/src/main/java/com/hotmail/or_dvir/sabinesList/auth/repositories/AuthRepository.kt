package com.hotmail.or_dvir.sabinesList.auth.repositories

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

internal interface AuthRepository {
    /**
     * Emits the current [FirebaseUser]. 
     * Note: You may need to call [FirebaseUser.reload] to get the latest email verification status.
     */
    val authState: StateFlow<FirebaseUser?>
    
    /**
     * Handles both Sign-In and Registration automatically via Firebase.
     */
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    
    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun registerWithEmail(email: String, password: String): Result<Unit>
    suspend fun sendVerificationEmail(): Result<Unit>
    suspend fun logout()
    
    /**
     * Forces a refresh of the current user to get the latest server-side status (e.g. email verification).
     */
    suspend fun reloadUser(): Result<Unit>

    /**
     * Deletes the current user's account from Firebase Auth.
     */
    suspend fun deleteAccount(): Result<Unit>
}
