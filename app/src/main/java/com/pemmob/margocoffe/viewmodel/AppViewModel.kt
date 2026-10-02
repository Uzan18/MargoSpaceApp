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
    val hasActiveOrder: Boolean = true,
    val orderNumber: String = "Pesanan #042",
    val tableNumber: String = "Meja 07",
    val customerName: String = "Fauzan",
    val branchName: String = "Cabang Margo Space",
    val isDineIn: Boolean = true,
    val items: List<CartItem> = emptyList(),
    val totalAmount: Int = 52800,
    val orderTime: String = "10:15 WIB",
    val estimatedTime: String = "~5-8 menit",
    val statusBadge: String = "Menunggu Dipanggil",
    val statusMessage: String = "Pesanan Berhasil!",
    val statusSubtitle: String = "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir.",
    val pastOrders: List<PastOrder> = emptyList()
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
        initDefaultOrderState()
    }

    private fun initDefaultOrderState() {
        val coffee1 = MockRepository.getCoffeeById(1)
        val coffee2 = MockRepository.getCoffeeById(4)
        val demoItems = if (coffee1 != null && coffee2 != null) {
            listOf(
                CartItem(
                    coffee = coffee1,
                    quantity = 1,
                    selectedSize = MockRepository.sizeOptions[0],
                    selectedIceLevel = MockRepository.iceLevelOptions[0],
                    selectedSweetness = MockRepository.sweetnessOptions[0]
                ),
                CartItem(
                    coffee = coffee2.copy(name = "Velvet Cappuccino"),
                    quantity = 1,
                    selectedSize = MockRepository.sizeOptions[0],
                    selectedIceLevel = MockRepository.iceLevelOptions[0],
                    selectedSweetness = MockRepository.sweetnessOptions[0]
                )
            )
        } else emptyList()

        _orderStatusState.update {
            it.copy(
                hasActiveOrder = true,
                orderNumber = "Pesanan #042",
                tableNumber = "Meja 07",
                customerName = "Fauzan",
                branchName = "Cabang Margo Space",
                items = demoItems,
                totalAmount = 52800,
                orderTime = "10:15 WIB",
                estimatedTime = "~5-8 menit",
                statusBadge = "Menunggu Dipanggil",
                pastOrders = MockRepository.mockPastOrders
            )
        }
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

    fun addMenuItemToCart(coffee: Coffee) {
        val newItem = CartItem(
            coffee = coffee,
            quantity = 1,
            selectedSize = MockRepository.sizeOptions.first(),
            selectedIceLevel = MockRepository.iceLevelOptions.first(),
            selectedSweetness = MockRepository.sweetnessOptions.first()
        )

        _checkoutState.update { current ->
            val existingIndex = current.cartItems.indexOfFirst {
                it.coffee.id == coffee.id &&
                    it.selectedSize == newItem.selectedSize &&
                    it.selectedIceLevel == newItem.selectedIceLevel &&
                    it.selectedSweetness == newItem.selectedSweetness
            }

            if (existingIndex >= 0) {
                val updatedItems = current.cartItems.toMutableList()
                val existing = updatedItems[existingIndex]
                updatedItems[existingIndex] = existing.copy(quantity = existing.quantity + 1)
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
        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()) + " WIB"
        val orderNo = (10..99).random().toString().padStart(2, '0')
        _orderStatusState.update {
            it.copy(
                hasActiveOrder = true,
                orderNumber = "Pesanan #$orderNo",
                customerName = checkout.customerName.ifEmpty { "Fauzan" },
                isDineIn = checkout.isDineIn,
                tableNumber = if (checkout.isDineIn) "Meja 07" else "Take Away",
                branchName = "Cabang Margo Space",
                items = checkout.cartItems,
                totalAmount = checkout.total,
                orderTime = currentTime,
                estimatedTime = "~5-8 menit",
                statusBadge = "Menunggu Dipanggil",
                statusMessage = "Pesanan Berhasil!",
                statusSubtitle = "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir."
            )
        }
        // Reward 10 loyalty points for completing an order
        _homeState.update { it.copy(loyaltyPoints = it.loyaltyPoints + 10) }
        // Clear cart after ordering
        _checkoutState.update { CheckoutUiState() }
    }

    fun reorderCoffee(coffeeId: Int) {
        val coffee = MockRepository.getCoffeeById(coffeeId) ?: return
        addMenuItemToCart(coffee)
    }
}
