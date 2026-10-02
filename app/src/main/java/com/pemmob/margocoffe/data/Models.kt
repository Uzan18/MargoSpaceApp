package com.pemmob.margocoffe.data

import kotlinx.serialization.Serializable

/**
 * Model class representing a coffee drink.
 * These models will be used by the ViewModel and UI layers.
 * When the backend is ready, the repository can fetch from API
 * and map to these same models.
 */
@Serializable
data class Coffee(
    val id: Int,
    val name: String,
    val price: Int, // Price in Rupiah (e.g. 20000)
    val imageDescription: String, // Placeholder description for the image
    val category: String,
    val isBestSeller: Boolean = false,
    val badge: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val imageRes: Int = 0
)

/**
 * Represents the size option for a drink.
 */
data class SizeOption(
    val label: String,
    val extraPrice: Int // Additional price in Rupiah
)

/**
 * Represents an ice level option.
 */
data class IceLevelOption(
    val label: String
)

/**
 * Represents a sweetness level option.
 */
data class SweetnessOption(
    val label: String
)

/**
 * Represents an item in the shopping cart.
 */
data class CartItem(
    val coffee: Coffee,
    val quantity: Int,
    val selectedSize: SizeOption,
    val selectedIceLevel: IceLevelOption,
    val selectedSweetness: SweetnessOption
) {
    val totalPrice: Int
        get() = (coffee.price + selectedSize.extraPrice) * quantity
}

/**
 * Promo banner data.
 */
data class PromoBanner(
    val id: Int,
    val title: String,
    val subtitle: String,
    val backgroundColor: Long // Color as ARGB long
)

/**
 * Represents a past completed order for the order history tab.
 */
data class PastOrder(
    val id: String,
    val orderNumber: String,
    val date: String,
    val itemsSummary: String,
    val totalPrice: Int,
    val paymentMethod: String,
    val imageUrl: String,
    val status: String = "Selesai",
    val coffeeIdToReorder: Int = 1
)
