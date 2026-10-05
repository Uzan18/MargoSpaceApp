package com.pemmob.margocoffe.viewmodel

sealed interface RewardsEvent {
    data class AddPoints(val points: Int) : RewardsEvent
    data class RedeemReward(
        val pointsRequired: Int,
        val title: String = "Voucher Diskon",
        val description: String = "Potongan harga pesanan",
        val discountAmount: Int = 0,
        val isFreeCoffee: Boolean = false,
        val isFreeUpSize: Boolean = false
    ) : RewardsEvent
    data object UseFreeCoffeeReward : RewardsEvent
}
