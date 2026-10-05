package com.pemmob.margocoffe.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RewardsData(
    val points: Int = 5,
    val stamps: Int = 5,
    val freeCoffeeRewards: Int = 0
) {
    val membershipTier: MembershipTier
        get() = MembershipTier.fromPoints(points)

    val stampsRemaining: Int
        get() = 10 - stamps
}

enum class MembershipTier(
    val displayName: String,
    val minimumPoints: Int
) {
    BRONZE("Bronze Member", 0),
    SILVER("Silver Member", 50),
    GOLD("Gold Member", 100);

    companion object {
        fun fromPoints(points: Int): MembershipTier =
            entries.lastOrNull { points >= it.minimumPoints } ?: BRONZE
    }
}

object RewardsStore {

    private const val MAX_STAMPS = 10

    private val _data = MutableStateFlow(RewardsData())
    val data: StateFlow<RewardsData> = _data.asStateFlow()

    fun addPoints(points: Int) {
        if (points <= 0) return

        _data.update { current ->
            val newStampCount = current.stamps + 1

            if (newStampCount >= MAX_STAMPS) {
                current.copy(
                    points = current.points + points,
                    stamps = 0,
                    freeCoffeeRewards = current.freeCoffeeRewards + 1
                )
            } else {
                current.copy(
                    points = current.points + points,
                    stamps = newStampCount
                )
            }
        }
    }

    fun redeem(pointsRequired: Int): Boolean {
        if (pointsRequired <= 0) return false

        var redeemed = false

        _data.update { current ->
            if (current.points >= pointsRequired) {
                redeemed = true
                current.copy(points = current.points - pointsRequired)
            } else {
                current
            }
        }

        return redeemed
    }

    fun useFreeCoffeeReward(): Boolean {
        var used = false

        _data.update { current ->
            if (current.freeCoffeeRewards > 0) {
                used = true
                current.copy(
                    freeCoffeeRewards = current.freeCoffeeRewards - 1
                )
            } else {
                current
            }
        }

        return used
    }
}
