package app.krafted.jokermemoryflip.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.ui.theme.DeepPurple
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.GoldLight
import app.krafted.jokermemoryflip.ui.theme.MidPurple
import kotlinx.coroutines.launch

@Composable
fun JokerGirl(
    modifier: Modifier = Modifier,
    reaction: JokerGirlReaction = JokerGirlReaction.IDLE,
    animateEntrance: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "JokerGirlIdle")

    val bobY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val entranceScale = remember { Animatable(if (animateEntrance) 0.8f else 1f) }
    val entranceAlpha = remember { Animatable(if (animateEntrance) 0f else 1f) }

    LaunchedEffect(Unit) {
        if (animateEntrance) {
            launch {
                entranceAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
            }
            entranceScale.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
    }

    val imageRes = when (reaction) {
        JokerGirlReaction.IDLE -> R.drawable.joker_girl_1
        JokerGirlReaction.CELEBRATE -> R.drawable.joker_girl_2
        JokerGirlReaction.TAUNT -> R.drawable.joker_girl_3
        JokerGirlReaction.DRAMATIC -> R.drawable.joker_girl_4
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.64f)
                .aspectRatio(1f)
                .graphicsLayer {
                    translationY = bobY
                    scaleX = entranceScale.value
                    scaleY = entranceScale.value
                    alpha = entranceAlpha.value
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldAccent.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .padding(14.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MidPurple.copy(alpha = 0.94f),
                            DeepPurple.copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            GoldLight.copy(alpha = 0.9f),
                            GoldAccent.copy(alpha = 0.65f),
                            Color.White.copy(alpha = 0.22f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Joker Girl",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Transparent)
            )
        }
    }
}

enum class JokerGirlReaction {
    IDLE,
    CELEBRATE,
    TAUNT,
    DRAMATIC
}
