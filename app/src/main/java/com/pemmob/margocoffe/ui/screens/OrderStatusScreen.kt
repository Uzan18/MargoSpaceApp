package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pemmob.margocoffe.R
import com.pemmob.margocoffe.data.PastOrder
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.ui.components.formatRupiah
import com.pemmob.margocoffe.viewmodel.OrderEvent
import com.pemmob.margocoffe.viewmodel.OrderViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(
    viewModel: OrderViewModel,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onRewardsClick: () -> Unit
) {
    val orderState by viewModel.uiState.collectAsState()
    var selectedTab by remember { androidx.compose.runtime.mutableIntStateOf(0) } // 0: Dalam Proses, 1: Riwayat Selesai
    var showReceiptDialog by remember { mutableStateOf(false) }
    var showPickupDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pesanan Saya",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.navigationBarsPadding(),
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onHomeClick,
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Beranda") },
                        label = { Text("Beranda") }
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
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Filled.Receipt, contentDescription = "Pesanan") },
                        label = { Text("Pesanan") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // ─── Segmented Tabs (Dalam Proses / Riwayat Selesai) ────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 0: Dalam Proses
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedTab = 0 },
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dalam Proses",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == 0) Color.White.copy(alpha = 0.25f) else Color(0xFFE2E8F0))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (orderState.hasActiveOrder) "1" else "0",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Tab 1: Riwayat Selesai
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedTab = 1 },
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Riwayat Selesai",
                            fontSize = 13.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ─── TAB CONTENT ──────────────────────────────────────────
            if (selectedTab == 0) {
                // ══════════════════════════════════════════════════════
                // TAB 0: DALAM PROSES
                // ══════════════════════════════════════════════════════
                if (orderState.hasActiveOrder) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row: Pesanan #042 | Meja 07 | Badge Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = orderState.orderNumber,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFEFF6FF)
                                    ) {
                                        Text(
                                            text = orderState.tableNumber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Campaign,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = orderState.statusBadge,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${if (orderState.isDineIn) "Makan di Sini" else "Take Away"} • ${orderState.branchName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // ─── Status & Instruksi Pengambilan Box ────
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.AccessTime,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Estimasi siap: ${orderState.estimatedTime} • Atas Nama: ${orderState.customerName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Silakan tunggu nama atau nomor antrean Anda dipanggil di kasir.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // ─── List of Ordered Drinks ───────────────
                            orderState.items.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val fallbackCoffee = MockRepository.getCoffeeById(item.coffee.id)
                                        ?: MockRepository.coffeeMenu.find { it.name.equals(item.coffee.name, ignoreCase = true) }
                                    val resolvedImageRes = if (item.coffee.imageRes != 0) item.coffee.imageRes else (fallbackCoffee?.imageRes ?: 0)
                                    val resolvedImageUrl = if (item.coffee.imageUrl.isNotBlank()) item.coffee.imageUrl else (fallbackCoffee?.imageUrl ?: "")

                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF8F9FA))
                                            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (resolvedImageRes != 0) {
                                            Image(
                                                painter = painterResource(id = resolvedImageRes),
                                                contentDescription = item.coffee.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Fit
                                            )
                                        } else if (resolvedImageUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = resolvedImageUrl,
                                                contentDescription = item.coffee.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Image(
                                                painter = painterResource(id = R.drawable.logomargo),
                                                contentDescription = item.coffee.name,
                                                modifier = Modifier.size(32.dp),
                                                contentScale = ContentScale.Fit
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.coffee.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${item.quantity}x • ${item.selectedSize.label} • ${item.selectedSweetness.label}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = formatRupiah(item.totalPrice),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                if (index < orderState.items.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = Color(0xFFF1F5F9)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            // Total Pembayaran
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Pembayaran (${orderState.items.sumOf { it.quantity }} Minuman)",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatRupiah(orderState.totalAmount),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons: Struk Digital & Lacak / Ambil
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showReceiptDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(22.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Receipt,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Struk Digital",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Button(
                                    onClick = { viewModel.onEvent(OrderEvent.CompleteOrder) },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(22.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = "Selesai",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logomargo_foreground),
                                    contentDescription = "Margo Space",
                                    modifier = Modifier.size(56.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tidak Ada Pesanan Aktif",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pesan kopi favoritmu sekarang untuk menikmati hari!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onMenuClick,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Pesan Kopi Sekarang")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ─── Section Header: Riwayat Pesanan Terbaru ─────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Pesanan Terbaru",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Lihat Semua >",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { selectedTab = 1 }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List of Recent Past Orders (max 2)
                orderState.pastOrders.take(2).forEach { pastOrder ->
                    PastOrderCard(
                        pastOrder = pastOrder,
                        onReorder = {
                            viewModel.onEvent(OrderEvent.Reorder(pastOrder.coffeeIdToReorder))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${pastOrder.orderNumber} berhasil ditambahkan ke keranjang!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                // ══════════════════════════════════════════════════════
                // TAB 1: RIWAYAT SELESAI
                // ══════════════════════════════════════════════════════
                Text(
                    text = "Semua Riwayat Pesanan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                orderState.pastOrders.forEach { pastOrder ->
                    PastOrderCard(
                        pastOrder = pastOrder,
                        onReorder = {
                            viewModel.onEvent(OrderEvent.Reorder(pastOrder.coffeeIdToReorder))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${pastOrder.orderNumber} berhasil ditambahkan ke keranjang!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ─── Struk Digital Dialog ────────────────────────────────────────
    if (showReceiptDialog) {
        AlertDialog(
            onDismissRequest = { showReceiptDialog = false },
            confirmButton = {
                Button(
                    onClick = { showReceiptDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Tutup Struk")
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MARGO SPACE",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Struk Pembayaran Digital",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("No. Pesanan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(orderState.orderNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pelanggan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(orderState.customerName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Waktu", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(orderState.orderTime, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Metode", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("QRIS / Lunas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF16A34A))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(10.dp))

                    orderState.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.quantity}x ${item.coffee.name}", fontSize = 12.sp)
                            Text(formatRupiah(item.totalPrice), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    if (orderState.discountAmount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Diskon Reward / Voucher", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                            Text("-${formatRupiah(orderState.discountAmount)}", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = formatRupiah(orderState.totalAmount),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    }

    // ─── Dialog Petunjuk Pengambilan ─────────────────────────────────
    if (showPickupDialog) {
        AlertDialog(
            onDismissRequest = { showPickupDialog = false },
            confirmButton = {
                Button(
                    onClick = { showPickupDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Mengerti")
                }
            },
            title = {
                Text(
                    text = "Pengambilan Pesanan",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Nomor Antrean Anda:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = orderState.orderNumber,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Silakan menuju ke kasir saat nomor atau nama \"${orderState.customerName}\" dipanggil oleh kasir.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        )
    }
}

/**
 * Reusable Card component for Past Order history items.
 */
@Composable
private fun PastOrderCard(
    pastOrder: PastOrder,
    onReorder: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Pesanan #028 | Selesai
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pastOrder.orderNumber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = pastOrder.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = pastOrder.date,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Image & Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val fallbackCoffee = MockRepository.getCoffeeById(pastOrder.coffeeIdToReorder)
                    ?: MockRepository.coffeeMenu.find { pastOrder.itemsSummary.contains(it.name, ignoreCase = true) }
                val resolvedImageRes = if (pastOrder.imageRes != 0) pastOrder.imageRes else (fallbackCoffee?.imageRes ?: 0)
                val resolvedImageUrl = if (pastOrder.imageUrl.isNotBlank()) pastOrder.imageUrl else (fallbackCoffee?.imageUrl ?: "")

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8F9FA))
                        .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (resolvedImageRes != 0) {
                        Image(
                            painter = painterResource(id = resolvedImageRes),
                            contentDescription = pastOrder.orderNumber,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else if (resolvedImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = resolvedImageUrl,
                            contentDescription = pastOrder.orderNumber,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.logomargo),
                            contentDescription = pastOrder.orderNumber,
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pastOrder.itemsSummary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${formatRupiah(pastOrder.totalPrice)} • ${pastOrder.paymentMethod}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pesan Lagi Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onReorder,
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pesan Lagi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

