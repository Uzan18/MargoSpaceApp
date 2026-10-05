package com.pemmob.margocoffe.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileData(
    val uid: String = "",
    val name: String = "Teman Margo",
    val phone: String = "",
    val email: String = "",
    val imageUrl: String = ""
)

object ProfileStore {
    private val _data = MutableStateFlow(ProfileData())
    val data: StateFlow<ProfileData> = _data.asStateFlow()

    fun updateProfile(
        uid: String = _data.value.uid,
        name: String,
        phone: String,
        email: String,
        imageUrl: String
    ) {
        _data.update {
            it.copy(
                uid = uid,
                name = name,
                phone = phone,
                email = email,
                imageUrl = imageUrl
            )
        }
    }

    fun reset() {
        _data.value = ProfileData(name = "Teman Margo")
    }
}

