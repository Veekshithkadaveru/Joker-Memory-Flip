package app.krafted.jokermemoryflip.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.game.CardSymbol
import app.krafted.jokermemoryflip.ui.theme.CardSurface
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.GoldDark
import app.krafted.jokermemoryflip.ui.theme.MidPurple

private const val MAX_VISIBLE_SYMBOLS = 7

@Composable
fun ScoreDisplay(
    playerName: String,
    collectedPairs: List<CardSymbol>,
    isActive: Boolean,
    isTopPlayer: Boolean = false,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(12.dp)
    val score by animateIntAsState(
        targetValue = collectedPairs.size,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 250f
        ),
        label = "scoreCount"
    )

    val containerModifier = modifier
        .clip(containerShape)
        .background(
            color = CardSurface.copy(alpha = 0.85f),
            shape = containerShape
        )
        .then(
            if (isActive) {
                Modifier.border(
                    width = 1.dp,
                    color = GoldAccent.copy(alpha = 0.7f),
                    shape = containerShape
                )
            } else {
                Modifier.border(
                    width = 1.dp,
                    color = MidPurple.copy(alpha = 0.4f),
                    shape = containerShape
                )
            }
        )
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .animateContentSize()

    val content: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = playerName.take(12),
                color = if (isActive) GoldAccent else Color.Gray.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(2.dp))

            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (isActive) GoldAccent else Color.DarkGray.copy(alpha = 0.6f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.toString(),
                    color = if (isActive) GoldDark else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            if (collectedPairs.isNotEmpty()) {
                val visibleSymbols = collectedPairs.take(MAX_VISIBLE_SYMBOLS)
                val hasOverflow = collectedPairs.size > MAX_VISIBLE_SYMBOLS

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    visibleSymbols.forEach { symbol ->
                        Image(
                            painter = painterResource(id = symbol.drawableRes),
                            contentDescription = symbol.displayName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (hasOverflow) {
                        Text(
                            text = "...",
                            color = GoldAccent.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (isTopPlayer) {
        Box(
            modifier = containerModifier.graphicsLayer { rotationX = 180f }
        ) {
            content()
        }
    } else {
        Box(modifier = containerModifier) {
            content()
        }
    }
}
