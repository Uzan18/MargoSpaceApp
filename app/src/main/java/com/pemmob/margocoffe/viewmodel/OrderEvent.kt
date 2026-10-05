package com.pemmob.margocoffe.viewmodel

sealed interface OrderEvent {

    data class PlaceOrder(
        val customerName: String,
        val isDineIn: Boolean,
        val useFreeCoffee: Boolean = false,
        val discountAmount: Int = 0,
        val appliedVoucherId: String? = null
    ) : OrderEvent

    data class Reorder(
        val coffeeId: Int
    ) : OrderEvent

    data object CompleteOrder : OrderEvent
}