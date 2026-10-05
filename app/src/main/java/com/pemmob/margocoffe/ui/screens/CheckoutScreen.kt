package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.pemmob.margocoffe.R
import com.pemmob.margocoffe.data.CartItem
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.ui.components.DineInToggle
import com.pemmob.margocoffe.ui.components.formatRupiah
import com.pemmob.margocoffe.ui.theme.DividerColor
import com.pemmob.margocoffe.viewmodel.CheckoutViewModel
import com.pemmob.margocoffe.viewmodel.OrderEvent
import com.pemmob.margocoffe.viewmodel.OrderViewModel
import com.pemmob.margocoffe.viewmodel.CheckoutEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    orderViewModel: OrderViewModel,
    profileViewModel: com.pemmob.margocoffe.viewmodel.ProfileViewModel? = null,
    isAuthenticated: Boolean = true,
    onRequireLogin: () -> Unit = {},
    onBackClick: () -> Unit,
    onProceedPayment: () -> Unit
) {
    val checkoutState by viewModel.uiState.collectAsState()
    val orderState by orderViewModel.uiState.collectAsState()
    val profileData by com.pemmob.margocoffe.viewmodel.ProfileStore.data.collectAsState()

    var showRequireLoginDialog by remember { mutableStateOf(false) }
    var showVoucherDialog by remember { mutableStateOf(false) }

    // Pre-fill nama pelanggan jika belum diisi
    LaunchedEffect(profileData.name) {
        if (checkoutState.customerName.isBlank() && profileData.name.isNotBlank() && profileData.name != "Teman Margo") {
            viewModel.onEvent(CheckoutEvent.SetCustomerName(profileData.name))
        }
    }

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun hasLocationPermission(): Boolean {
        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        viewModel.onEvent(CheckoutEvent.RequestCurrentLocation)

        fusedLocationClient
            .getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            )
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.onEvent(
                        CheckoutEvent.SetCurrentLocation(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { lastLocation ->
                            if (lastLocation != null) {
                                viewModel.onEvent(
                                    CheckoutEvent.SetCurrentLocation(
                                        latitude = lastLocation.latitude,
                                        longitude = lastLocation.longitude
                                    )
                                )
                            } else {
                                viewModel.onEvent(
                                    CheckoutEvent.LocationError(
                                        "Lokasi tidak dapat ditemukan. Pastikan GPS aktif."
                                    )
                                )
                            }
                        }
                        .addOnFailureListener {
                            viewModel.onEvent(
                                CheckoutEvent.LocationError(
                                    "Gagal mengambil lokasi perangkat."
                                )
                            )
                        }
                }
            }
            .addOnFailureListener {
                viewModel.onEvent(
                    CheckoutEvent.LocationError(
                        "Gagal mengambil lokasi perangkat."
                    )
                )
            }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                fetchCurrentLocation()
            } else {
                viewModel.onEvent(
                    CheckoutEvent.LocationError(
                        "Izin lokasi diperlukan untuk mencari cabang Margo terdekat."
                    )
                )
            }
        }

    fun requestLocation() {
        if (hasLocationPermission()) {
            fetchCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Keranjang & Pembayaran")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    val isSubmitting = orderState.isSubmitting

                    Button(
                        onClick = {
                            if (!isAuthenticated) {
                                showRequireLoginDialog = true
                                return@Button
                            }
                            if (!isSubmitting) {
                                viewModel.onEvent(
                                    CheckoutEvent.ProceedPayment
                                )
                                orderViewModel.onEvent(
                                    OrderEvent.PlaceOrder(
                                        customerName = checkoutState.customerName,
                                        isDineIn = checkoutState.isDineIn,
                                        useFreeCoffee = checkoutState.useReward || checkoutState.selectedVoucher?.isFreeCoffee == true,
                                        discountAmount = checkoutState.rewardDiscount,
                                        appliedVoucherId = checkoutState.selectedVoucher?.id
                                    )
                                )
                                onProceedPayment()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        enabled = !isSubmitting &&
                                checkoutState.customerName.isNotBlank() &&
                                checkoutState.cartItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Memproses Pesanan...",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Bayar Sekarang",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // ─── Branch Header ──────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cabang Terdekat",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                    .copy(alpha = 0.7f)
                            )

                            Text(
                                text = checkoutState.branchName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            checkoutState.selectedBranch?.let { branch ->
                                Text(
                                    text = branch.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                        .copy(alpha = 0.75f)
                                )

                                checkoutState.distanceToBranchKm?.let { distance ->
                                    Text(
                                        text = if (distance < 1.0) {
                                            "${(distance * 1000).toInt()} m dari lokasi kamu"
                                        } else {
                                            String.format(
                                                java.util.Locale.US,
                                                "%.2f km dari lokasi kamu",
                                                distance
                                            )
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { requestLocation() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !checkoutState.isLocationLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (checkoutState.isLocationLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mencari cabang terdekat...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gunakan lokasi saya")
                        }
                    }

                    checkoutState.locationError?.let { error ->
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Dine-in / Take-away Toggle ─────────────────────────
            Text(
                text = "Tipe Pesanan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            DineInToggle(
                isDineIn = checkoutState.isDineIn,
                onToggle = { isDineIn ->
                    viewModel.onEvent(
                        CheckoutEvent.SetDineIn(isDineIn)
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ─── Customer Name Input ────────────────────────────────
            Text(
                text = "Atas Nama",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val nameFieldShape = RoundedCornerShape(12.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(nameFieldShape)
                    .background(Color.White)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = nameFieldShape
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = checkoutState.customerName,
                    onValueChange = { name ->
                        viewModel.onEvent(
                            CheckoutEvent.SetCustomerName(name)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(
                        MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (checkoutState.customerName.isEmpty()) {
                                Text(
                                    text = "Masukkan nama untuk pemanggilan",
                                    color = Color(0xFF465264),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            innerTextField()
                        }
                    }
                )
            }

            if (checkoutState.customerName.isBlank()) {
                Text(
                    text = "* Wajib diisi untuk pemanggilan pesanan",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(
                        start = 4.dp,
                        top = 4.dp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ─── Cart Items ─────────────────────────────────────────
            Text(
                text = "Pesananmu",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (checkoutState.cartItems.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🛒",
                                fontSize = 40.sp
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Keranjang masih kosong",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

            } else {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        checkoutState.cartItems.forEachIndexed { index, item ->

                            CartItemRow(
                                item = item,
                                onDelete = {
                                    viewModel.onEvent(
                                        CheckoutEvent.RemoveItem(index)
                                    )
                                }
                            )

                            if (index < checkoutState.cartItems.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        vertical = 12.dp
                                    ),
                                    color = DividerColor
                                )
                            }
                        }
                    }
                }
            }

            // ─── Voucher / Reward Margo Space ────────────────────────
            if (checkoutState.cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocalOffer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Voucher & Poin Reward",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (checkoutState.availableVouchers.isNotEmpty()) {
                                            "${checkoutState.availableVouchers.size} voucher tersedia"
                                        } else {
                                            "Tukarkan poin reward di menu Hadiah"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (checkoutState.availableVouchers.isNotEmpty()) {
                                TextButton(
                                    onClick = { showVoucherDialog = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (checkoutState.selectedVoucher != null) "Ganti" else "Pilih",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Tampilan Voucher yang sedang aktif
                        if (checkoutState.selectedVoucher != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0FDF4))
                                    .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "VOUCHER DIGUNAKAN",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF16A34A)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = checkoutState.selectedVoucher!!.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF14532D)
                                        )
                                        Text(
                                            text = checkoutState.selectedVoucher!!.description,
                                            fontSize = 11.sp,
                                            color = Color(0xFF166534)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "-${formatRupiah(checkoutState.rewardDiscount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF16A34A)
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.onEvent(CheckoutEvent.SelectVoucher(null))
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus Voucher",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Opsi Tukar Poin Reward jika ada poin >= 10 dan belum pakai voucher
                        if (checkoutState.userPoints >= 10 && checkoutState.selectedVoucher == null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tukar ${checkoutState.userPoints} Poin Reward",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Potongan diskon ${formatRupiah((checkoutState.userPoints / 10) * 5000)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = checkoutState.usePoints,
                                    onCheckedChange = { isChecked ->
                                        viewModel.onEvent(CheckoutEvent.ToggleUsePoints(isChecked))
                                    }
                                )
                            }
                        }

                        // Opsi Reward Kopi Gratis dari Stamp (jika tersedia)
                        if (checkoutState.availableFreeCoffee > 0 && checkoutState.selectedVoucher == null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Reward Kopi Gratis (Stamp)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Tersedia ${checkoutState.availableFreeCoffee} reward siap pakai",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = checkoutState.useReward,
                                    onCheckedChange = { isChecked ->
                                        viewModel.onEvent(CheckoutEvent.ToggleUseReward(isChecked))
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ─── Order Summary ──────────────────────────────────────
            if (checkoutState.cartItems.isNotEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ringkasan Pembayaran",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow(
                            label = "Subtotal",
                            value = formatRupiah(
                                checkoutState.subtotal
                            )
                        )

                        if (checkoutState.rewardDiscount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val discountTitle = when {
                                    checkoutState.selectedVoucher != null -> "Diskon (${checkoutState.selectedVoucher!!.title})"
                                    checkoutState.usePoints -> "Diskon Poin Reward"
                                    else -> "Diskon Kopi Gratis"
                                }
                                Text(
                                    text = discountTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF16A34A),
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "-${formatRupiah(checkoutState.rewardDiscount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF16A34A)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        SummaryRow(
                            label = "PPN (10%)",
                            value = formatRupiah(
                                checkoutState.tax
                            )
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(
                                vertical = 12.dp
                            ),
                            color = DividerColor
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = formatRupiah(
                                    checkoutState.total
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(100.dp)
            )
        }
    }

    if (showRequireLoginDialog) {
        AlertDialog(
            onDismissRequest = { showRequireLoginDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Masuk Akun Terlebih Dahulu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Untuk memesan dan melakukan pembayaran, silakan masuk atau daftar akun Margo Space terlebih dahulu.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRequireLoginDialog = false
                        onRequireLogin()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Masuk / Daftar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRequireLoginDialog = false }
                ) {
                    Text("Nanti Saja")
                }
            }
        )
    }

    if (showVoucherDialog) {
        AlertDialog(
            onDismissRequest = { showVoucherDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Pilih Voucher Diskon",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (checkoutState.availableVouchers.isEmpty()) {
                        Text(
                            text = "Belum ada voucher tersedia. Kumpulkan poin dan tukarkan voucher di halaman Hadiah!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        checkoutState.availableVouchers.forEach { voucher ->
                            val isSelected = checkoutState.selectedVoucher?.id == voucher.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onEvent(CheckoutEvent.SelectVoucher(voucher))
                                        showVoucherDialog = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFF0FDF4) else Color(0xFFF8F9FA)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFF16A34A) else Color(0xFFE2E8F0)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = voucher.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color(0xFF14532D) else Color.Black
                                        )
                                        Text(
                                            text = voucher.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Dipilih",
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Gunakan",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoucherDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                if (checkoutState.selectedVoucher != null) {
                    TextButton(onClick = {
                        viewModel.onEvent(CheckoutEvent.SelectVoucher(null))
                        showVoucherDialog = false
                    }) {
                        Text("Batal Gunakan", color = Color.Red)
                    }
                }
            }
        )
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val fallbackCoffee = MockRepository.getCoffeeById(item.coffee.id)
            ?: MockRepository.coffeeMenu.find { it.name.equals(item.coffee.name, ignoreCase = true) }
        val resolvedImageRes = if (item.coffee.imageRes != 0) item.coffee.imageRes else (fallbackCoffee?.imageRes ?: 0)
        val resolvedImageUrl = if (item.coffee.imageUrl.isNotBlank()) item.coffee.imageUrl else (fallbackCoffee?.imageUrl ?: "")

        // Foto pesanan aktual
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF8F9FA))
                .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (resolvedImageRes != 0) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = resolvedImageRes),
                    contentDescription = item.coffee.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            } else if (resolvedImageUrl.isNotBlank()) {
                coil.compose.AsyncImage(
                    model = resolvedImageUrl,
                    contentDescription = item.coffee.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.logomargo),
                    contentDescription = item.coffee.name,
                    modifier = Modifier.size(36.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.coffee.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "${item.selectedSize.label} · " +
                        "${item.selectedIceLevel.label} · " +
                        item.selectedSweetness.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "${item.quantity}x ${
                    formatRupiah(
                        item.coffee.price +
                                item.selectedSize.extraPrice
                    )
                }",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = formatRupiah(item.totalPrice),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}