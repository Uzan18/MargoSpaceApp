package com.pemmob.margocoffe.data

import com.pemmob.margocoffe.R

/**
 * Mock Repository providing dummy data for the Kopi Royal app.
 *
 * BACKEND INTEGRATION NOTE:
 * When the backend is ready, replace this class with a real repository
 * that calls your REST API via Retrofit/Ktor. The ViewModel only depends
 * on the data returned, not on HOW it's fetched, making the swap seamless.
 */
object MockRepository {

    // ─── Size Options ───────────────────────────────────────────────
    val sizeOptions = listOf(
        SizeOption(label = "Reguler", extraPrice = 0),
        SizeOption(label = "Besar", extraPrice = 4000)
    )

    // ─── Ice Level Options ──────────────────────────────────────────
    val iceLevelOptions = listOf(
        IceLevelOption(label = "Normal"),
        IceLevelOption(label = "Sedikit Es"),
        IceLevelOption(label = "Tanpa Es")
    )

    // ─── Sweetness Options ──────────────────────────────────────────
    val sweetnessOptions = listOf(
        SweetnessOption(label = "Normal"),
        SweetnessOption(label = "Kurang Manis"),
        SweetnessOption(label = "Tanpa Gula")
    )

    // ─── Promo Banners ──────────────────────────────────────────────
    val promoBanners = listOf(
        PromoBanner(
            id = 1,
            title = "Diskon 30%",
            subtitle = "Untuk semua minuman es kopi setiap Senin!",
            backgroundColor = 0xFF1565C0
        ),
        PromoBanner(
            id = 2,
            title = "Beli 2 Gratis 1",
            subtitle = "Promo spesial akhir pekan di Margo Space",
            backgroundColor = 0xFF6A1B9A
        ),
        PromoBanner(
            id = 3,
            title = "Member Baru?",
            subtitle = "Daftar sekarang & dapatkan 50 poin gratis!",
            backgroundColor = 0xFF00695C
        )
    )

    // ─── Coffee Menu (Margo Space Official Menu) ───────────────────
    val coffeeMenu = listOf(
        Coffee(
            id = 1,
            name = "Marko",
            price = 17000,
            imageDescription = "Signature iced coffee blend Margo Space creamy",
            category = "Coffee",
            isBestSeller = true,
            badge = "Signature",
            description = "Signature blend kopi Margo Space berpadu susu creamy istimewa",
            imageUrl = "",
            imageRes = R.drawable.marko
        ),
        Coffee(
            id = 2,
            name = "Salted Caramel",
            price = 20000,
            imageDescription = "Espresso based dengan salted caramel",
            category = "Coffee",
            isBestSeller = true,
            badge = "Terlaris",
            description = "Espresso based dengan sirup salted caramel gurih manis",
            imageUrl = "",
            imageRes = R.drawable.salted_caramel
        ),
        Coffee(
            id = 3,
            name = "Butterscotch",
            price = 20000,
            imageDescription = "Espresso based dengan aroma butterscotch kaya",
            category = "Coffee",
            isBestSeller = false,
            badge = "Favorit",
            description = "Espresso kaya rasa dengan aroma mentega karamel butterscotch",
            imageUrl = "",
            imageRes = R.drawable.butterscotch
        ),
        Coffee(
            id = 4,
            name = "Oats Margo",
            price = 22000,
            imageDescription = "Espresso dengan oatmilk pilihan khas Margo",
            category = "Coffee",
            isBestSeller = true,
            badge = "Rekomendasi",
            description = "Espresso nikmat berpadu creamy oatmilk premium khas Margo",
            imageUrl = "",
            imageRes = R.drawable.oats_margo
        ),
        Coffee(
            id = 5,
            name = "Americano",
            price = 15000,
            imageDescription = "Espresso murni dingin segar",
            category = "Coffee",
            isBestSeller = false,
            badge = "",
            description = "Espresso shot murni segar dengan karakter rasa kopi bold",
            imageUrl = "",
            imageRes = R.drawable.americano
        ),
        Coffee(
            id = 6,
            name = "Chillberry",
            price = 18000,
            imageDescription = "Perpaduan kopi espresso dan buah berry dingin",
            category = "Coffee",
            isBestSeller = false,
            badge = "Spesial",
            description = "Sensasi kopi espresso berpadu kesegaran buah berry manis dingin",
            imageUrl = "",
            imageRes = R.drawable.chillberry
        ),
        Coffee(
            id = 7,
            name = "Chocomargo",
            price = 20000,
            imageDescription = "Kombinasi coklat pekat dan espresso khas Margo",
            category = "Coffee",
            isBestSeller = false,
            badge = "",
            description = "Kombinasi mantap antara coklat pekat dan espresso khas Margo",
            imageUrl = "",
            imageRes = R.drawable.chocomargo
        ),
        Coffee(
            id = 9,
            name = "Milo",
            price = 15000,
            imageDescription = "Coklat malt Milo dingin segar",
            category = "Non-Coffee",
            isBestSeller = false,
            badge = "",
            description = "Minuman coklat malt Milo dingin yang manis dan menyegarkan",
            imageUrl = "",
            imageRes = R.drawable.milo
        ),
        Coffee(
            id = 10,
            name = "Matcha",
            price = 18000,
            imageDescription = "Matcha hijau autentik dengan susu segar creamy",
            category = "Non-Coffee",
            isBestSeller = true,
            badge = "Terlaris",
            description = "Matcha hijau autentik berpadu susu segar dingin yang creamy",
            imageUrl = "",
            imageRes = R.drawable.matcha
        ),
        Coffee(
            id = 11,
            name = "Mango Yakult",
            price = 18000,
            imageDescription = "Sari mangga segar berpadu Yakult asam manis",
            category = "Non-Coffee",
            isBestSeller = true,
            badge = "Favorit",
            description = "Kesegaran sari mangga berpadu probiotik Yakult dingin asam manis",
            imageUrl = "",
            imageRes = R.drawable.mango_yakult
        )
    )

    /**
     * Simulates fetching the coffee menu from the API.
     * Replace this with a real API call later.
     */
    suspend fun getCoffeeMenu(): List<Coffee> {
        // Simulate network delay
        kotlinx.coroutines.delay(800)
        return coffeeMenu
    }

    /**
     * Gets the most popular items based on category.
     * Takes top items from each category.
     */
    fun getPopularMenu(category: String = "Semua"): List<Coffee> {
        val topCoffee = coffeeMenu.filter { it.category == "Coffee" && it.isBestSeller }
        val topNonCoffee = coffeeMenu.filter { it.category == "Non-Coffee" && it.isBestSeller }

        return when (category) {
            "Coffee" -> topCoffee
            "Non-Coffee" -> topNonCoffee
            else -> topCoffee + topNonCoffee
        }
    }

    /**
     * Returns curated best choices for the home screen.
     */
    fun getHomeBestChoices(category: String = "Semua"): List<Coffee> {
        return when (category) {
            "Coffee" -> coffeeMenu.filter { it.category == "Coffee" }
            "Non-Coffee" -> coffeeMenu.filter { it.category == "Non-Coffee" }
            else -> coffeeMenu
        }
    }

    /**
     * Simulates fetching promo banners from the API.
     */
    suspend fun getPromoBanners(): List<PromoBanner> {
        kotlinx.coroutines.delay(500)
        return promoBanners
    }

    /**
     * Get a specific coffee by its ID.
     */
    fun getCoffeeById(id: Int): Coffee? {
        return coffeeMenu.find { it.id == id }
    }

    /**
     * Mock past completed orders for the Pesanan Saya screen.
     */
    val mockPastOrders = listOf(
        PastOrder(
            id = "ord-028",
            orderNumber = "Pesanan #028",
            date = "24 Des 2024, 15:30 WIB",
            itemsSummary = "1x Marko, 1x Matcha",
            totalPrice = 35000,
            paymentMethod = "Lunas via QRIS",
            imageUrl = "",
            coffeeIdToReorder = 1
        ),
        PastOrder(
            id = "ord-015",
            orderNumber = "Pesanan #015",
            date = "20 Des 2024, 09:15 WIB",
            itemsSummary = "2x Salted Caramel",
            totalPrice = 40000,
            paymentMethod = "Lunas via Kasir",
            imageUrl = "",
            coffeeIdToReorder = 2
        )
    )
}
