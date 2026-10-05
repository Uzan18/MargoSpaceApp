package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RewardsUiState(
    val loyaltyPoints: Int = 0,
    val stamps: Int = 0,
    val freeCoffeeRewards: Int = 0,
    val vouchers: List<RewardVoucher> = emptyList(),
    val membershipTier: MembershipTier = MembershipTier.BRONZE,
    val lastRedeemedPoints: Int? = null,
    val lastRedeemedMessage: String? = null
) {
    val stampsRemaining: Int
        get() = 10 - stamps
}

class RewardsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            RewardsStore.data.collect { rewards ->
                _uiState.update {
                    it.copy(
                        loyaltyPoints = rewards.points,
                        stamps = rewards.stamps,
                        freeCoffeeRewards = rewards.freeCoffeeRewards,
                        vouchers = rewards.vouchers,
                        membershipTier = rewards.membershipTier
                    )
                }
            }
        }
    }

    fun onEvent(event: RewardsEvent) {
        when (event) {
            is RewardsEvent.AddPoints -> {
                RewardsStore.addPoints(event.points)
            }

            is RewardsEvent.RedeemReward -> {
                val success = RewardsStore.redeem(
                    pointsRequired = event.pointsRequired,
                    title = event.title,
                    description = event.description,
                    discountAmount = event.discountAmount,
                    isFreeCoffee = event.isFreeCoffee,
                    isFreeUpSize = event.isFreeUpSize
                )
                if (success) {
                    _uiState.update {
                        it.copy(
                            lastRedeemedPoints = event.pointsRequired,
                            lastRedeemedMessage = "Berhasil menukarkan ${event.title}! Siap digunakan di Checkout."
                        )
                    }
                }
            }

            RewardsEvent.UseFreeCoffeeReward -> {
                RewardsStore.useFreeCoffeeReward()
            }
        }
    }

    fun clearRedeemedMessage() {
        _uiState.update { it.copy(lastRedeemedMessage = null) }
    }
}
