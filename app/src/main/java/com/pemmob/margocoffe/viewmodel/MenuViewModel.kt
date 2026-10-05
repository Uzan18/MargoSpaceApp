package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.data.remote.RetrofitClient
import com.pemmob.margocoffe.data.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MenuUiState(
    val coffeeList: UiState<List<Coffee>> = UiState.Loading,
    val selectedCategory: String = "Semua",
    val searchQuery: String = ""
) {
    val allDrinks: List<Coffee>
        get() =
            when (val state = coffeeList) {
                is UiState.Success -> state.data
                else -> emptyList()
            }

    val popularDrinks: List<Coffee>
        get() =
            allDrinks
                .filter { it.isBestSeller }
                .filter {
                    selectedCategory == "Semua" ||
                            it.category.equals(
                                selectedCategory,
                                ignoreCase = true
                            )
                }
                .take(4)

    val bestSellerPerCategory: List<Coffee>
        get() {
            val categories = if (selectedCategory == "Semua") {
                listOf("Coffee", "Non-Coffee")
            } else {
                listOf(selectedCategory)
            }
            return categories.flatMap { cat ->
                val inCat = allDrinks.filter { it.category.equals(cat, ignoreCase = true) }
                val bestSellers = inCat.filter { it.isBestSeller }
                if (bestSellers.isNotEmpty()) bestSellers.take(2) else inCat.take(2)
            }
        }

    val visibleDrinks: List<Coffee>
        get() =
            allDrinks.filter { coffee ->

                val matchesCategory =
                    selectedCategory == "Semua" ||
                            coffee.category.equals(
                                selectedCategory,
                                ignoreCase = true
                            )

                val matchesSearch =
                    searchQuery.isBlank() ||
                            coffee.name.contains(
                                searchQuery,
                                ignoreCase = true
                            ) ||
                            coffee.description.contains(
                                searchQuery,
                                ignoreCase = true
                            )

                matchesCategory && matchesSearch
            }
}

class MenuViewModel : ViewModel() {

    private val menuRepository =
        MenuRepository(
            api = RetrofitClient.menuApi
        )

    private val _uiState =
        MutableStateFlow(MenuUiState())

    val uiState: StateFlow<MenuUiState> =
        _uiState.asStateFlow()

    init {
        loadMenu()
    }

    fun onEvent(event: MenuEvent) {
        when (event) {

            is MenuEvent.SelectCategory -> {
                _uiState.update {
                    it.copy(
                        selectedCategory =
                            event.category
                    )
                }
            }

            is MenuEvent.Search -> {
                _uiState.update {
                    it.copy(
                        searchQuery =
                            event.query
                    )
                }
            }

            MenuEvent.RetryLoad -> {
                loadMenu()
            }
        }
    }

    private fun loadMenu() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    coffeeList =
                        UiState.Loading
                )
            }

            menuRepository
                .getMenu()
                .onSuccess { coffees ->

                    _uiState.update {
                        it.copy(
                            coffeeList =
                                UiState.Success(
                                    coffees
                                )
                        )
                    }
                }
                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            coffeeList =
                                UiState.Error(
                                    error.message
                                        ?: "Gagal memuat menu"
                                )
                        )
                    }
                }
        }
    }
}