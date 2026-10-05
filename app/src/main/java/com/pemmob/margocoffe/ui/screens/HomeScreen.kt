package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pemmob.margocoffe.R
import coil.compose.AsyncImage
import com.pemmob.margocoffe.ui.components.CoffeeMenuCard
import com.pemmob.margocoffe.viewmodel.CheckoutViewModel
import com.pemmob.margocoffe.viewmodel.HomeEvent
import com.pemmob.margocoffe.viewmodel.HomeViewModel
import com.pemmob.margocoffe.viewmodel.MenuEvent
import com.pemmob.margocoffe.viewmodel.MenuViewModel
import com.pemmob.margocoffe.viewmodel.AppNotification
import com.pemmob.margocoffe.viewmodel.NotificationEvent
import com.pemmob.margocoffe.viewmodel.NotificationType
import com.pemmob.margocoffe.viewmodel.NotificationViewModel
import com.pemmob.margocoffe.viewmodel.ProfileEvent
import com.pemmob.margocoffe.viewmodel.ProfileViewModel
import com.pemmob.margocoffe.viewmodel.RewardsViewModel
import com.pemmob.margocoffe.viewmodel.UiState
import kotlinx.coroutines.delay



@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    menuViewModel: MenuViewModel,
    rewardsViewModel: RewardsViewModel,
    checkoutViewModel: CheckoutViewModel,
    notificationViewModel: NotificationViewModel,
    profileViewModel: ProfileViewModel,
    onCoffeeClick: (Int) -> Unit,
    onMenuClick: () -> Unit,
    onRewardsClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val menuState by menuViewModel.uiState.collectAsState()
    val rewardsState by rewardsViewModel.uiState.collectAsState()
    val checkoutState by checkoutViewModel.uiState.collectAsState()
    val notificationState by notificationViewModel.uiState.collectAsState()
    val profileState by profileViewModel.uiState.collectAsState()

    var showNotifications by remember { mutableStateOf(false) }

    val selectedCategory = menuState.selectedCategory

    val displayedCoffees = menuState.bestSellerPerCategory

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Beranda"
                        )
                    },
                    label = {
                        Text("Beranda")
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
                    onClick = onMenuClick,
                    icon = {
                        Icon(
                            Icons.Outlined.LocalCafe,
                            contentDescription = "Menu"
                        )
                    },
                    label = {
                        Text("Menu")
                    }
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
        },

        containerColor = MaterialTheme.colorScheme.background,

        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),

            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = paddingValues.calculateBottomPadding()
                ),

            contentPadding = PaddingValues(
                bottom = 16.dp
            ),

            horizontalArrangement =
                Arrangement.spacedBy(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // ─── Promo Slider + Greeting Card ──────────────────────
            item(
                span = {
                    GridItemSpan(2)
                }
            ) {
                PromoAndGreetingSection(
                    userName = profileState.name,
                    profileImageUrl = profileState.imageUrl,
                    loyaltyPoints = rewardsState.loyaltyPoints,
                    hasUnreadNotifications = notificationState.hasUnread,
                    onNotificationClick = {
                        showNotifications = true
                    },
                    onProfileClick = onProfileClick,
                    onRewardsClick = onRewardsClick
                )
            }

            // ─── Categories ────────────────────────────────────────
            item(
                span = {
                    GridItemSpan(2)
                }
            ) {
                CategoryRow(
                    selectedCategory = selectedCategory,
                    onSelectCategory = { category ->
                        menuViewModel.onEvent(
                            MenuEvent.SelectCategory(category)
                        )
                    }
                )
            }

            // ─── Section Title ─────────────────────────────────────
            item(
                span = {
                    GridItemSpan(2)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 8.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilihan Terbaik",
                            style =
                                MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color =
                                MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text =
                                "Menu kurasi terpopuler untuk Anda",
                            style =
                                MaterialTheme.typography.bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color =
                                    MaterialTheme.colorScheme
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
                                "${displayedCoffees.size} menu",

                            style =
                                MaterialTheme.typography.labelSmall,

                            color =
                                MaterialTheme.colorScheme.primary,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }

            // ─── Coffee Grid ───────────────────────────────────────
            when (val coffeeState = menuState.coffeeList) {

                is UiState.Loading -> {
                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),

                            contentAlignment =
                                Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is UiState.Error -> {
                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 20.dp
                                ),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Gagal memuat menu",

                                style =
                                    MaterialTheme.typography
                                        .titleMedium,

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text = coffeeState.message,

                                style =
                                    MaterialTheme.typography
                                        .bodySmall,

                                color =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
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
                    if (displayedCoffees.isEmpty()) {

                        item(
                            span = {
                                GridItemSpan(2)
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 20.dp,
                                        vertical = 40.dp
                                    ),

                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Text(
                                    text =
                                        "Belum ada menu untuk kategori ini",

                                    style =
                                        MaterialTheme.typography
                                            .bodyMedium,

                                    color =
                                        MaterialTheme.colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }

                    } else {

                        gridItems(
                            items = displayedCoffees,
                            key = { it.id }
                        ) { coffee ->

                            val coffeeIndex =
                                displayedCoffees.indexOfFirst {
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
                                        if (coffeeIndex % 2 == 0)
                                            20.dp
                                        else
                                            0.dp,

                                    end =
                                        if (coffeeIndex % 2 == 1)
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
    if (showNotifications) {
        NotificationBottomSheet(
            notifications = notificationState.notifications,
            onDismiss = {
                showNotifications = false
            },
            onNotificationClick = { notification ->
                if (!notification.isRead) {
                    notificationViewModel.onEvent(
                        NotificationEvent.MarkAsRead(notification.id)
                    )
                }
                showNotifications = false
                when (notification.type) {
                    NotificationType.REWARD,
                    NotificationType.POINTS,
                    NotificationType.MEMBERSHIP -> onRewardsClick()
                    NotificationType.ORDER -> onOrdersClick()
                    NotificationType.PROMO -> onMenuClick()
                }
            },
            onMarkAllAsRead = {
                notificationViewModel.onEvent(
                    NotificationEvent.MarkAllAsRead
                )
            },
            onClearAll = {
                notificationViewModel.onEvent(
                    NotificationEvent.ClearAll
                )
            }
        )
    }
}


@Composable
private fun PromoAndGreetingSection(
    userName: String,
    profileImageUrl: String,
    loyaltyPoints: Int,
    hasUnreadNotifications: Boolean,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRewardsClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // ─── Top layer: Banner Margo Space (Static, No Slider) ─────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp)
                .height(300.dp)
                .background(Color(0xFF004AAD))
        ) {
            Image(
                painter = painterResource(id = R.drawable.banner_margo),
                contentDescription = "Banner Margo Space",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // ─── Notification Icon ────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(
                        top = 16.dp,
                        end = 20.dp
                    )
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Color.Black.copy(alpha = 0.4f)
                    )
                    .clickable(
                        onClick = onNotificationClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifikasi",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.error
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        // ─── Bottom layer: Greeting card ──────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .align(Alignment.BottomCenter)
                .zIndex(1f),

            shape =
                RoundedCornerShape(20.dp),

            colors =
                CardDefaults.cardColors(
                    containerColor = Color.White
                ),

            elevation =
                CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
        ) {

            Column(
                modifier =
                    Modifier.padding(20.dp)
            ) {

                // ─── User row ─────────────────────────────────────
                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hai $userName!",

                        style =
                            MaterialTheme.typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.ExtraBold
                    )

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onProfileClick)
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer
                                    .copy(alpha = 0.5f)
                            )
                            .border(
                                1.dp,

                                MaterialTheme
                                    .colorScheme
                                    .primary
                                    .copy(alpha = 0.2f),

                                CircleShape
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {
                        if (profileImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Profil",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                // ─── Loyalty Points ───────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(24.dp)
                        )
                        .border(
                            1.dp,

                            MaterialTheme
                                .colorScheme
                                .outlineVariant,

                            RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            onRewardsClick()
                        }
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        ),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.Star,

                            contentDescription = null,

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,

                            modifier =
                                Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(6.dp)
                        )

                        Text(
                            text =
                                "$loyaltyPoints Poin",

                            style =
                                MaterialTheme.typography
                                    .bodyMedium,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Tukar Poin",

                        style =
                            MaterialTheme.typography
                                .labelMedium,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            MaterialTheme.colorScheme
                                .primary
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
    val categories = listOf(
        "Semua",
        "Coffee",
        "Non-Coffee"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),

        horizontalArrangement =
            Arrangement.spacedBy(8.dp),

        contentPadding =
            PaddingValues(vertical = 4.dp)
    ) {
        items(
            items = categories,
            key = { it }
        ) { category ->

            FilterChip(
                selected =
                    category == selectedCategory,

                onClick = {
                    onSelectCategory(category)
                },

                label = {
                    Text(
                        text = category
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationBottomSheet(
    notifications: List<AppNotification>,
    onDismiss: () -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onClearAll: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Notifikasi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                if (notifications.any { !it.isRead }) {
                    TextButton(onClick = onMarkAllAsRead) {
                        Text("Tandai semua dibaca")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum ada notifikasi",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = {
                                onNotificationClick(notification)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onClearAll,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Hapus Semua")
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val icon = when (notification.type) {
        NotificationType.ORDER -> Icons.Outlined.Receipt
        NotificationType.REWARD -> Icons.Outlined.CardGiftcard
        NotificationType.PROMO -> Icons.Outlined.LocalOffer
        NotificationType.POINTS -> Icons.Outlined.Star
        NotificationType.MEMBERSHIP -> Icons.Outlined.WorkspacePremium
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            }
        ),
        border = if (!notification.isRead) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
