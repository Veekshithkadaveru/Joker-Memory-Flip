package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.game.CardSymbol
import app.krafted.jokermemoryflip.ui.theme.CardSurface
import app.krafted.jokermemoryflip.ui.theme.DarkPurple
import app.krafted.jokermemoryflip.ui.theme.DeepPurple
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.GoldDark
import app.krafted.jokermemoryflip.ui.theme.GoldLight
import app.krafted.jokermemoryflip.ui.theme.MidPurple
import app.krafted.jokermemoryflip.ui.theme.NeonPurple
import app.krafted.jokermemoryflip.ui.theme.RichPurple
import kotlinx.coroutines.launch

@Composable
fun StealScreen(
    stealingPlayerName: String,
    victimPlayerName: String,
    victimPairs: List<CardSymbol>,
    onSymbolChosen: (CardSymbol) -> Unit
) {
    // Guard: block multiple taps once a card is chosen
    var stolen by remember { mutableStateOf(false) }

    // Entrance animations
    val screenAlpha   = remember { Animatable(0f) }
    val headerOffsetY = remember { Animatable(-60f) }
    val badgeScale    = remember { Animatable(0.7f) }
    val cardsOffsetY  = remember { Animatable(80f) }

    LaunchedEffect(Unit) {
        launch { screenAlpha.animateTo(1f, tween(280)) }
        launch {
            headerOffsetY.animateTo(
                0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
            )
        }
        launch {
            badgeScale.animateTo(
                1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)
            )
        }
        cardsOffsetY.animateTo(
            0f, tween(380, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = screenAlpha.value }
    ) {
        // ── Background: joker_girl_3 fills the entire screen ─────────────────
        Image(
            painter = painterResource(R.drawable.joker_girl_3),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark overlay — heavy enough so all text is readable everywhere
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkPurple.copy(alpha = 0.82f))
        )

        // ── Foreground content ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Header ────────────────────────────────────────────────────────
            Spacer(Modifier.height(12.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = headerOffsetY.value }
            ) {
                GoldDivider()
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "JOKER  STEAL",
                    color = GoldAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "The Joker's power is yours",
                    color = GoldLight.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                GoldDivider()
            }

            Spacer(Modifier.height(18.dp))

            // ── Victim badge ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = badgeScale.value
                        scaleY = badgeScale.value
                    }
            ) {
                VictimBadge(
                    stealingPlayerName = stealingPlayerName,
                    victimPlayerName = victimPlayerName
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Instruction ───────────────────────────────────────────────────
            Text(
                text = if (victimPairs.isEmpty()) "Nothing to steal — you still win the match!"
                       else "Tap a card to steal it",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = cardsOffsetY.value }
            )

            Spacer(Modifier.height(14.dp))

            // ── Card grid ─────────────────────────────────────────────────────
            if (victimPairs.isNotEmpty()) {
                val columns = when (victimPairs.size) {
                    1    -> 1
                    2    -> 2
                    else -> 3
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .graphicsLayer { translationY = cardsOffsetY.value }
                ) {
                    itemsIndexed(victimPairs) { _, symbol ->
                        StealableCard(
                            symbol = symbol,
                            enabled = !stolen,
                            onSteal = {
                                if (!stolen) {
                                    stolen = true
                                    onSymbolChosen(symbol)
                                }
                            }
                        )
                    }
                }
            } else {
                // No pairs to steal — show a dismiss button so the screen is not a dead-end
                Spacer(Modifier.weight(1f))
                val continueShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .graphicsLayer { translationY = cardsOffsetY.value }
                        .clip(continueShape)
                        .background(
                            Brush.horizontalGradient(listOf(GoldDark, GoldAccent, GoldDark))
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = !stolen
                        ) {
                            if (!stolen) {
                                stolen = true
                                onSymbolChosen(CardSymbol.JOKER)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CONTINUE",
                        color = DarkPurple,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Victim badge ──────────────────────────────────────────────────────────────

@Composable
private fun VictimBadge(stealingPlayerName: String, victimPlayerName: String) {
    val badgeShape = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(badgeShape)
            .background(
                Brush.horizontalGradient(
                    listOf(RichPurple.copy(alpha = 0.8f), MidPurple, RichPurple.copy(alpha = 0.8f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(GoldDark.copy(alpha = 0.4f), GoldAccent.copy(alpha = 0.9f), GoldDark.copy(alpha = 0.4f))
                ),
                shape = badgeShape
            )
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        // Stealer avatar dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(GoldAccent, CircleShape)
        )

        Text(
            text = stealingPlayerName.uppercase(),
            color = GoldAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )

        Text(
            text = "raids",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontStyle = FontStyle.Italic
        )

        Text(
            text = victimPlayerName.uppercase(),
            color = NeonPurple.copy(alpha = 0.9f),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp
        )

        // Victim avatar dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(NeonPurple.copy(alpha = 0.8f), CircleShape)
        )
    }
}

// ── Gold divider line ─────────────────────────────────────────────────────────

@Composable
private fun GoldDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(0.72f),
        thickness = 1.dp,
        color = GoldAccent.copy(alpha = 0.35f)
    )
}

// ── Stealable card ────────────────────────────────────────────────────────────

@Composable
private fun StealableCard(
    symbol: CardSymbol,
    enabled: Boolean,
    onSteal: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val pressScale = remember { Animatable(1f) }
    val glowAlpha  = remember { Animatable(0f) }
    val cardShape  = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .aspectRatio(0.75f)
            .graphicsLayer {
                scaleX = pressScale.value
                scaleY = pressScale.value
            }
            // Gold glow drawn behind the card
            .drawBehind {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        listOf(
                            GoldAccent.copy(alpha = glowAlpha.value * 0.6f),
                            Color.Transparent
                        )
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                )
            }
            .clip(cardShape)
            .background(
                Brush.verticalGradient(listOf(MidPurple, DeepPurple, CardSurface))
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        GoldAccent.copy(alpha = 0.75f),
                        NeonPurple.copy(alpha = 0.4f),
                        GoldAccent.copy(alpha = 0.75f)
                    )
                ),
                shape = cardShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled
            ) {
                coroutineScope.launch {
                    // Glow pulse
                    launch { glowAlpha.animateTo(1f, tween(120)) }
                    // Press shrink → spring back
                    pressScale.animateTo(0.86f, tween(90))
                    pressScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy))
                    glowAlpha.animateTo(0f, tween(180))
                    onSteal()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            // Card image
            Image(
                painter = painterResource(id = symbol.drawableRes),
                contentDescription = symbol.displayName,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(Modifier.height(6.dp))

            // Thin gold separator
            HorizontalDivider(
                thickness = 0.5.dp,
                color = GoldAccent.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            Spacer(Modifier.height(5.dp))

            // Symbol name
            Text(
                text = symbol.displayName.uppercase(),
                color = GoldLight.copy(alpha = 0.85f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
