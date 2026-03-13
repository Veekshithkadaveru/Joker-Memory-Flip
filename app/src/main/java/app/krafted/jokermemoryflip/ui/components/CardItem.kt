package app.krafted.jokermemoryflip.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.game.CardState
import app.krafted.jokermemoryflip.game.GameConstants
import app.krafted.jokermemoryflip.ui.theme.CardSurface
import app.krafted.jokermemoryflip.ui.theme.DeepPurple
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.MatchGreen
import app.krafted.jokermemoryflip.ui.theme.MidPurple
import app.krafted.jokermemoryflip.ui.theme.MismatchRed
import app.krafted.jokermemoryflip.ui.theme.RichPurple
import kotlinx.coroutines.launch

@Composable
fun CardItem(
    card: CardState,
    isFlipping: Boolean = false,
    isMismatch: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(12.dp)

    val rotation = remember { Animatable(if (card.isFaceUp) 180f else 0f) }

    LaunchedEffect(card.isFaceUp) {
        rotation.animateTo(
            targetValue = if (card.isFaceUp) 180f else 0f,
            animationSpec = tween(
                durationMillis = GameConstants.FLIP_TOTAL_DURATION_MS,
                easing = FastOutSlowInEasing
            )
        )
    }

    val flipScaleX = if (rotation.value <= 90f) {
        1f - (rotation.value / 90f)
    } else {
        (rotation.value - 90f) / 90f
    }
    val showFace = rotation.value > 90f

    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(isMismatch) {
        if (isMismatch) {
            val shakeSequence = listOf(10f, -10f, 8f, -8f, 4f, -4f, 0f)
            for (target in shakeSequence) {
                shakeOffset.animateTo(
                    targetValue = target,
                    animationSpec = tween(durationMillis = 60, easing = FastOutSlowInEasing)
                )
            }
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    val collectScale = remember { Animatable(1f) }

    LaunchedEffect(card.isCollected) {
        if (card.isCollected) {
            launch {
                collectScale.animateTo(
                    targetValue = GameConstants.MATCH_SCALE_FACTOR,
                    animationSpec = tween(
                        durationMillis = (GameConstants.MATCH_GLOW_MS / 2).toInt(),
                        easing = FastOutSlowInEasing
                    )
                )
                collectScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = (GameConstants.MATCH_GLOW_MS / 2).toInt(),
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    val isClickable = !card.isFaceUp && !card.isCollected
    val contentAlpha = if (card.isCollected) 0.4f else 1f

    fun Modifier.glowBorder(
        glowColor: Color,
        borderWidth: Dp = 2.dp,
        glowRadius: Dp = 8.dp
    ): Modifier = this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        glowRadius.toPx(), 0f, 0f,
                        glowColor.copy(alpha = 0.9f).toArgb()
                    )
                }
            }
            canvas.drawRoundRect(
                left = -borderWidth.toPx(),
                top = -borderWidth.toPx(),
                right = size.width + borderWidth.toPx(),
                bottom = size.height + borderWidth.toPx(),
                radiusX = 12.dp.toPx(),
                radiusY = 12.dp.toPx(),
                paint = paint
            )
        }
    }

    val glowModifier = when {
        card.isCollected -> Modifier.glowBorder(MatchGreen, 2.dp, 12.dp)
        isMismatch       -> Modifier.glowBorder(MismatchRed, 2.dp, 10.dp)
        showFace         -> Modifier.glowBorder(GoldAccent, 1.dp, 6.dp)
        else             -> Modifier
    }

    val borderModifier = when {
        card.isCollected -> Modifier.border(
            2.dp,
            Brush.linearGradient(listOf(MatchGreen.copy(alpha = 0.8f), GoldAccent.copy(alpha = 0.5f))),
            cardShape
        )
        isMismatch -> Modifier.border(2.dp, MismatchRed.copy(alpha = 0.8f), cardShape)
        else -> Modifier
    }

    Card(
        modifier = modifier
            .aspectRatio(0.714f)
            .graphicsLayer {
                translationX = shakeOffset.value
                this.scaleX = collectScale.value
                scaleY = collectScale.value
            }
            .then(glowModifier)
            .then(borderModifier)
            .clickable(
                enabled = isClickable,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isCollected) 2.dp else 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.scaleX = flipScaleX
                    alpha = contentAlpha
                },
            contentAlignment = Alignment.Center
        ) {
            if (!showFace) {
                CardBack(isCollected = card.isCollected, shape = cardShape)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(MidPurple, CardSurface)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = card.symbol.drawableRes),
                        contentDescription = card.symbol.displayName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CardBack(isCollected: Boolean, shape: RoundedCornerShape) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        DeepPurple,
                        RichPurple.copy(alpha = 0.9f),
                        DeepPurple
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            RichPurple.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            GoldAccent.copy(alpha = if (isCollected) 0.15f else 0.35f),
                            GoldAccent.copy(alpha = if (isCollected) 0.05f else 0.15f),
                            GoldAccent.copy(alpha = if (isCollected) 0.15f else 0.35f)
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Image(
            painter = painterResource(id = R.drawable.card_joker),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer { alpha = if (isCollected) 0.08f else 0.18f }
        )
    }
}
