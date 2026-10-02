package com.pemmob.margocoffe.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.pemmob.margocoffe.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.margocoffe.data.Coffee
import com.pemmob.margocoffe.ui.theme.BestSellerBadge
import com.pemmob.margocoffe.ui.theme.PriceText
import java.text.NumberFormat
import java.util.Locale

/**
 * Formats an integer price to Indonesian Rupiah format.
 * e.g. 20000 -> "Rp 20.000"
 */
fun formatRupiah(price: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    return "Rp ${formatter.format(price)}"
}

/**
 * Badge pill displayed over drinks (e.g. Terlaris, Rekomendasi).
 */
@Composable
fun DrinkBadge(
    badge: String,
    modifier: Modifier = Modifier
) {
    val isTerlaris = badge.equals("Terlaris", ignoreCase = true)
    val containerColor = if (isTerlaris) {
        MaterialTheme.colorScheme.primary // Deep Blue
    } else {
        Color(0xFFDBEAFE) // Soft Light Blue
    }
    val contentColor = if (isTerlaris) {
        Color.White
    } else {
        Color(0xFF1E40AF) // Darker blue text
    }

    Box(
        modifier = modifier
            .background(color = containerColor, shape = RoundedCornerShape(percent = 50))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isTerlaris) Icons.Default.LocalFireDepartment else Icons.Default.ThumbUp,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = badge,
                color = contentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Popular Coffee item card designed for horizontal LazyRow.
 * Matches the design with image, badge, title, 2-line description, and blue '+' button.
 */
@Composable
fun PopularCoffeeCard(
    coffee: Coffee,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .width(165.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (coffee.imageRes != 0) {
                    Image(
                        painter = painterResource(id = coffee.imageRes),
                        contentDescription = coffee.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else if (coffee.imageUrl.isNotBlank()) {
                    coil.compose.AsyncImage(
                        model = coffee.imageUrl,
                        contentDescription = coffee.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logomargo),
                        contentDescription = coffee.name,
                        modifier = Modifier
                            .size(76.dp)
                            .padding(4.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }

                // Badge (Terlaris / Rekomendasi)
                val badgeText = when {
                    coffee.badge.isNotBlank() -> coffee.badge
                    coffee.isBestSeller -> "Terlaris"
                    else -> ""
                }
                if (badgeText.isNotBlank()) {
                    DrinkBadge(
                        badge = badgeText,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = coffee.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Description
            Text(
                text = coffee.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price only
            Text(
                text = formatRupiah(coffee.price),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Coffee menu item card for the home screen grid based on new design.
 */
@Composable
fun CoffeeMenuCard(
    coffee: Coffee,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAddClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (coffee.imageRes != 0) {
                    Image(
                        painter = painterResource(id = coffee.imageRes),
                        contentDescription = coffee.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else if (coffee.imageUrl.isNotBlank()) {
                    coil.compose.AsyncImage(
                        model = coffee.imageUrl,
                        contentDescription = coffee.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logomargo),
                        contentDescription = coffee.name,
                        modifier = Modifier
                            .size(76.dp)
                            .padding(4.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }

                // Badge
                val badgeText = when {
                    coffee.badge.isNotBlank() -> coffee.badge
                    coffee.isBestSeller -> "Terlaris"
                    else -> ""
                }
                if (badgeText.isNotBlank()) {
                    DrinkBadge(
                        badge = badgeText,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info section
            Text(
                text = coffee.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = coffee.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price only
            Text(
                text = formatRupiah(coffee.price),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
