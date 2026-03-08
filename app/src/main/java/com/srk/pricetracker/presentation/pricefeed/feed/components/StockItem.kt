package com.srk.pricetracker.presentation.pricefeed.feed.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.srk.pricetracker.presentation.pricefeed.model.StockUiModel
import com.srk.pricetracker.ui.theme.PriceTrackerTheme

@Composable
fun StockItem(
    stock: StockUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val flashColor = remember { Animatable(Color.Transparent) }
    var lastObservedPrice by remember { mutableDoubleStateOf(stock.price) }

    LaunchedEffect(stock.price) {
        if (stock.price != lastObservedPrice) {
            val color = if (stock.isUp) {
                Color.Green.copy(alpha = 0.15f)
            } else {
                Color.Red.copy(alpha = 0.15f)
            }
            flashColor.snapTo(color)
            flashColor.animateTo(Color.Transparent, animationSpec = tween(1000))
            lastObservedPrice = stock.price
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Market",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        // Optimization: Use drawBehind to avoid recomposition during color animation
                        .drawBehind {
                            if (flashColor.value != Color.Transparent) {
                                drawRoundRect(
                                    color = flashColor.value,
                                    cornerRadius = CornerRadius(6.dp.toPx())
                                )
                            }
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    val priceText = remember(stock.price) { String.format("$%.2f", stock.price) }
                    Text(
                        text = priceText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    val hasChanged = stock.hasChanged
                    Box(modifier = Modifier.alpha(if (hasChanged) 1f else 0f)) {
                        PriceChangeIndicator(isUp = stock.isUp)
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun PriceChangeIndicator(isUp: Boolean) {
    val (text, color) = remember(isUp) {
        if (isUp) {
            "↑" to Color(0xFF4CAF50)
        } else {
            "↓" to Color(0xFFF44336)
        }
    }
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Black
    )
}

@Preview(showBackground = true)
@Composable
private fun StockItemUpPreview() {
    PriceTrackerTheme {
        Surface {
            StockItem(
                stock = StockUiModel("AAPL", 150.25, 145.10),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StockItemDownPreview() {
    PriceTrackerTheme {
        Surface {
            StockItem(
                stock = StockUiModel("GOOG", 2800.50, 2810.00),
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StockItemNoChangePreview() {
    PriceTrackerTheme {
        Surface {
            StockItem(
                stock = StockUiModel("MSFT", 300.00, 300.00),
                onClick = {}
            )
        }
    }
}
