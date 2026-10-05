package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.*
import com.pemmob.margocoffe.data.remote.RetrofitClient
import com.pemmob.margocoffe.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val coffee: Coffee? = null,
    val selectedSize: SizeOption =
        MockRepository.sizeOptions.first(),
    val selectedIceLevel: IceLevelOption =
        MockRepository.iceLevelOptions.first(),
    val selectedSweetness: SweetnessOption =
        MockRepository.sweetnessOptions.first(),
    val quantity: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalPrice: Int
        get() =
            coffee?.let {
                (it.price + selectedSize.extraPrice) *
                        quantity
            } ?: 0
}

class DetailViewModel : ViewModel() {

    private val menuRepository =
        MenuRepository(
            api = RetrofitClient.menuApi
        )

    private val _uiState =
        MutableStateFlow(DetailUiState())

    val uiState: StateFlow<DetailUiState> =
        _uiState.asStateFlow()

    fun onEvent(event: DetailEvent) {

        when (event) {

            is DetailEvent.SelectSize -> {
                _uiState.update {
                    it.copy(
                        selectedSize =
                            event.size
                    )
                }
            }

            is DetailEvent.SelectIceLevel -> {
                _uiState.update {
                    it.copy(
                        selectedIceLevel =
                            event.iceLevel
                    )
                }
            }

            is DetailEvent.SelectSweetness -> {
                _uiState.update {
                    it.copy(
                        selectedSweetness =
                            event.sweetness
                    )
                }
            }

            DetailEvent.IncrementQuantity -> {
                _uiState.update {
                    it.copy(
                        quantity =
                            it.quantity + 1
                    )
                }
            }

            DetailEvent.DecrementQuantity -> {
                _uiState.update {

                    if (it.quantity > 1) {
                        it.copy(
                            quantity =
                                it.quantity - 1
                        )
                    } else {
                        it
                    }
                }
            }

            DetailEvent.AddToCart -> {
                addToCart()
            }
        }
    }

    fun loadCoffeeDetail(coffeeId: Int) {

        viewModelScope.launch {

            _uiState.update {
                DetailUiState(
                    isLoading = true
                )
            }

            menuRepository
                .getMenu()
                .onSuccess { coffees ->

                    val coffee =
                        coffees.find {
                            it.id == coffeeId
                        }

                    if (coffee != null) {

                        _uiState.update {
                            it.copy(
                                coffee = coffee,
                                isLoading = false,
                                errorMessage = null
                            )
                        }

                    } else {

                        _uiState.update {
                            it.copy(
                                coffee = null,
                                isLoading = false,
                                errorMessage =
                                    "Menu tidak ditemukan"
                            )
                        }
                    }
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            coffee = null,
                            isLoading = false,
                            errorMessage =
                                error.message
                                    ?: "Gagal memuat detail menu"
                        )
                    }
                }
        }
    }

    private fun addToCart() {

        val detail = _uiState.value
        val coffee = detail.coffee ?: return

        val item =
            CartItem(
                coffee = coffee,
                quantity = detail.quantity,
                selectedSize = detail.selectedSize,
                selectedIceLevel =
                    detail.selectedIceLevel,
                selectedSweetness =
                    detail.selectedSweetness
            )

        CartStore.add(item)
    }
}