package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.ui.components.formatRupiah
import com.pemmob.margocoffe.viewmodel.AppViewModel
import kotlinx.coroutines.launch

private data class MenuProduct(
    val item: Coffee,
    val section: String,
    val imageUrl: String
)

private val menuProducts = listOf(
    MenuProduct(MockRepository.coffeeMenu[0], "Minuman", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(MockRepository.coffeeMenu[2], "Minuman", "https://images.unsplash.com/photo-1534778101976-62847782c213?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(MockRepository.coffeeMenu[1], "Minuman", "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(MockRepository.coffeeMenu[3], "Minuman", "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(Coffee(101, "Chicken Croissant", 28000, "Chicken Croissant", "Makanan", description = "Croissant renyah dengan ayam dan sayuran segar."), "Makanan", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(Coffee(102, "Royal Club Sandwich", 32000, "Royal Club Sandwich", "Makanan", description = "Roti panggang berisi ayam, telur, dan sayuran."), "Makanan", "https://images.unsplash.com/photo-1528735602780-2552fd46c7af?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(Coffee(103, "Pasta Creamy", 35000, "Pasta Creamy", "Makanan", description = "Pasta creamy gurih dengan taburan keju."), "Makanan", "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(Coffee(201, "Butter Croissant", 18000, "Butter Croissant", "Snack", description = "Pastry butter panggang, renyah di luar."), "Snack", "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(Coffee(202, "Kentang Goreng", 20000, "Kentang Goreng", "Snack", description = "Kentang goreng hangat dengan saus pilihan."), "Snack", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?auto=format&fit=crop&w=700&q=85"),
    MenuProduct(Coffee(203, "Chocolate Brownies", 22000, "Chocolate Brownies", "Snack", description = "Brownies cokelat lembut untuk teman kopi."), "Snack", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?auto=format&fit=crop&w=700&q=85")
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    viewModel: AppViewModel,
    onHomeClick: () -> Unit,
    onCoffeeClick: (Int) -> Unit,
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
    var selectedSection by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    val sections = listOf("Semua", "Minuman", "Makanan", "Snack")
    val visibleProducts = menuProducts.filter { product ->
        (selectedSection == "Semua" || product.section == selectedSection) &&
            (searchQuery.isBlank() || product.item.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Menu Kopi Royal", fontWeight = FontWeight.Bold)
                        Text("Minuman, makanan, dan camilan", style = MaterialTheme.typography.bodySmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                        label = { Text("Menu") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onCartClick,
                        icon = {
                            BadgedBox(badge = { if (cartCount > 0) Badge { Text("$cartCount") } }) {
                                Icon(Icons.Outlined.ShoppingCart, contentDescription = "Keranjang")
                            }
                        },
                        label = { Text("Keranjang") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Restaurant, contentDescription = "Pesanan") },
                        label = { Text("Pesanan") }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Cari minuman, makanan, atau snack") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sections.forEach { section ->
                    FilterChip(
                        selected = selectedSection == section,
                        onClick = { selectedSection = section },
                        label = { Text(section) }
                    )
                }
            }

            Text(
                text = if (selectedSection == "Semua") "Pilihan untuk kamu" else selectedSection,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(visibleProducts, key = { it.item.id }) { product ->
                    MenuProductCard(
                        product = product,
                        onClick = {
                            if (product.section == "Minuman") onCoffeeClick(product.item.id)
                            else addProductToCart(product.item)
                        },
                        onAddClick = { addProductToCart(product.item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuProductCard(
    product: MenuProduct,
    onClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.18f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.item.name,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = product.item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatRupiah(product.item.price),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onAddClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah ${product.item.name} ke keranjang",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}