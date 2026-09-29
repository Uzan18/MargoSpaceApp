package com.pemmob.margocoffe.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Custom segmented toggle button for Dine-in / Take-away selection.
 * "Makan di Sini" vs "Bawa Pulang"
 */
@Composable
fun DineInToggle(
    isDineIn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = Color.Transparent
    val selectedTextColor = Color.White
    val unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Makan di Sini
        val dineInBg by animateColorAsState(
            targetValue = if (isDineIn) selectedColor else unselectedColor,
            label = "dineInBg"
        )
        val dineInText by animateColorAsState(
            targetValue = if (isDineIn) selectedTextColor else unselectedTextColor,
            label = "dineInText"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(dineInBg)
                .clickable { onToggle(true) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Makan di Sini",
                color = dineInText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isDineIn) FontWeight.Bold else FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Bawa Pulang
        val takeAwayBg by animateColorAsState(
            targetValue = if (!isDineIn) selectedColor else unselectedColor,
            label = "takeAwayBg"
        )
        val takeAwayText by animateColorAsState(
            targetValue = if (!isDineIn) selectedTextColor else unselectedTextColor,
            label = "takeAwayText"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(takeAwayBg)
                .clickable { onToggle(false) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Bawa Pulang",
                color = takeAwayText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (!isDineIn) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
