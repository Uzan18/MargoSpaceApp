package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.ui.components.CoffeeMenuCard
import com.pemmob.margocoffe.viewmodel.AppViewModel
import com.pemmob.margocoffe.viewmodel.UiState
import kotlinx.coroutines.launch

data class PromoItem(val imageUrl: String, val badge: String, val title: String)

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onCoffeeClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val checkoutState by viewModel.checkoutState.collectAsState()
    val cartCount = checkoutState.cartItems.sumOf { it.quantity }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMenuClick,
                    icon = { Icon(Icons.Outlined.LocalCafe, contentDescription = "Menu") },
                    label = { Text("Menu") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onCartClick,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("$cartCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Outlined.ShoppingCart, contentDescription = "Keranjang")
                        }
                    },
                    label = { Text("Keranjang") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.Receipt, contentDescription = "Pesanan") },
                    label = { Text("Pesanan") }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Promo Slider + Greeting Card (overlapping layout) ──
            item(span = { GridItemSpan(2) }) {
                PromoAndGreetingSection(
                    userName = homeState.userName,
                    loyaltyPoints = homeState.loyaltyPoints
                )
            }

            // ─── Categories ─────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                CategoryRow()
            }

            // ─── Section Title ──────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilihan Terbaik",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Menu kurasi terpopuler untuk Anda",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    // 8 menu badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "8 menu",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ─── Coffee Grid ────────────────────────────────────────
            when (val coffeeState = homeState.coffeeList) {
                is UiState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                is UiState.Error -> {
                    item(span = { GridItemSpan(2) }) {
                        Text(
                            text = "Gagal memuat menu: ${coffeeState.message}",
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
                is UiState.Success -> {
                    gridItems(items = coffeeState.data, key = { it.id }) { coffee ->
                        CoffeeMenuCard(
                            coffee = coffee,
                            onClick = { onCoffeeClick(coffee.id) },
                            onAddClick = {
                                viewModel.addMenuItemToCart(coffee)
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "${coffee.name} ditambahkan ke keranjang",
                                        actionLabel = "Lihat keranjang",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Long
                                    )
                                    if (result == SnackbarResult.ActionPerformed) onCartClick()
                                }
                            },
                            modifier = Modifier.padding(
                                start = if (coffeeState.data.indexOf(coffee) % 2 == 0) 20.dp else 0.dp,
                                end = if (coffeeState.data.indexOf(coffee) % 2 == 1) 20.dp else 0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoAndGreetingSection(userName: String, loyaltyPoints: Int) {
    val promos = listOf(
        PromoItem("https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?auto=format&fit=crop&w=800&q=80", "Edisi Khusus", "Bingkisan Spesial & Menu Musim Dingin"),
        PromoItem("https://images.unsplash.com/photo-1497935586351-b67a49e012bf?auto=format&fit=crop&w=800&q=80", "Promo Baru", "Nikmati Kopi Arabica Pilihan Terbaik"),
        PromoItem("https://images.unsplash.com/photo-1511920170033-f8396924c348?auto=format&fit=crop&w=800&q=80", "Diskon 50%", "Beli 1 Gratis 1 Khusus Hari Jumat")
    )
    
    // Setup for true infinite scrolling (always slides right)
    val pageCount = Int.MAX_VALUE
    val startIndex = pageCount / 2 - (pageCount / 2 % promos.size)
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { pageCount }
    )

    // Auto-scroll effect (fixed)
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Top layer: Promo Image (edge to edge with Pager)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp) // Space for the card to overlap at the bottom
                .height(300.dp), // Taller to cover behind status bar
            contentAlignment = Alignment.BottomStart
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val actualPage = page % promos.size
                val promo = promos[actualPage]
                Box(modifier = Modifier.fillMaxSize()) {
                    // Promo Image using Coil
                    coil.compose.AsyncImage(
                        model = promo.imageUrl,
                        contentDescription = promo.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Dark overlay for text readability, blending into top
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )

                    // Promotional Text
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                            .padding(bottom = 80.dp) // Adjusted slightly higher
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = promo.badge,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = promo.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Top Icons (floating)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Notification Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Profile Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Pager Indicator Dots
            Row(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp), // Pushed up slightly
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentActualPage = pagerState.currentPage % promos.size
                repeat(promos.size) { iteration ->
                    val isActive = currentActualPage == iteration
                    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f)
                    val size = if (isActive) 10.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(size)
                    )
                }
            }
        }

        // Bottom layer: Greeting card overlapping the image
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // User row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hai $userName!",
                        style = MaterialTheme.typography.headlineSmall, // Bigger text
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action row (Points & Plan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Poin
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$loyaltyPoints Poin",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Tukar",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    // My Plan
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Map,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "My Plan",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow() {
    val categories = listOf("Semua Kopi", "Espresso", "Non-Kopi")
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == "Semua Kopi"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
