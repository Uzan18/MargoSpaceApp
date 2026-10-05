package com.pemmob.margocoffe.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser
        get() = auth.currentUser

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<String> {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val user = result.user
                ?: return Result.failure(
                    IllegalStateException(
                        "User berhasil dibuat tetapi data user tidak tersedia."
                    )
                )

            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdate).await()

            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = auth
                .signInWithEmailAndPassword(email, password)
                .await()

            val user = result.user
                ?: return Result.failure(
                    IllegalStateException(
                        "Login berhasil tetapi data user tidak tersedia."
                    )
                )

            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}