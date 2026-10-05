package com.pemmob.margocoffe.viewmodel

sealed interface HomeEvent {

    data object RetryLoad : HomeEvent

    data object OpenRewards : HomeEvent

    data object OpenOrders : HomeEvent

    data object OpenMenu : HomeEvent
}