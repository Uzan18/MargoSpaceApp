package com.pemmob.margocoffe.viewmodel

sealed interface CheckoutEvent {
    data class SetDineIn(val isDineIn: Boolean) : CheckoutEvent
    data class SetCustomerName(val name: String) : CheckoutEvent
    data class RemoveItem(val index: Int) : CheckoutEvent

    data object RequestCurrentLocation : CheckoutEvent
    data class SetCurrentLocation(
        val latitude: Double,
        val longitude: Double
    ) : CheckoutEvent
    data class LocationError(val message: String) : CheckoutEvent
    data class ToggleUseReward(val use: Boolean) : CheckoutEvent
    data class SelectVoucher(val voucher: RewardVoucher?) : CheckoutEvent
    data class ToggleUsePoints(val use: Boolean) : CheckoutEvent

    data object ProceedPayment : CheckoutEvent
}
