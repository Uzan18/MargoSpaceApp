package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "FAUZAN",
    val phone: String = "081234567890",
    val email: String = "fauzan@margospace.com",
    val imageUrl: String = ""
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ProfileStore.data.collect { profile ->
                _uiState.update {
                    it.copy(
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
                ProfileStore.updateProfile(
                    name = event.name,
                    phone = event.phone,
                    email = event.email,
                    imageUrl = event.imageUrl
                )
            }
        }
    }
}
