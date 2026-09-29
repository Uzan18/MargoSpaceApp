package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─── UI State Sealed Classes ────────────────────────────────────────
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ─── Home Screen State ──────────────────────────────────────────────
data class HomeUiState(
    val coffeeList: UiState<List<Coffee>> = UiState.Loading,
    val promoBanners: UiState<List<PromoBanner>> = UiState.Loading,
    val userName: String = "FAUZAN",
    val loyaltyPoints: Int = 5
)

// ─── Detail Screen State ────────────────────────────────────────────
data class DetailUiState(
    val coffee: Coffee? = null,
    val selectedSize: SizeOption = MockRepository.sizeOptions.first(),
    val selectedIceLevel: IceLevelOption = MockRepository.iceLevelOptions.first(),
    val selectedSweetness: SweetnessOption = MockRepository.sweetnessOptions.first(),
    val quantity: Int = 1
) {
    val totalPrice: Int
        get() = coffee?.let { (it.price + selectedSize.extraPrice) * quantity } ?: 0
}

// ─── Checkout Screen State ──────────────────────────────────────────
data class CheckoutUiState(
    val cartItems: List<CartItem> = emptyList(),
    val isDineIn: Boolean = true,
    val customerName: String = "",
    val branchName: String = "Purbalingga"
) {
    val subtotal: Int
        get() = cartItems.sumOf { it.totalPrice }
    val tax: Int
        get() = (subtotal * 0.1).toInt() // PPN 10%
    val total: Int
        get() = subtotal + tax
}

// ─── Order Status State ─────────────────────────────────────────────
data class OrderStatusUiState(
    val orderNumber: String = "#042",
    val customerName: String = "FAUZAN",
    val statusMessage: String = "Kopi kamu sedang diracik!",
    val statusSubtitle: String = "Silakan duduk santai. Barista kami akan memanggil namamu saat pesanan sudah siap."
)

/**
 * Main ViewModel for the Kopi Royal App.
 *
 * Manages all UI states using StateFlow for reactive, unidirectional data flow.
 * Currently uses MockRepository — when backend is ready, simply swap
 * the repository calls inside the load/fetch methods.
 */
class AppViewModel : ViewModel() {

    // ─── Home State ─────────────────────────────────────────────────
    private val _homeState = MutableStateFlow(HomeUiState())
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    // ─── Detail State ───────────────────────────────────────────────
    private val _detailState = MutableStateFlow(DetailUiState())
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    // ─── Checkout State ─────────────────────────────────────────────
    private val _checkoutState = MutableStateFlow(CheckoutUiState())
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    // ─── Order Status State ─────────────────────────────────────────
    private val _orderStatusState = MutableStateFlow(OrderStatusUiState())
    val orderStatusState: StateFlow<OrderStatusUiState> = _orderStatusState.asStateFlow()

    init {
        loadHomeData()
    }

    // ═══════════════════════════════════════════════════════════════
    // HOME
    // ═══════════════════════════════════════════════════════════════

    fun loadHomeData() {
        viewModelScope.launch {
            _homeState.update { it.copy(coffeeList = UiState.Loading, promoBanners = UiState.Loading) }
            try {
                val banners = MockRepository.getPromoBanners()
                _homeState.update { it.copy(promoBanners = UiState.Success(banners)) }
            } catch (e: Exception) {
                _homeState.update { it.copy(promoBanners = UiState.Error(e.message ?: "Gagal memuat promo")) }
            }
            try {
                val coffees = MockRepository.getCoffeeMenu()
                _homeState.update { it.copy(coffeeList = UiState.Success(coffees)) }
            } catch (e: Exception) {
                _homeState.update { it.copy(coffeeList = UiState.Error(e.message ?: "Gagal memuat menu")) }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // DETAIL
    // ═══════════════════════════════════════════════════════════════

    fun loadCoffeeDetail(coffeeId: Int) {
        val coffee = MockRepository.getCoffeeById(coffeeId)
        _detailState.update {
            DetailUiState(
                coffee = coffee,
                selectedSize = MockRepository.sizeOptions.first(),
                selectedIceLevel = MockRepository.iceLevelOptions.first(),
                selectedSweetness = MockRepository.sweetnessOptions.first(),
                quantity = 1
            )
        }
    }

    fun selectSize(size: SizeOption) {
        _detailState.update { it.copy(selectedSize = size) }
    }

    fun selectIceLevel(iceLevel: IceLevelOption) {
        _detailState.update { it.copy(selectedIceLevel = iceLevel) }
    }

    fun selectSweetness(sweetness: SweetnessOption) {
        _detailState.update { it.copy(selectedSweetness = sweetness) }
    }

    fun incrementQuantity() {
        _detailState.update { it.copy(quantity = it.quantity + 1) }
    }

    fun decrementQuantity() {
        _detailState.update {
            if (it.quantity > 1) it.copy(quantity = it.quantity - 1) else it
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // CART / CHECKOUT
    // ═══════════════════════════════════════════════════════════════

    fun addToCart() {
        val detail = _detailState.value
        val coffee = detail.coffee ?: return

        val newItem = CartItem(
            coffee = coffee,
            quantity = detail.quantity,
            selectedSize = detail.selectedSize,
            selectedIceLevel = detail.selectedIceLevel,
            selectedSweetness = detail.selectedSweetness
        )

        _checkoutState.update { current ->
            // Check if same coffee with same options exists
            val existingIndex = current.cartItems.indexOfFirst {
                it.coffee.id == coffee.id &&
                        it.selectedSize == detail.selectedSize &&
                        it.selectedIceLevel == detail.selectedIceLevel &&
                        it.selectedSweetness == detail.selectedSweetness
            }

            if (existingIndex >= 0) {
                val updatedItems = current.cartItems.toMutableList()
                val existing = updatedItems[existingIndex]
                updatedItems[existingIndex] = existing.copy(quantity = existing.quantity + detail.quantity)
                current.copy(cartItems = updatedItems)
            } else {
                current.copy(cartItems = current.cartItems + newItem)
            }
        }
    }

    fun removeFromCart(index: Int) {
        _checkoutState.update { current ->
            val updatedItems = current.cartItems.toMutableList()
            if (index in updatedItems.indices) {
                updatedItems.removeAt(index)
            }
            current.copy(cartItems = updatedItems)
        }
    }

    fun setDineIn(isDineIn: Boolean) {
        _checkoutState.update { it.copy(isDineIn = isDineIn) }
    }

    fun setCustomerName(name: String) {
        _checkoutState.update { it.copy(customerName = name) }
    }

    fun getCartItemCount(): Int {
        return _checkoutState.value.cartItems.sumOf { it.quantity }
    }

    // ═══════════════════════════════════════════════════════════════
    // ORDER
    // ═══════════════════════════════════════════════════════════════

    fun placeOrder() {
        val checkout = _checkoutState.value
        _orderStatusState.update {
            it.copy(
                customerName = checkout.customerName.uppercase().ifEmpty { "FAUZAN" },
                orderNumber = "#${(40..99).random().toString().padStart(3, '0')}"
            )
        }
        // Clear cart after ordering
        _checkoutState.update { CheckoutUiState() }
    }
}
