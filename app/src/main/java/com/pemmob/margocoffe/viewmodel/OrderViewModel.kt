package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.margocoffe.data.CartItem
import com.pemmob.margocoffe.data.MockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val statusSubtitle: String =
        "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir.",
    val pastOrders: List<com.pemmob.margocoffe.data.PastOrder> =
        emptyList()
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
                    customerName =
                        event.customerName,
                    isDineIn =
                        event.isDineIn
                )
            }

            is OrderEvent.Reorder -> {
                reorderCoffee(
                    event.coffeeId
                )
            }
        }
    }

    fun placeOrder(
        customerName: String,
        isDineIn: Boolean
    ) {

        val cartItems =
            CartStore.items.value

        if (cartItems.isEmpty()) {
            return
        }

        val subtotal =
            cartItems.sumOf {
                it.totalPrice
            }

        val tax =
            (subtotal * 0.1).toInt()

        val total =
            subtotal + tax

        val currentTime =
            SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(Date()) + " WIB"

        val orderNo =
            (10..99)
                .random()
                .toString()
                .padStart(2, '0')

        _uiState.value =
            _uiState.value.copy(
                hasActiveOrder = true,
                orderNumber =
                    "Pesanan #$orderNo",
                customerName =
                    customerName.ifEmpty {
                        "Fauzan"
                    },
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
                orderTime = currentTime,
                estimatedTime = "~5-8 menit",
                statusBadge =
                    "Menunggu Dipanggil",
                statusMessage =
                    "Pesanan Berhasil!",
                statusSubtitle =
                    "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir."
            )

        CartStore.clear()

        // Rewards akan kita hubungkan melalui shared RewardsStore.
        RewardsStore.addPoints(10)
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

        val coffee1 =
            MockRepository.getCoffeeById(1)

        val coffee2 =
            MockRepository.getCoffeeById(4)

        val demoItems =
            if (
                coffee1 != null &&
                coffee2 != null
            ) {

                listOf(

                    CartItem(
                        coffee = coffee1,
                        quantity = 1,
                        selectedSize =
                            MockRepository
                                .sizeOptions[0],
                        selectedIceLevel =
                            MockRepository
                                .iceLevelOptions[0],
                        selectedSweetness =
                            MockRepository
                                .sweetnessOptions[0]
                    ),

                    CartItem(
                        coffee =
                            coffee2.copy(
                                name =
                                    "Velvet Cappuccino"
                            ),
                        quantity = 1,
                        selectedSize =
                            MockRepository
                                .sizeOptions[0],
                        selectedIceLevel =
                            MockRepository
                                .iceLevelOptions[0],
                        selectedSweetness =
                            MockRepository
                                .sweetnessOptions[0]
                    )
                )

            } else {
                emptyList()
            }

        _uiState.value =
            _uiState.value.copy(
                hasActiveOrder = true,
                orderNumber =
                    "Pesanan #042",
                tableNumber = "Meja 07",
                customerName = "Fauzan",
                branchName =
                    "Cabang Margo Space",
                items = demoItems,
                totalAmount = 52800,
                orderTime = "10:15 WIB",
                estimatedTime =
                    "~5-8 menit",
                statusBadge =
                    "Menunggu Dipanggil",
                pastOrders =
                    MockRepository.mockPastOrders
            )
    }
}