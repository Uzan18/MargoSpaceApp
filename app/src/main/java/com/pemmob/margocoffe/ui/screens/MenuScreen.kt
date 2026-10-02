package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.ui.components.CoffeeMenuCard
import com.pemmob.margocoffe.ui.components.PopularCoffeeCard
import com.pemmob.margocoffe.ui.components.formatRupiah
import com.pemmob.margocoffe.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: AppViewModel,
    onHomeClick: () -> Unit,
    onCoffeeClick: (Int) -> Unit,
    onRewardsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val checkoutState by viewModel.checkoutState.collectAsState()
    val cartCount = checkoutState.cartItems.sumOf { it.quantity }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val addProductToCart: (Coffee) -> Unit = { coffee ->
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
    }

    var selectedCategory by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("Semua", "Coffee", "Non-Coffee")

    // Filter popular items based on selected category (2 best per category)
    val popularDrinks = remember(selectedCategory) {
        MockRepository.getPopularMenu(selectedCategory)
    }

    // Filter full menu based on category & search query
    val visibleDrinks = remember(selectedCategory, searchQuery) {
        MockRepository.coffeeMenu.filter { coffee ->
            (selectedCategory == "Semua" || coffee.category.equals(selectedCategory, ignoreCase = true)) &&
                    (searchQuery.isBlank() || coffee.name.contains(searchQuery, ignoreCase = true) || coffee.description.contains(searchQuery, ignoreCase = true))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Menu Margo Space",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Pilihan minuman kopi & non-kopi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                if (checkoutState.cartItems.isNotEmpty()) {
                    Surface(
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = checkoutState.cartItems.joinToString(" · ") {
                                        "${it.coffee.name} x${it.quantity}"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatRupiah(checkoutState.cartItems.sumOf { it.totalPrice }),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Button(onClick = onCartClick) {
                                Text("Lihat keranjang")
                            }
                        }
                    }
                }
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onHomeClick,
                        icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                        label = { Text("Beranda") }
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.LocalCafe, contentDescription = "Menu") },
                        label = { Text("Menu") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
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
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Search Bar ─────────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    placeholder = { Text("Cari minuman favoritmu...") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Cari",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // ─── Category Chips ─────────────────────────────────────
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // ─── Paling Populer (LazyRow) Section ───────────────────
            if (searchQuery.isBlank() && popularDrinks.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Section Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 12.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Paling Populer",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Pilihan terbaik barista kami hari ini",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = "${popularDrinks.size} Rekomendasi",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Horizontal LazyRow of Popular Cards
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(popularDrinks, key = { "pop_${it.id}" }) { coffee ->
                                PopularCoffeeCard(
                                    coffee = coffee,
                                    onClick = { onCoffeeClick(coffee.id) }
                                )
                            }
                        }
                    }
                }
            }

            // ─── Section Title for All / Filtered Menu ──────────────
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedCategory == "Semua") "Daftar Menu" else "Menu $selectedCategory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${visibleDrinks.size} menu",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ─── Menu Grid ──────────────────────────────────────────
            items(visibleDrinks, key = { it.id }) { coffee ->
                CoffeeMenuCard(
                    coffee = coffee,
                    onClick = { onCoffeeClick(coffee.id) },
                    modifier = Modifier.padding(
                        start = if (visibleDrinks.indexOf(coffee) % 2 == 0) 20.dp else 0.dp,
                        end = if (visibleDrinks.indexOf(coffee) % 2 == 1) 20.dp else 0.dp
                    )
                )
            }
        }
    }
}