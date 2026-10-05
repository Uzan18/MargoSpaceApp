package com.pemmob.margocoffe.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.pemmob.margocoffe.viewmodel.ProfileStore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val currentUser
        get() = auth.currentUser

    init {
        syncCurrentUserProfile()
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    private fun syncCurrentUserProfile() {
        val user = auth.currentUser ?: return
        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val name = doc.getString("name") ?: user.displayName ?: "Teman Margo"
                    val phone = doc.getString("phone") ?: ""
                    val email = doc.getString("email") ?: user.email ?: ""
                    val imageUrl = doc.getString("imageUrl") ?: ""
                    ProfileStore.updateProfile(
                        uid = user.uid,
                        name = name,
                        phone = phone,
                        email = email,
                        imageUrl = imageUrl
                    )
                } else {
                    val name = user.displayName ?: "Teman Margo"
                    val email = user.email ?: ""
                    ProfileStore.updateProfile(
                        uid = user.uid,
                        name = name,
                        phone = "",
                        email = email,
                        imageUrl = ""
                    )
                }
            }
            .addOnFailureListener {
                val name = user.displayName ?: "Teman Margo"
                val email = user.email ?: ""
                ProfileStore.updateProfile(
                    uid = user.uid,
                    name = name,
                    phone = "",
                    email = email,
                    imageUrl = ""
                )
            }
    }

    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String
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

            // Simpan profil user ke Firestore secara non-blocking di latar belakang
            val now = System.currentTimeMillis()
            val userMap = hashMapOf(
                "uid" to user.uid,
                "name" to name,
                "email" to email,
                "phone" to phone,
                "imageUrl" to "",
                "createdAt" to now,
                "updatedAt" to now
            )
            firestore.collection("users").document(user.uid).set(userMap)

            // Update single source of truth ProfileStore segera
            ProfileStore.updateProfile(
                uid = user.uid,
                name = name,
                phone = phone,
                email = email,
                imageUrl = ""
            )

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
                .signInWithEmailAndPassword(email.trim(), password)
                .await()

            val user = result.user
                ?: return Result.failure(
                    IllegalStateException(
                        "Login berhasil tetapi data user tidak tersedia."
                    )
                )

            // Update profil awal dari data auth (langsung responsif tanpa menunggu jaringan Firestore)
            ProfileStore.updateProfile(
                uid = user.uid,
                name = user.displayName?.ifBlank { "Teman Margo" } ?: "Teman Margo",
                phone = "",
                email = user.email ?: email,
                imageUrl = user.photoUrl?.toString() ?: ""
            )

            // Sinkronkan data Firestore secara asinkron di latar belakang
            syncCurrentUserProfile()

            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        ProfileStore.reset()
    }
}