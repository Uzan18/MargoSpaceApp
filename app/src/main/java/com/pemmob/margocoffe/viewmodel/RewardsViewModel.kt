package com.pemmob.margocoffe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RewardsUiState(
    val loyaltyPoints: Int = 5,
    val stamps: Int = 5,
    val freeCoffeeRewards: Int = 0,
    val membershipTier: MembershipTier = MembershipTier.BRONZE,
    val lastRedeemedPoints: Int? = null
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
                if (RewardsStore.redeem(event.pointsRequired)) {
                    _uiState.update {
                        it.copy(lastRedeemedPoints = event.pointsRequired)
                    }
                }
            }

            RewardsEvent.UseFreeCoffeeReward -> {
                RewardsStore.useFreeCoffeeReward()
            }
        }
    }
}
