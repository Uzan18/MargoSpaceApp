package com.pemmob.margocoffe.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileData(
    val name: String = "FAUZAN",
    val phone: String = "081234567890",
    val email: String = "fauzan@margospace.com",
    val imageUrl: String = ""
)

object ProfileStore {
    private val _data = MutableStateFlow(ProfileData())
    val data: StateFlow<ProfileData> = _data.asStateFlow()

    fun updateProfile(
        name: String,
        phone: String,
        email: String,
        imageUrl: String
    ) {
        _data.update {
            it.copy(
                name = name,
                phone = phone,
                email = email,
                imageUrl = imageUrl
            )
        }
    }
}
