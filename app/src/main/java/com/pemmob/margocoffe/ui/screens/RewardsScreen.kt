package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.margocoffe.viewmodel.RewardsEvent
import com.pemmob.margocoffe.viewmodel.RewardsViewModel

data class RewardItem(
    val title: String,
    val description: String,
    val pointsRequired: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val discountAmount: Int = 0,
    val isFreeCoffee: Boolean = false,
    val isFreeUpSize: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsScreen(
    viewModel: RewardsViewModel,
    profileViewModel: com.pemmob.margocoffe.viewmodel.ProfileViewModel? = null,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    val rewardsState by viewModel.uiState.collectAsState()
    val profileData by com.pemmob.margocoffe.viewmodel.ProfileStore.data.collectAsState()
    val loyaltyPoints = rewardsState.loyaltyPoints
    val userName = profileViewModel?.uiState?.collectAsState()?.value?.name ?: profileData.name
    val membershipTier = rewardsState.membershipTier
    val stampCount = rewardsState.stamps
    val freeCoffeeRewards = rewardsState.freeCoffeeRewards

    val rewardsCatalog = listOf(
        RewardItem("Gratis Up-Size", "Upgrade ukuran minuman reguler ke besar", 5, Icons.Outlined.LocalCafe, isFreeUpSize = true, discountAmount = 4000),
        RewardItem("Voucher Diskon Rp 10.000", "Potongan harga untuk semua menu minuman", 15, Icons.Outlined.LocalOffer, discountAmount = 10000),
        RewardItem("Gratis 1 Kopi Pilihan", "Pilih Es Kopi Susu Aren atau Americano gratis", 25, Icons.Outlined.Coffee, isFreeCoffee = true),
        RewardItem("Tumbler Margo Space", "Tumbler eksklusif tahan panas & dingin 500ml", 100, Icons.Outlined.CardGiftcard, discountAmount = 0)
    )

    val snackbarHostState = remember { SnackbarHostState() }

    androidx.compose.runtime.LaunchedEffect(rewardsState.lastRedeemedMessage) {
        rewardsState.lastRedeemedMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearRedeemedMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Margo Rewards",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Kumpulkan poin & tukarkan hadiah menarik",
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
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onHomeClick,
                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                    label = { Text("Beranda") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMenuClick,
                    icon = { Icon(Icons.Outlined.LocalCafe, contentDescription = "Menu") },
                    label = { Text("Menu") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Rewards") },
                    label = { Text("Rewards") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onOrdersClick,
                    icon = { Icon(Icons.Outlined.Receipt, contentDescription = "Pesanan") },
                    label = { Text("Pesanan") }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // ─── Membership Card ────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1E40AF), Color(0xFF1A56DB), Color(0xFF3B82F6))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top row of card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Margo Space Member",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = userName,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Bottom row of card (Points)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Poin Tersedia",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$loyaltyPoints",
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Poin",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Stamp Card Section ─────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Koleksi Stamp Kopi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Beli 10 cangkir, gratis 1 cangkir!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "$stampCount / 10",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 10 Stamp Circles Grid (2 rows of 5)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (i in 1..5) {
                                StampCircle(isStamped = i <= stampCount, index = i)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (i in 6..10) {
                                StampCircle(
                                    isStamped = i <= stampCount,
                                    index = i,
                                    isGift = i == 10 && stampCount < 10
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (stampCount >= 10) {
                            "☕ Kamu mendapatkan 1 kopi gratis!"
                        } else {
                            "☕ Kumpulkan ${10 - stampCount} stamp lagi untuk dapat 1 kopi gratis!"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (freeCoffeeRewards > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kopi Gratis Tersedia",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$freeCoffeeRewards reward siap digunakan",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ─── Voucher Saya yang Siap Digunakan ───────────────────
            if (rewardsState.vouchers.isNotEmpty()) {
                Text(
                    text = "Voucher Saya (${rewardsState.vouchers.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Voucher aktif yang dapat kamu gunakan saat checkout pesanan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    rewardsState.vouchers.forEach { voucher ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFDCFCE7)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.LocalOffer,
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = voucher.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF14532D)
                                        )
                                        Text(
                                            text = voucher.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF166534),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = onMenuClick,
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF16A34A)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Pakai",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ─── Redeem Rewards Catalog ─────────────────────────────
            Text(
                text = "Katalog Penukaran Poin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tukarkan poin loyalty kamu dengan hadiah spesial",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                rewardsCatalog.forEach { reward ->
                    val canRedeem = loyaltyPoints >= reward.pointsRequired
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = reward.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = reward.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = reward.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            tint = if (canRedeem) Color(0xFFF59E0B) else Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${reward.pointsRequired} Poin",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (canRedeem) MaterialTheme.colorScheme.primary else Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    viewModel.onEvent(
                                        RewardsEvent.RedeemReward(
                                            pointsRequired = reward.pointsRequired,
                                            title = reward.title,
                                            description = reward.description,
                                            discountAmount = reward.discountAmount,
                                            isFreeCoffee = reward.isFreeCoffee,
                                            isFreeUpSize = reward.isFreeUpSize
                                        )
                                    )
                                },
                                enabled = canRedeem,
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = Color(0xFFE2E8F0)
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (canRedeem) "Tukar" else "Poin Kurang",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StampCircle(isStamped: Boolean, index: Int, isGift: Boolean = false) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(
                if (isStamped) MaterialTheme.colorScheme.primary
                else if (isGift) Color(0xFFFEF3C7)
                else Color(0xFFF1F5F9)
            )
            .border(
                width = 1.dp,
                color = if (isStamped) Color.Transparent
                else if (isGift) Color(0xFFF59E0B)
                else Color(0xFFE2E8F0),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isStamped) {
            Icon(
                imageVector = Icons.Default.LocalCafe,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else if (isGift) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = "$index",
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
