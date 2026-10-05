package com.pemmob.margocoffe.viewmodel

sealed interface MenuEvent {

    data class SelectCategory(
        val category: String
    ) : MenuEvent

    data class Search(
        val query: String
    ) : MenuEvent

    data object RetryLoad : MenuEvent
}