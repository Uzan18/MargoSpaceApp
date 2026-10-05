package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val uid: String = "",
    val name: String = "Teman Margo",
    val phone: String = "",
    val email: String = "",
    val imageUrl: String = ""
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        viewModelScope.launch {
            ProfileStore.data.collect { profile ->
                _uiState.update {
                    it.copy(
                        uid = profile.uid,
                        name = profile.name,
                        phone = profile.phone,
                        email = profile.email,
                        imageUrl = profile.imageUrl
                    )
                }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.SaveProfile -> {
                val currentUid = auth.currentUser?.uid ?: _uiState.value.uid
                ProfileStore.updateProfile(
                    uid = currentUid,
                    name = event.name,
                    phone = event.phone,
                    email = event.email,
                    imageUrl = event.imageUrl
                )

                // Simpan perubahan ke Firestore jika user terautentikasi
                if (currentUid.isNotBlank()) {
                    viewModelScope.launch {
                        try {
                            val updates = mapOf(
                                "name" to event.name,
                                "phone" to event.phone,
                                "email" to event.email,
                                "imageUrl" to event.imageUrl,
                                "updatedAt" to System.currentTimeMillis()
                            )
                            firestore.collection("users").document(currentUid).set(
                                updates,
                                com.google.firebase.firestore.SetOptions.merge()
                            )
                        } catch (_: Exception) {
                            // Offline fallback sudah tersimpan di ProfileStore
                        }
                    }
                }
            }
        }
    }
}

