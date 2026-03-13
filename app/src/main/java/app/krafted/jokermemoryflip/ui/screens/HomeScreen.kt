package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.ui.components.JokerGirl
import app.krafted.jokermemoryflip.ui.components.JokerGirlReaction
import app.krafted.jokermemoryflip.ui.theme.BackgroundVignette
import app.krafted.jokermemoryflip.ui.theme.CardSurface
import app.krafted.jokermemoryflip.ui.theme.DarkPurple
import app.krafted.jokermemoryflip.ui.theme.DeepPurple
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.GoldDark
import app.krafted.jokermemoryflip.ui.theme.GoldLight
import app.krafted.jokermemoryflip.ui.theme.MidPurple
import app.krafted.jokermemoryflip.ui.theme.OverlayBlack
import app.krafted.jokermemoryflip.ui.theme.RichPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onPlayVsAiClick: () -> Unit,
    onPlayVsPlayerClick: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    val heroAlpha = remember { Animatable(0f) }
    val heroOffset = remember { Animatable(18f) }
    val actionsAlpha = remember { Animatable(0f) }
    val actionsOffset = remember { Animatable(20f) }

    LaunchedEffect(Unit) {
        launch {
            heroAlpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
        }
        launch {
            heroOffset.animateTo(0f, tween(600, easing = FastOutSlowInEasing))
        }
        delay(200)
        launch {
            actionsAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }
        actionsOffset.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurple)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_round_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.25f
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundVignette)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            OverlayBlack.copy(alpha = 0.10f),
                            Color.Transparent,
                            DarkPurple.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = heroAlpha.value
                    translationY = heroOffset.value
                }
            ) {
                JokerGirl(
                    reaction = JokerGirlReaction.IDLE,
                    animateEntrance = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "JOKER",
                    style = androidx.compose.ui.text.TextStyle(
                        brush = Brush.verticalGradient(
                            colors = listOf(GoldLight, GoldAccent, GoldDark)
                        ),
                        fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 12.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = GoldAccent.copy(alpha = 0.5f),
                            offset = androidx.compose.ui.geometry.Offset(0f, 8f),
                            blurRadius = 12f
                        )
                    ),
                    modifier = Modifier.graphicsLayer(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "MEMORY FLIP",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp,
                        shadow = androidx.compose.ui.graphics.Shadow(
                            color = Color.Black.copy(alpha = 0.8f),
                            offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                            blurRadius = 8f
                        )
                    ),
                    modifier = Modifier.graphicsLayer(alpha = 0.85f),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = actionsAlpha.value
                        translationY = actionsOffset.value
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeActionButton(
                    title = "Play vs AI",
                    icon = R.drawable.card_joker,
                    containerBrush = Brush.horizontalGradient(
                        colors = listOf(RichPurple.copy(alpha = 0.8f), DeepPurple.copy(alpha = 0.9f))
                    ),
                    borderColor = GoldAccent.copy(alpha = 0.4f),
                    onClick = onPlayVsAiClick
                )
                HomeActionButton(
                    title = "Play vs Player",
                    icon = R.drawable.card_heart,
                    containerBrush = Brush.horizontalGradient(
                        colors = listOf(DeepPurple.copy(alpha = 0.9f), CardSurface.copy(alpha = 0.8f))
                    ),
                    borderColor = Color.White.copy(alpha = 0.15f),
                    onClick = onPlayVsPlayerClick
                )
                HomeActionButton(
                    title = "Leaderboard",
                    icon = R.drawable.card_crown,
                    containerBrush = Brush.horizontalGradient(
                        colors = listOf(CardSurface.copy(alpha = 0.8f), MidPurple.copy(alpha = 0.8f))
                    ),
                    borderColor = GoldDark.copy(alpha = 0.3f),
                    onClick = onLeaderboardClick
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "VERSION 1.0",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = Color.White.copy(alpha = 0.25f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    title: String,
    icon: Int,
    containerBrush: Brush,
    borderColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.95f else 1f
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.6f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(shape)
            .background(containerBrush)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.CenterStart
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Go",
                    tint = GoldAccent.copy(alpha = 0.9f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}