package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.data.PromoBanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val promoBanners: UiState<List<PromoBanner>> = UiState.Loading,
    val userName: String = "Teman Margo"
)

class HomeViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> =
        _uiState.asStateFlow()

    init {
        loadHomeData()
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            ProfileStore.data.collect { profile ->
                _uiState.update {
                    it.copy(userName = profile.name)
                }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {

            HomeEvent.RetryLoad -> {
                loadHomeData()
            }

            HomeEvent.OpenRewards -> {
                // Navigation ditangani oleh UI
            }

            HomeEvent.OpenOrders -> {
                // Navigation ditangani oleh UI
            }

            HomeEvent.OpenMenu -> {
                // Navigation ditangani oleh UI
            }
        }
    }

    private fun loadHomeData() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    promoBanners = UiState.Loading
                )
            }

            try {

                val banners =
                    MockRepository.getPromoBanners()

                _uiState.update {
                    it.copy(
                        promoBanners =
                            UiState.Success(banners)
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        promoBanners =
                            UiState.Error(
                                e.message
                                    ?: "Gagal memuat promo"
                            )
                    )
                }
            }
        }
    }
}