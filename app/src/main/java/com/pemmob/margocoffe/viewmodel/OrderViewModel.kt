package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.CartItem
import com.pemmob.margocoffe.data.MockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrderStatusUiState(
    val hasActiveOrder: Boolean = false,
    val isSubmitting: Boolean = false,
    val orderNumber: String = "",
    val tableNumber: String = "Meja 07",
    val customerName: String = "",
    val branchName: String = "Cabang Margo Space",
    val isDineIn: Boolean = true,
    val items: List<CartItem> = emptyList(),
    val totalAmount: Int = 0,
    val discountAmount: Int = 0,
    val orderTime: String = "",
    val estimatedTime: String = "~5-8 menit",
    val statusBadge: String = "Menunggu Dipanggil",
    val statusMessage: String = "Pesanan Berhasil!",
    val statusSubtitle: String =
        "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir.",
    val pastOrders: List<com.pemmob.margocoffe.data.PastOrder> =
        com.pemmob.margocoffe.data.MockRepository.mockPastOrders
)

class OrderViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            OrderStatusUiState()
        )

    val uiState: StateFlow<OrderStatusUiState> =
        _uiState.asStateFlow()

    init {
        initDefaultOrderState()
    }

    fun onEvent(event: OrderEvent) {

        when (event) {

            is OrderEvent.PlaceOrder -> {
                placeOrder(
                    customerName = event.customerName,
                    isDineIn = event.isDineIn,
                    useFreeCoffee = event.useFreeCoffee,
                    discountAmount = event.discountAmount,
                    appliedVoucherId = event.appliedVoucherId
                )
            }

            is OrderEvent.Reorder -> {
                reorderCoffee(
                    event.coffeeId
                )
            }

            OrderEvent.CompleteOrder -> {
                completeOrder()
            }
        }
    }

    fun completeOrder() {
        val current = _uiState.value
        if (!current.hasActiveOrder) return

        val firstCoffee = current.items.firstOrNull()?.coffee
        val resolvedCoffee = firstCoffee?.let { c ->
            if (c.imageRes != 0 || c.imageUrl.isNotBlank()) c
            else MockRepository.getCoffeeById(c.id) ?: MockRepository.coffeeMenu.find { it.name.equals(c.name, ignoreCase = true) }
        }
        val resolvedImageRes = if (firstCoffee?.imageRes != null && firstCoffee.imageRes != 0) {
            firstCoffee.imageRes
        } else {
            resolvedCoffee?.imageRes ?: 0
        }
        val resolvedImageUrl = if (!firstCoffee?.imageUrl.isNullOrBlank()) {
            firstCoffee.imageUrl
        } else {
            resolvedCoffee?.imageUrl ?: ""
        }

        val itemsSummary = current.items.joinToString(", ") { "${it.quantity}x ${it.coffee.name}" }.ifBlank { "Pesanan Margo Space" }

        val newPastOrder = com.pemmob.margocoffe.data.PastOrder(
            id = System.currentTimeMillis().toString(),
            orderNumber = current.orderNumber,
            date = current.orderTime,
            itemsSummary = itemsSummary,
            totalPrice = current.totalAmount,
            paymentMethod = "QRIS / Lunas",
            imageUrl = resolvedImageUrl,
            imageRes = resolvedImageRes,
            status = "Selesai",
            coffeeIdToReorder = firstCoffee?.id ?: 1
        )

        _uiState.update {
            it.copy(
                hasActiveOrder = false,
                pastOrders = listOf(newPastOrder) + it.pastOrders
            )
        }
    }

    fun placeOrder(
        customerName: String,
        isDineIn: Boolean,
        useFreeCoffee: Boolean = false,
        discountAmount: Int = 0,
        appliedVoucherId: String? = null
    ) {
        // Cegah pembuatan pesanan ganda (double-click/recomposition)
        if (_uiState.value.isSubmitting) {
            return
        }

        val cartItems =
            CartStore.items.value

        if (cartItems.isEmpty()) {
            return
        }

        _uiState.value = _uiState.value.copy(isSubmitting = true)

        val subtotal =
            cartItems.sumOf {
                it.totalPrice
            }

        val calculatedDiscount = if (discountAmount > 0) {
            discountAmount
        } else if (useFreeCoffee && cartItems.isNotEmpty()) {
            cartItems.minOf { it.coffee.price + it.selectedSize.extraPrice }
        } else {
            0
        }
        val discount = calculatedDiscount.coerceAtMost(subtotal)

        val taxableAmount = (subtotal - discount).coerceAtLeast(0)
        val tax = (taxableAmount * 0.1).toInt()
        val total = taxableAmount + tax

        val currentTime =
            SimpleDateFormat(
                "d MMM yyyy, HH:mm",
                Locale("id", "ID")
            ).format(Date()) + " WIB"

        val orderNo =
            (10..99)
                .random()
                .toString()
                .padStart(2, '0')

        val resolvedCustomerName = customerName.ifBlank {
            ProfileStore.data.value.name.ifBlank { "Pelanggan Margo" }
        }

        val newOrderNumber = "Pesanan #$orderNo"

        _uiState.value =
            _uiState.value.copy(
                hasActiveOrder = true,
                isSubmitting = false,
                orderNumber = newOrderNumber,
                customerName = resolvedCustomerName,
                isDineIn = isDineIn,
                tableNumber =
                    if (isDineIn) {
                        "Meja 07"
                    } else {
                        "Take Away"
                    },
                branchName =
                    "Cabang Margo Space",
                items = cartItems,
                totalAmount = total,
                discountAmount = discount,
                orderTime = currentTime,
                estimatedTime = "~5-8 menit",
                statusBadge =
                    "Menunggu Dipanggil",
                statusMessage =
                    "Pesanan Berhasil!",
                statusSubtitle =
                    "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir."
            )

        // Bersihkan keranjang belanja
        CartStore.clear()

        // Jika menggunakan voucher, kurangi/hapus voucher dari RewardsStore
        if (!appliedVoucherId.isNullOrBlank()) {
            RewardsStore.useVoucher(appliedVoucherId)
        }

        // Jika menggunakan reward kopi gratis, kurangi reward setelah order berhasil
        if (useFreeCoffee) {
            RewardsStore.useFreeCoffeeReward()
        }

        // Tambah poin loyalty rewards
        RewardsStore.addPoints(10)

        // Notifikasi pesanan baru
        NotificationStore.add(
            title = "Pesanan Berhasil Diproses",
            message = "$newOrderNumber sedang disiapkan. Silakan tunggu di kasir.",
            type = NotificationType.ORDER
        )

        // Catat ke Firestore jika user terautentikasi
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val orderMap = hashMapOf(
                        "orderNumber" to newOrderNumber,
                        "customerName" to resolvedCustomerName,
                        "isDineIn" to isDineIn,
                        "totalAmount" to total,
                        "orderTime" to currentTime,
                        "itemCount" to cartItems.size,
                        "createdAt" to System.currentTimeMillis()
                    )
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUser.uid)
                        .collection("orders")
                        .document("order_$orderNo")
                        .set(orderMap)
                        .await()
                } catch (_: Exception) {
                    // Simpanan lokal tetap berhasil
                }
            }
        }
    }

    private fun reorderCoffee(
        coffeeId: Int
    ) {

        viewModelScope.launch {

            val coffee =
                MockRepository.getCoffeeById(
                    coffeeId
                )

            if (coffee != null) {

                val item =
                    CartItem(
                        coffee = coffee,
                        quantity = 1,
                        selectedSize =
                            MockRepository
                                .sizeOptions
                                .first(),
                        selectedIceLevel =
                            MockRepository
                                .iceLevelOptions
                                .first(),
                        selectedSweetness =
                            MockRepository
                                .sweetnessOptions
                                .first()
                    )

                CartStore.add(item)
            }
        }
    }

    private fun initDefaultOrderState() {
        _uiState.value =
            _uiState.value.copy(
                hasActiveOrder = false,
                orderNumber = "",
                customerName = ProfileStore.data.value.name,
                branchName = "Cabang Margo Space",
                items = emptyList(),
                totalAmount = 0,
                pastOrders = MockRepository.mockPastOrders
            )
    }
}