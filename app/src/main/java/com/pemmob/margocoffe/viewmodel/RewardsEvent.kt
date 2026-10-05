package com.pemmob.margocoffe.viewmodel

sealed interface RewardsEvent {
    data class AddPoints(val points: Int) : RewardsEvent
    data class RedeemReward(val pointsRequired: Int) : RewardsEvent
    data object UseFreeCoffeeReward : RewardsEvent
}
