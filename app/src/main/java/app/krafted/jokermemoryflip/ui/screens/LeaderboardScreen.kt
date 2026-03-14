package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import app.krafted.jokermemoryflip.data.MatchRecord
import app.krafted.jokermemoryflip.data.WinnerStat
import app.krafted.jokermemoryflip.ui.theme.*
import app.krafted.jokermemoryflip.viewmodel.LeaderboardViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    onBackClick: () -> Unit
) {
    val topWinners by viewModel.topWinners.collectAsState()
    val recentMatches by viewModel.recentMatches.collectAsState()

    // ── Entrance animations ──────────────────────────────────────────────────
    val screenAlpha   = remember { Animatable(0f) }
    val headerOffsetY = remember { Animatable(-50f) }
    val listAlpha     = remember { Animatable(0f) }
    val listOffsetY   = remember { Animatable(40f) }

    LaunchedEffect(Unit) {
        launch { screenAlpha.animateTo(1f, tween(300)) }
        launch {
            headerOffsetY.animateTo(
                0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
            )
        }
        launch {
            delay(180)
            listAlpha.animateTo(1f, tween(420))
        }
        launch {
            delay(180)
            listOffsetY.animateTo(0f, tween(420, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = screenAlpha.value }
    ) {
        // ── Background (matches HomeScreen pattern) ───────────────────────────
        Image(
            painter = painterResource(R.drawable.bg_round_5),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.25f,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(BackgroundVignette))
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
        ) {
            // ── Header (slides from top) ──────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = headerOffsetY.value }
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Back button row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BackButton(onClick = onBackClick)
                    Spacer(modifier = Modifier.weight(1f))
                    // Crown icon as decorative header badge
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(listOf(MidPurple, DeepPurple))
                            )
                            .border(
                                1.dp,
                                GoldAccent.copy(alpha = 0.35f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.card_crown),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LeaderGoldDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "HALL OF JOKERS",
                    color = GoldAccent,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Only the sharpest memories survive",
                    color = GoldLight.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))
                LeaderGoldDivider()
                Spacer(modifier = Modifier.height(14.dp))
            }

            // ── Content (fades + slides up) ───────────────────────────────────
            if (topWinners.isEmpty() && recentMatches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = listAlpha.value
                            translationY = listOffsetY.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.card_joker),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(56.dp),
                            alpha = 0.35f
                        )
                        Text(
                            text = "No matches played yet",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Play a game to write your legend",
                            color = GoldLight.copy(alpha = 0.3f),
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = listAlpha.value
                            translationY = listOffsetY.value
                        }
                ) {
                    if (topWinners.isNotEmpty()) {
                        item { LeaderSectionHeader(title = "TOP WINNERS") }

                        itemsIndexed(topWinners) { index, winner ->
                            WinnerRow(rank = index + 1, winner = winner)
                        }

                        item { Spacer(modifier = Modifier.height(6.dp)) }
                    }

                    if (recentMatches.isNotEmpty()) {
                        item { LeaderSectionHeader(title = "RECENT MATCHES") }

                        itemsIndexed(recentMatches) { _, match ->
                            RecentMatchRow(match = match)
                        }
                    }
                }
            }
        }
    }
}

// ── Back button (matches HomeActionButton style) ───────────────────────────────

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.92f else 1f
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .shadow(
                elevation = if (isPressed) 1.dp else 6.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(listOf(MidPurple, DeepPurple))
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(GoldAccent.copy(alpha = 0.4f), GoldAccent.copy(alpha = 0.1f))
                ),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = GoldAccent.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = GoldAccent,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ── Section header ─────────────────────────────────────────────────────────────

@Composable
private fun LeaderSectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(GoldAccent.copy(alpha = 0.2f))
        )
        Text(
            text = title,
            color = GoldAccent.copy(alpha = 0.75f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.5.sp
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(GoldAccent.copy(alpha = 0.2f))
        )
    }
}

// ── Gold divider (matches StealScreen) ────────────────────────────────────────

@Composable
private fun LeaderGoldDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(0.72f),
        thickness = 1.dp,
        color = GoldAccent.copy(alpha = 0.35f)
    )
}

// ── Winner row ─────────────────────────────────────────────────────────────────

@Composable
private fun WinnerRow(rank: Int, winner: WinnerStat) {
    val isChampion = rank == 1
    val rankColor = when (rank) {
        1    -> GoldAccent
        2    -> Color(0xFFC0C0C0)
        3    -> Color(0xFFCD7F32)
        else -> Color.White.copy(alpha = 0.45f)
    }
    val cardShape = RoundedCornerShape(14.dp)
    val bgBrush = if (isChampion) {
        Brush.horizontalGradient(
            listOf(RichPurple.copy(alpha = 0.7f), MidPurple, RichPurple.copy(alpha = 0.5f))
        )
    } else {
        Brush.verticalGradient(listOf(MidPurple.copy(alpha = 0.6f), CardSurface))
    }
    val borderBrush = if (isChampion) {
        Brush.linearGradient(
            listOf(GoldAccent.copy(alpha = 0.85f), NeonPurple.copy(alpha = 0.3f), GoldAccent.copy(alpha = 0.85f))
        )
    } else {
        Brush.linearGradient(
            listOf(rankColor.copy(alpha = 0.3f), rankColor.copy(alpha = 0.1f))
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(bgBrush)
            .border(
                width = if (isChampion) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = cardShape
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Rank badge
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(rankColor.copy(alpha = 0.12f))
                .border(
                    width = if (isChampion) 1.5.dp else 1.dp,
                    color = rankColor.copy(alpha = if (isChampion) 0.9f else 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (rank <= 3) listOf("🥇", "🥈", "🥉")[rank - 1] else "#$rank",
                color = rankColor,
                fontSize = if (rank <= 3) 18.sp else 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Winner name
        Text(
            text = winner.winnerName,
            color = if (isChampion) GoldLight else Color.White.copy(alpha = 0.88f),
            fontSize = 16.sp,
            fontWeight = if (isChampion) FontWeight.ExtraBold else FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Win count
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${winner.wins}",
                color = rankColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (winner.wins == 1) "win" else "wins",
                color = Color.White.copy(alpha = 0.38f),
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ── Recent match row ───────────────────────────────────────────────────────────

@Composable
private fun RecentMatchRow(match: MatchRecord) {
    val dateStr = remember(match.date) {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(match.date))
    }
    val modePill = match.gameMode.replace("_", " ")
    val cardShape = RoundedCornerShape(12.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Brush.verticalGradient(listOf(DeepPurple, CardSurface)))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.07f),
                shape = cardShape
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Score bubble
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(listOf(MidPurple, DeepPurple))
                )
                .border(
                    1.dp,
                    GoldAccent.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${match.winnerPairs}-${match.loserPairs}",
                color = GoldAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Names
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = match.winnerName,
                    color = GoldLight.copy(alpha = 0.92f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = "  beat  ",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = match.loserName,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mode pill badge (matches VictimBadge pattern)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(RichPurple.copy(alpha = 0.6f))
                        .border(
                            0.5.dp,
                            GoldDark.copy(alpha = 0.4f),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = modePill,
                        color = GoldLight.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                if (match.aiDifficulty != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MidPurple.copy(alpha = 0.5f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = match.aiDifficulty.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            color = NeonPurple.copy(alpha = 0.75f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Date
        Text(
            text = dateStr,
            color = Color.White.copy(alpha = 0.28f),
            fontSize = 11.sp,
            textAlign = TextAlign.End
        )
    }
}
