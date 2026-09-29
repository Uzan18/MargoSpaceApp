package com.pemmob.margocoffe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.margocoffe.data.MockRepository
import com.pemmob.margocoffe.ui.components.QuantitySelector
import com.pemmob.margocoffe.ui.components.SelectableChip
import com.pemmob.margocoffe.ui.components.formatRupiah
import com.pemmob.margocoffe.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    coffeeId: Int,
    viewModel: AppViewModel,
    onBackClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    // Load detail when screen opens
    LaunchedEffect(coffeeId) {
        viewModel.loadCoffeeDetail(coffeeId)
    }

    val detailState by viewModel.detailState.collectAsState()
    val coffee = detailState.coffee

    if (coffee == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Minuman") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            // Bottom bar: Add to cart button with dynamic price
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        viewModel.addToCart()
                        onAddToCart()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Tambah ke Keranjang — ${formatRupiah(detailState.totalPrice)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
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
        ) {
            // ─── Drink Image ────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "☕", fontSize = 80.sp)
            }

            // ─── Drink Info ─────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Name and price
                Text(
                    text = coffee.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRupiah(coffee.price),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = coffee.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // ─── Ukuran Gelas ───────────────────────────────────
                OptionSectionTitle(title = "Ukuran Gelas")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MockRepository.sizeOptions.forEach { size ->
                        SelectableChip(
                            label = size.label,
                            isSelected = detailState.selectedSize == size,
                            onClick = { viewModel.selectSize(size) },
                            extraInfo = if (size.extraPrice > 0) "+${formatRupiah(size.extraPrice)}" else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ─── Tingkat Es ─────────────────────────────────────
                OptionSectionTitle(title = "Tingkat Es")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MockRepository.iceLevelOptions.forEach { ice ->
                        SelectableChip(
                            label = ice.label,
                            isSelected = detailState.selectedIceLevel == ice,
                            onClick = { viewModel.selectIceLevel(ice) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ─── Tingkat Manis ──────────────────────────────────
                OptionSectionTitle(title = "Tingkat Manis")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MockRepository.sweetnessOptions.forEach { sweet ->
                        SelectableChip(
                            label = sweet.label,
                            isSelected = detailState.selectedSweetness == sweet,
                            onClick = { viewModel.selectSweetness(sweet) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ─── Quantity ────────────────────────────────────────
                OptionSectionTitle(title = "Jumlah")
                Spacer(modifier = Modifier.height(12.dp))
                QuantitySelector(
                    quantity = detailState.quantity,
                    onIncrement = { viewModel.incrementQuantity() },
                    onDecrement = { viewModel.decrementQuantity() }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun OptionSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}
