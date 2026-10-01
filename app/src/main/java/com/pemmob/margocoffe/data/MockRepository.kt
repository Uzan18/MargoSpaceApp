package com.pemmob.margocoffe.data

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
            subtitle = "Promo spesial akhir pekan di Kopi Royal",
            backgroundColor = 0xFF6A1B9A
        ),
        PromoBanner(
            id = 3,
            title = "Member Baru?",
            subtitle = "Daftar sekarang & dapatkan 50 poin gratis!",
            backgroundColor = 0xFF00695C
        )
    )

    // ─── Coffee Menu ────────────────────────────────────────────────
    val coffeeMenu = listOf(
        Coffee(
            id = 1,
            name = "Es Kopi Susu Aren",
            price = 20000,
            imageDescription = "Es Kopi Susu Aren",
            category = "Kopi Susu",
            isBestSeller = true,
            description = "Perpaduan kopi robusta pilihan dengan susu segar dan gula aren asli. Rasa manis alami yang bikin nagih!",
            imageUrl = "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 2,
            name = "Americano",
            price = 18000,
            imageDescription = "Americano",
            category = "Kopi Hitam",
            isBestSeller = false,
            description = "Espresso shot yang diencerkan dengan air panas. Cocok untuk penikmat kopi sejati.",
            imageUrl = "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 3,
            name = "Cappuccino",
            price = 24000,
            imageDescription = "Cappuccino",
            category = "Kopi Susu",
            isBestSeller = true,
            description = "Espresso dengan steamed milk dan foam yang lembut. Klasik dan selalu enak.",
            imageUrl = "https://images.unsplash.com/photo-1534778101976-62847782c213?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 4,
            name = "Matcha Latte",
            price = 26000,
            imageDescription = "Matcha Latte",
            category = "Non-Kopi",
            isBestSeller = false,
            description = "Matcha premium dari Jepang dicampur susu segar. Creamy dan menyegarkan.",
            imageUrl = "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 5,
            name = "Es Coklat",
            price = 22000,
            imageDescription = "Es Coklat",
            category = "Non-Kopi",
            isBestSeller = true,
            description = "Coklat Belgium premium dengan susu segar. Manisnya pas, coklatnya nendang!",
            imageUrl = "https://images.unsplash.com/photo-1542990253-0b8be7ec9cbe?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 6,
            name = "Caramel Macchiato",
            price = 28000,
            imageDescription = "Caramel Macchiato",
            category = "Kopi Susu",
            isBestSeller = false,
            description = "Espresso dengan susu dan drizzle caramel. Manis, creamy, dan aromatic.",
            imageUrl = "https://images.unsplash.com/photo-1485808191679-5f86510681a2?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 7,
            name = "Es Teh Lemon",
            price = 15000,
            imageDescription = "Es Teh Lemon",
            category = "Non-Kopi",
            isBestSeller = false,
            description = "Teh hitam segar dengan perasan lemon asli. Segar dan menyejukkan.",
            imageUrl = "https://images.unsplash.com/photo-1556679343-c7306c1976bc?auto=format&fit=crop&w=700&q=85"
        ),
        Coffee(
            id = 8,
            name = "Kopi Susu Royal",
            price = 25000,
            imageDescription = "Kopi Susu Royal",
            category = "Kopi Susu",
            isBestSeller = true,
            description = "Signature drink Kopi Royal! Espresso, susu, brown sugar, dan secret spice.",
            imageUrl = "https://images.unsplash.com/photo-1497935586351-b67a49e012bf?auto=format&fit=crop&w=700&q=85"
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
}
