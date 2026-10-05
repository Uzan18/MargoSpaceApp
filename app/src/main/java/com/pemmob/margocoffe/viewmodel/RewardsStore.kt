package com.pemmob.margocoffe.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RewardVoucher(
    val id: String,
    val title: String,
    val description: String,
    val discountAmount: Int = 0,
    val isFreeCoffee: Boolean = false,
    val isFreeUpSize: Boolean = false
)

data class RewardsData(
    val points: Int = 20,
    val stamps: Int = 0,
    val freeCoffeeRewards: Int = 0,
    val vouchers: List<RewardVoucher> = listOf(
        RewardVoucher(
            id = "vch_welcome_10k",
            title = "Voucher Diskon Rp 10.000",
            description = "Potongan harga Rp 10.000 untuk pesanan di Margo Space",
            discountAmount = 10000
        )
    )
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
                val stampVoucher = RewardVoucher(
                    id = "vch_stamp_${System.currentTimeMillis()}",
                    title = "Gratis 1 Kopi (Stamp Reward)",
                    description = "Reward 10 stamp kopi gratis dari Margo Space",
                    discountAmount = 0,
                    isFreeCoffee = true
                )
                current.copy(
                    points = current.points + points,
                    stamps = 0,
                    freeCoffeeRewards = current.freeCoffeeRewards + 1,
                    vouchers = current.vouchers + stampVoucher
                )
            } else {
                current.copy(
                    points = current.points + points,
                    stamps = newStampCount
                )
            }
        }
    }

    fun redeem(
        pointsRequired: Int,
        title: String = "Voucher Diskon",
        description: String = "Potongan harga pesanan",
        discountAmount: Int = 0,
        isFreeCoffee: Boolean = false,
        isFreeUpSize: Boolean = false
    ): Boolean {
        if (pointsRequired <= 0) return false

        var redeemed = false

        _data.update { current ->
            if (current.points >= pointsRequired) {
                redeemed = true
                val newVoucher = RewardVoucher(
                    id = "vch_${System.currentTimeMillis()}",
                    title = title,
                    description = description,
                    discountAmount = discountAmount,
                    isFreeCoffee = isFreeCoffee,
                    isFreeUpSize = isFreeUpSize
                )
                current.copy(
                    points = current.points - pointsRequired,
                    vouchers = current.vouchers + newVoucher
                )
            } else {
                current
            }
        }

        return redeemed
    }

    fun useVoucher(voucherId: String): Boolean {
        var used = false
        _data.update { current ->
            val found = current.vouchers.any { it.id == voucherId }
            if (found) {
                used = true
                current.copy(
                    vouchers = current.vouchers.filterNot { it.id == voucherId }
                )
            } else {
                current
            }
        }
        return used
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
