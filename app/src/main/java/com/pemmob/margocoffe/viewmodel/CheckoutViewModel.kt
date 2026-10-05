package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.*

data class MargoBranch(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isOpen24Hours: Boolean
)

private val margoBranches = listOf(
    MargoBranch(
        id = "bukateja",
        name = "Margo Kopi - Outlet Bukateja",
        address = "Jl. Purwandaru, Dusun 5, Majasari, Kec. Bukateja, Kabupaten Purbalingga, Jawa Tengah 53382",
        latitude = -7.4270458725381205,
        longitude = 109.42607159175087,
        isOpen24Hours = true
    ),
    MargoBranch(
        id = "bobotsari",
        name = "Margo Kopi - Outlet Bobotsari",
        address = "Jl. Rs. Yosomiharjo, Dusun 4, Bobotsari, Kec. Bobotsari, Kabupaten Purbalingga, Jawa Tengah 53353",
        latitude = -7.3047346483616264,
        longitude = 109.36943487640859,
        isOpen24Hours = true
    ),
    MargoBranch(
        id = "kalimanah",
        name = "MARGO KOPI (KALIMANAH)",
        address = "Jl. Raya Mayjen Sungkono No.52, Dusun 2, Kalimanah Wetan, Kec. Kalimanah, Kabupaten Purbalingga, Jawa Tengah 53371",
        latitude = -7.416068835583694,
        longitude = 109.33940688813439,
        isOpen24Hours = false
    ),
    MargoBranch(
        id = "purbalingga_space",
        name = "Margo Space",
        address = "Jl. D.I Panjaitan, Purbalingga, Purbalingga Lor, Kec. Purbalingga, Kabupaten Purbalingga, Jawa Tengah 53311",
        latitude = -7.386238610311188,
        longitude = 109.3657605821556,
        isOpen24Hours = true
    ),
    MargoBranch(
        id = "letkol_isdiman",
        name = "Margo Kopi",
        address = "J958+367, Jl. Letkol Isdiman, Purbalingga, Purbalingga Lor, Kec. Purbalingga, Kabupaten Purbalingga, Jawa Tengah 53313",
        latitude = -7.3922761911378165,
        longitude = 109.36551019564888,
        isOpen24Hours = true
    )
)

data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val isDineIn: Boolean = true,
    val customerName: String = "",
    val selectedBranch: MargoBranch? = null,
    val distanceToBranchKm: Double? = null,
    val isLocationLoading: Boolean = false,
    val locationError: String? = null
) {
    val branchName: String
        get() = selectedBranch?.name ?: "Lokasi belum ditentukan"

    val subtotal: Int
        get() = cartItems.sumOf { it.totalPrice }

    val tax: Int
        get() = (subtotal * 0.1).toInt()

    val total: Int
        get() = subtotal + tax
}

class CheckoutViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            CartStore.items.collect { items ->
                _uiState.update { it.copy(cartItems = items) }
            }
        }
    }

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.SetDineIn -> {
                _uiState.update { it.copy(isDineIn = event.isDineIn) }
            }

            is CheckoutEvent.SetCustomerName -> {
                _uiState.update { it.copy(customerName = event.name) }
            }

            is CheckoutEvent.RemoveItem -> {
                CartStore.remove(event.index)
            }

            CheckoutEvent.RequestCurrentLocation -> {
                _uiState.update {
                    it.copy(
                        isLocationLoading = true,
                        locationError = null
                    )
                }
            }

            is CheckoutEvent.SetCurrentLocation -> {
                val nearest = margoBranches.minByOrNull { branch ->
                    calculateDistanceKm(
                        event.latitude,
                        event.longitude,
                        branch.latitude,
                        branch.longitude
                    )
                }

                val distance = nearest?.let {
                    calculateDistanceKm(
                        event.latitude,
                        event.longitude,
                        it.latitude,
                        it.longitude
                    )
                }

                _uiState.update {
                    it.copy(
                        selectedBranch = nearest,
                        distanceToBranchKm = distance,
                        isLocationLoading = false,
                        locationError = null
                    )
                }
            }

            is CheckoutEvent.LocationError -> {
                _uiState.update {
                    it.copy(
                        isLocationLoading = false,
                        locationError = event.message
                    )
                }
            }

            CheckoutEvent.ProceedPayment -> {
                // Order dibuat oleh OrderViewModel.
            }
        }
    }

    fun getCartItemCount(): Int = CartStore.count()
}

private fun calculateDistanceKm(
    userLatitude: Double,
    userLongitude: Double,
    branchLatitude: Double,
    branchLongitude: Double
): Double {
    val earthRadiusKm = 6371.0

    val dLat = Math.toRadians(branchLatitude - userLatitude)
    val dLon = Math.toRadians(branchLongitude - userLongitude)

    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(userLatitude)) *
            cos(Math.toRadians(branchLatitude)) *
            sin(dLon / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadiusKm * c
}
