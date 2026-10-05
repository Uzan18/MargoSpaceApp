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
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.data.CartItem
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.ui.components.CoffeeMenuCard
import com.pemmob.margocoffe.ui.components.PopularCoffeeCard
import com.pemmob.margocoffe.ui.components.formatRupiah
import com.pemmob.margocoffe.viewmodel.CartStore
import com.pemmob.margocoffe.viewmodel.CheckoutViewModel
import com.pemmob.margocoffe.viewmodel.MenuEvent
import com.pemmob.margocoffe.viewmodel.MenuViewModel
import com.pemmob.margocoffe.viewmodel.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menuViewModel: MenuViewModel,
    checkoutViewModel: CheckoutViewModel,
    onHomeClick: () -> Unit,
    onCoffeeClick: (Int) -> Unit,
    onRewardsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val menuState by menuViewModel.uiState.collectAsState()
    val checkoutState by checkoutViewModel.uiState.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    val categories = listOf(
        "Semua",
        "Coffee",
        "Non-Coffee"
    )

    val selectedCategory = menuState.selectedCategory

    val menuLoadState = menuState.coffeeList

    val allDrinks = menuState.allDrinks

    val popularDrinks = menuState.popularDrinks

    val visibleDrinks = menuState.visibleDrinks

    val addProductToCart: (Coffee) -> Unit = { coffee ->

        CartStore.add(
            CartItem(
                coffee = coffee,
                quantity = 1,
                selectedSize = MockRepository.sizeOptions.first(),
                selectedIceLevel = MockRepository.iceLevelOptions.first(),
                selectedSweetness = MockRepository.sweetnessOptions.first()
            )
        )

        coroutineScope.launch {

            val result = snackbarHostState.showSnackbar(
                message = "${coffee.name} ditambahkan ke keranjang",
                actionLabel = "Lihat keranjang",
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )

            if (result == SnackbarResult.ActionPerformed) {
                onCartClick()
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },

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
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 10.dp
                                ),

                            verticalAlignment = Alignment.CenterVertically,

                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = checkoutState.cartItems.joinToString(" · ") {
                                        "${it.coffee.name} x${it.quantity}"
                                    },

                                    style = MaterialTheme.typography.bodySmall,

                                    maxLines = 1,

                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = formatRupiah(
                                        checkoutState.cartItems.sumOf {
                                            it.totalPrice
                                        }
                                    ),

                                    style = MaterialTheme.typography.titleSmall,

                                    fontWeight = FontWeight.Bold,

                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = onCartClick
                            ) {
                                Text("Lihat keranjang")
                            }
                        }
                    }
                }

                NavigationBar(
                    containerColor = Color.White
                ) {

                    NavigationBarItem(
                        selected = false,
                        onClick = onHomeClick,
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Beranda"
                            )
                        },
                        label = {
                            Text("Beranda")
                        }
                    )

                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = {
                            Icon(
                                Icons.Outlined.LocalCafe,
                                contentDescription = "Menu"
                            )
                        },
                        label = {
                            Text("Menu")
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor =
                                MaterialTheme.colorScheme.primary,

                            selectedTextColor =
                                MaterialTheme.colorScheme.primary,

                            indicatorColor =
                                MaterialTheme.colorScheme.primaryContainer
                                    .copy(alpha = 0.5f)
                        )
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = onRewardsClick,
                        icon = {
                            Icon(
                                Icons.Outlined.Star,
                                contentDescription = "Rewards"
                            )
                        },
                        label = {
                            Text("Rewards")
                        }
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = onOrdersClick,
                        icon = {
                            Icon(
                                Icons.Outlined.Receipt,
                                contentDescription = "Pesanan"
                            )
                        },
                        label = {
                            Text("Pesanan")
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        when (menuLoadState) {

            UiState.Loading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    contentAlignment = Alignment.Center
                ) {

                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Gagal memuat menu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = menuLoadState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {
                                menuViewModel.onEvent(
                                    MenuEvent.RetryLoad
                                )
                            }
                        ) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }

            is UiState.Success -> {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),

                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),

                    contentPadding = PaddingValues(
                        bottom = 24.dp
                    ),

                    horizontalArrangement = Arrangement.spacedBy(16.dp),

                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Search
                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {

                        OutlinedTextField(
                            value = menuState.searchQuery,

                            onValueChange = {
                                menuViewModel.onEvent(
                                    MenuEvent.Search(it)
                                )
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp,
                                    vertical = 6.dp
                                ),

                            placeholder = {
                                Text(
                                    "Cari minuman favoritmu..."
                                )
                            },

                            leadingIcon = {

                                Icon(
                                    Icons.Outlined.Search,
                                    contentDescription = "Cari",

                                    tint = MaterialTheme.colorScheme
                                        .onSurfaceVariant
                                )
                            },

                            singleLine = true,

                            shape = RoundedCornerShape(16.dp),

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White,

                                unfocusedContainerColor = Color.White,

                                focusedBorderColor =
                                    MaterialTheme.colorScheme.primary,

                                unfocusedBorderColor =
                                    MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    // Category
                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {

                        LazyRow(
                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp),

                            contentPadding =
                                PaddingValues(
                                    horizontal = 20.dp
                                ),

                            modifier = Modifier.padding(
                                vertical = 4.dp
                            )
                        ) {

                            items(
                                items = categories,
                                key = { it }
                            ) { category ->

                                val isSelected =
                                    selectedCategory == category

                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(24.dp)
                                        )
                                        .background(
                                            if (isSelected)
                                                MaterialTheme
                                                    .colorScheme
                                                    .primary
                                            else
                                                Color.White
                                        )
                                        .border(
                                            width = 1.dp,

                                            color =
                                                if (isSelected)
                                                    Color.Transparent
                                                else
                                                    MaterialTheme
                                                        .colorScheme
                                                        .outlineVariant,

                                            shape =
                                                RoundedCornerShape(24.dp)
                                        )
                                        .clickable {

                                            menuViewModel.onEvent(
                                                MenuEvent.SelectCategory(
                                                    category
                                                )
                                            )
                                        }
                                        .padding(
                                            horizontal = 20.dp,
                                            vertical = 10.dp
                                        ),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text = category,

                                        style =
                                            MaterialTheme.typography
                                                .labelLarge,

                                        fontWeight =
                                            if (isSelected)
                                                FontWeight.Bold
                                            else
                                                FontWeight.Medium,

                                        color =
                                            if (isSelected)
                                                Color.White
                                            else
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Popular
                    if (
                        menuState.searchQuery.isBlank() &&
                        popularDrinks.isNotEmpty()
                    ) {

                        item(
                            span = {
                                GridItemSpan(2)
                            }
                        ) {

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 20.dp
                                        )
                                        .padding(
                                            top = 12.dp,
                                            bottom = 10.dp
                                        ),

                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Column {

                                        Text(
                                            text = "Paling Populer",

                                            style =
                                                MaterialTheme.typography
                                                    .titleLarge,

                                            fontWeight = FontWeight.Bold,

                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurface
                                        )

                                        Text(
                                            text =
                                                "Pilihan terbaik barista kami hari ini",

                                            style =
                                                MaterialTheme.typography
                                                    .bodySmall,

                                            color =
                                                MaterialTheme.colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text =
                                            "${popularDrinks.size} Rekomendasi",

                                        style =
                                            MaterialTheme.typography
                                                .labelMedium,

                                        color =
                                            MaterialTheme.colorScheme
                                                .primary,

                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                LazyRow(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(14.dp),

                                    contentPadding =
                                        PaddingValues(
                                            horizontal = 20.dp
                                        ),

                                    modifier =
                                        Modifier.fillMaxWidth()
                                ) {

                                    items(
                                        items = popularDrinks,
                                        key = {
                                            "pop_${it.id}"
                                        }
                                    ) { coffee ->

                                        PopularCoffeeCard(
                                            coffee = coffee,

                                            onClick = {
                                                onCoffeeClick(
                                                    coffee.id
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section title
                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp
                                )
                                .padding(
                                    top = 16.dp,
                                    bottom = 4.dp
                                ),

                            horizontalArrangement =
                                Arrangement.SpaceBetween,

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    if (selectedCategory == "Semua") {
                                        "Daftar Menu"
                                    } else {
                                        "Menu $selectedCategory"
                                    },

                                style =
                                    MaterialTheme.typography
                                        .titleMedium,

                                fontWeight = FontWeight.Bold,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurface
                            )

                            Box(
                                modifier = Modifier
                                    .background(
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .primaryContainer
                                                .copy(alpha = 0.5f),

                                        shape =
                                            RoundedCornerShape(12.dp)
                                    )
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    )
                            ) {

                                Text(
                                    text =
                                        "${visibleDrinks.size} menu",

                                    style =
                                        MaterialTheme.typography
                                            .labelSmall,

                                    color =
                                        MaterialTheme.colorScheme
                                            .primary,

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Menu grid
                    items(
                        items = visibleDrinks,
                        key = { it.id }
                    ) { coffee ->

                        val index =
                            visibleDrinks.indexOfFirst {
                                it.id == coffee.id
                            }

                        CoffeeMenuCard(
                            coffee = coffee,

                            onClick = {
                                onCoffeeClick(
                                    coffee.id
                                )
                            },

                            modifier = Modifier.padding(
                                start =
                                    if (index % 2 == 0)
                                        20.dp
                                    else
                                        0.dp,

                                end =
                                    if (index % 2 == 1)
                                        20.dp
                                    else
                                        0.dp
                            )
                        )
                    }
                }
            }
        }
    }
}