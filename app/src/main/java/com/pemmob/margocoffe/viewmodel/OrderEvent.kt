package com.pemmob.margocoffe.viewmodel

sealed interface OrderEvent {

    data class PlaceOrder(
        val customerName: String,
        val isDineIn: Boolean
    ) : OrderEvent

    data class Reorder(
        val coffeeId: Int
    ) : OrderEvent
}