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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import com.pemmob.margocoffe.R
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.ui.components.CoffeeMenuCard
import com.pemmob.margocoffe.viewmodel.AppViewModel
import com.pemmob.margocoffe.viewmodel.UiState
import kotlinx.coroutines.launch

data class PromoItem(
    val badge: String,
    val title: String,
    val subtitle: String,
    val gradientColors: List<Color>
)

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onCoffeeClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    onRewardsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val checkoutState by viewModel.checkoutState.collectAsState()
    val cartCount = checkoutState.cartItems.sumOf { it.quantity }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedCategory by remember { androidx.compose.runtime.mutableStateOf("Semua") }

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
                    onClick = onRewardsClick,
                    icon = { Icon(Icons.Outlined.Star, contentDescription = "Rewards") },
                    label = { Text("Rewards") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOrdersClick,
                    icon = { Icon(Icons.Outlined.Receipt, contentDescription = "Pesanan") },
                    label = { Text("Pesanan") }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        val displayedCoffees = remember(selectedCategory, homeState.coffeeList) {
            when (homeState.coffeeList) {
                is UiState.Success -> com.pemmob.margocoffe.data.MockRepository.getHomeBestChoices(selectedCategory)
                else -> emptyList()
            }
        }

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
                    loyaltyPoints = homeState.loyaltyPoints,
                    onRewardsClick = onRewardsClick
                )
            }

            // ─── Categories ─────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                CategoryRow(
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it }
                )
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
                    
                    // menu count badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${displayedCoffees.size} menu",
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
                    gridItems(items = displayedCoffees, key = { it.id }) { coffee ->
                        CoffeeMenuCard(
                            coffee = coffee,
                            onClick = { onCoffeeClick(coffee.id) },
                            modifier = Modifier.padding(
                                start = if (displayedCoffees.indexOf(coffee) % 2 == 0) 20.dp else 0.dp,
                                end = if (displayedCoffees.indexOf(coffee) % 2 == 1) 20.dp else 0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoAndGreetingSection(
    userName: String,
    loyaltyPoints: Int,
    onRewardsClick: () -> Unit
) {
    val promos = listOf(
        PromoItem(
            badge = "Edisi Khusus",
            title = "Bingkisan Spesial & Menu Musim Dingin",
            subtitle = "Nikmati racikan istimewa barista Margo Space",
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF1E40AF))
        ),
        PromoItem(
            badge = "Promo Baru",
            title = "Nikmati Kopi Arabica Pilihan Terbaik",
            subtitle = "Biji kopi single origin pilihan dari nusantara",
            gradientColors = listOf(Color(0xFF172554), Color(0xFF1E3A8A), Color(0xFF2563EB))
        ),
        PromoItem(
            badge = "Diskon 50%",
            title = "Beli 1 Gratis 1 Khusus Hari Jumat",
            subtitle = "Klaim promo di seluruh cabang Margo Space",
            gradientColors = listOf(Color(0xFF0B192C), Color(0xFF1E3E62), Color(0xFF000000))
        )
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
        // Top layer: Promo Gradient + Brand Logo
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = promo.gradientColors
                            )
                        )
                ) {
                    // Margo Space logo watermark (from user's added photo)
                    Image(
                        painter = painterResource(id = R.drawable.logomargo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(190.dp)
                            .align(Alignment.CenterEnd)
                            .padding(end = 8.dp)
                            .graphicsLayer(alpha = 0.22f),
                        contentScale = ContentScale.Fit
                    )

                    // Promotional Text
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                            .padding(bottom = 80.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = promo.badge,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = promo.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = promo.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
            
            // Top Notification Icon (floating)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 20.dp)
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
                // User row with Profile icon on the right
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

                    // Profile Icon inside card
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Action row (Loyalty Points full-width)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                        .clickable { onRewardsClick() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$loyaltyPoints Poin",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Tukar Poin",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf("Semua", "Coffee", "Non-Coffee")
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { onSelectCategory(category) }
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
