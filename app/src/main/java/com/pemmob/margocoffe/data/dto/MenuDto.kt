package com.pemmob.margocoffe.data.dto

import com.pemmob.margocoffe.data.Coffee
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuDto(
    val id: String,
    val name: String,
    val price: Int,
    val description: String,

    @SerialName("image_url")
    val imageUrl: String
)

/**
 * Maps API response into the application's domain model.
 *
 * The API intentionally contains only fields provided by MockAPI.
 * UI-specific fields are derived here so the UI does not depend
 * directly on the API structure.
 */
fun MenuDto.toDomain(): Coffee {
    val normalizedName = name.trim().lowercase()

    val category = when {
        normalizedName in setOf(
            "milo",
            "matcha",
            "mango yakult",
            "berry canoe",
            "chocolate margarita"
        ) -> "Non-Coffee"

        else -> "Coffee"
    }

    val isBestSeller = normalizedName in setOf(
        "marko",
        "salted caramel",
        "matcha",
        "mango yakult"
    )

    val badge = when {
        normalizedName == "marko" -> "Signature"
        normalizedName == "salted caramel" -> "Terlaris"
        normalizedName == "matcha" -> "Terlaris"
        normalizedName == "mango yakult" -> "Favorit"
        else -> ""
    }

    return Coffee(
        id = id.toIntOrNull() ?: 0,
        name = name,
        price = price,
        imageDescription = description,
        category = category,
        isBestSeller = isBestSeller,
        badge = badge,
        description = description,
        imageUrl = imageUrl,
        imageRes = 0
    )
}