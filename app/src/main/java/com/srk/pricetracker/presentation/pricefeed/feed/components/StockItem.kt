package com.srk.pricetracker.presentation.pricefeed.feed.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.srk.pricetracker.presentation.pricefeed.model.Stock
import com.srk.pricetracker.ui.theme.PriceTrackerTheme

@Composable
fun StockItem(
    stock: Stock,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val flashColor = remember { Animatable(Color.Transparent) }

    LaunchedEffect(stock.price) {
        if (stock.price != stock.previousPrice) {
            val color = if (stock.isUp) {
                Color.Green.copy(alpha = 0.2f)
            } else {
                Color.Red.copy(alpha = 0.2f)
            }
            flashColor.snapTo(color)
            flashColor.animateTo(Color.Transparent, animationSpec = tween(1000))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stock.symbol,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(flashColor.value, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = String.format("%.2f", stock.price),
                style = MaterialTheme.typography.bodyLarge
            )
            
            // We always keep the spacer and indicator in the layout to prevent "jumping"
            // but we hide them using alpha if there is no price change.
            val hasChange = stock.price != stock.previousPrice
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.alpha(if (hasChange) 1f else 0f)) {
                PriceChangeIndicator(isUp = stock.isUp)
            }
        }
    }
}

@Composable
private fun PriceChangeIndicator(isUp: Boolean) {
    val (text, color) = if (isUp) {
        "↑" to Color.Green
    } else {
        "↓" to Color.Red
    }
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
private fun StockItemUpPreview() {
    PriceTrackerTheme {
        Surface {
            StockItem(
                stock = Stock("AAPL", 150.0, 145.0),
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
                stock = Stock("GOOG", 2800.0, 2810.0),
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
                stock = Stock("MSFT", 300.0, 300.0),
                onClick = {}
            )
        }
    }
}
