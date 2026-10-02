package com.pemmob.margocoffe.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes for the Kopi Royal app.
 * Uses Kotlin Serialization for type-safe argument passing.
 */
sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Menu : Screen()

    @Serializable
    data class Detail(val coffeeId: Int) : Screen()

    @Serializable
    data object Checkout : Screen()

    @Serializable
    data object Rewards : Screen()

    @Serializable
    data object OrderStatus : Screen()
}
