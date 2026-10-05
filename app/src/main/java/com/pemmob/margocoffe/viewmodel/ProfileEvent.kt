package com.pemmob.margocoffe.viewmodel

sealed interface ProfileEvent {
    data class SaveProfile(
        val name: String,
        val phone: String,
        val email: String,
        val imageUrl: String
    ) : ProfileEvent
}