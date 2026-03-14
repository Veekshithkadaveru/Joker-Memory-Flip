package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.game.CardSymbol
import app.krafted.jokermemoryflip.game.Player
import app.krafted.jokermemoryflip.ui.theme.*
import app.krafted.jokermemoryflip.viewmodel.GameUiState
import app.krafted.jokermemoryflip.viewmodel.MatchResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    uiState: GameUiState,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    val winner = uiState.matchResult as? MatchResult.Winner ?: return
    val p1Pairs = uiState.collectedPairs[Player.ONE] ?: emptyList()
    val p2Pairs = uiState.collectedPairs[Player.TWO] ?: emptyList()

    val screenAlpha    = remember { Animatable(0f) }
    val headerOffsetY  = remember { Animatable(-50f) }
    val tallyAlpha     = remember { Animatable(0f) }
    val tallyOffsetY   = remember { Animatable(40f) }
    val buttonsAlpha   = remember { Animatable(0f) }
    val buttonsOffsetY = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        launch { screenAlpha.animateTo(1f, tween(320)) }
        launch {
            headerOffsetY.animateTo(
                0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)
            )
        }
        delay(220)
        launch { tallyAlpha.animateTo(1f, tween(380)) }
        launch { tallyOffsetY.animateTo(0f, tween(380, easing = FastOutSlowInEasing)) }
        delay(160)
        launch { buttonsAlpha.animateTo(1f, tween(380)) }
        buttonsOffsetY.animateTo(0f, tween(380, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = screenAlpha.value }
    ) {
        Image(
            painter = painterResource(R.drawable.bg_round_2),
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
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            JokerCardCircle()

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = headerOffsetY.value }
                    .padding(horizontal = 28.dp)
            ) {
                ResultGoldDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "WINNER",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = winner.playerName,
                    style = TextStyle(
                        brush = Brush.verticalGradient(listOf(GoldLight, GoldAccent, GoldDark)),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        shadow = Shadow(
                            color = GoldAccent.copy(alpha = 0.45f),
                            offset = Offset(0f, 6f),
                            blurRadius = 12f
                        )
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "with ${winner.pairs} pairs collected",
                    color = GoldLight.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                ResultGoldDivider()
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(IntrinsicSize.Max)
                    .graphicsLayer {
                        alpha = tallyAlpha.value
                        translationY = tallyOffsetY.value
                    },
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerTallyCard(
                    playerName = uiState.player1Name,
                    pairs = p1Pairs,
                    isWinner = winner.player == Player.ONE,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                PlayerTallyCard(
                    playerName = uiState.player2Name,
                    pairs = p2Pairs,
                    isWinner = winner.player == Player.TWO,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .graphicsLayer {
                        alpha = buttonsAlpha.value
                        translationY = buttonsOffsetY.value
                    },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultButton(
                    text = "PLAY AGAIN",
                    icon = R.drawable.card_joker,
                    containerBrush = GoldButtonGradient,
                    textColor = DarkPurple,
                    onClick = onPlayAgain
                )
                ResultButton(
                    text = "HOME",
                    icon = R.drawable.card_crown,
                    containerBrush = Brush.horizontalGradient(
                        listOf(RichPurple.copy(alpha = 0.8f), DeepPurple.copy(alpha = 0.9f))
                    ),
                    textColor = GoldAccent,
                    borderBrush = Brush.horizontalGradient(
                        listOf(GoldDark.copy(alpha = 0.5f), GoldAccent.copy(alpha = 0.8f), GoldDark.copy(alpha = 0.5f))
                    ),
                    onClick = onHome
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun JokerCardCircle() {
    val infiniteTransition = rememberInfiniteTransition(label = "JokerBob")

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
        initialValue = 0.14f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val entranceScale = remember { Animatable(0.8f) }
    val entranceAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { entranceAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing)) }
        entranceScale.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .graphicsLayer {
                    translationY = bobY
                    scaleX = entranceScale.value
                    scaleY = entranceScale.value
                    alpha = entranceAlpha.value
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(GoldAccent.copy(alpha = glowAlpha), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .padding(14.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MidPurple.copy(alpha = 0.94f), DeepPurple.copy(alpha = 0.98f))
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
                painter = painterResource(R.drawable.card_joker),
                contentDescription = "Joker",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ResultGoldDivider() {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(0.72f),
        thickness = 1.dp,
        color = GoldAccent.copy(alpha = 0.35f)
    )
}

@Composable
private fun PlayerTallyCard(
    playerName: String,
    pairs: List<CardSymbol>,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)
    val borderBrush = if (isWinner) {
        Brush.linearGradient(
            listOf(
                GoldAccent.copy(alpha = 0.85f),
                NeonPurple.copy(alpha = 0.35f),
                GoldAccent.copy(alpha = 0.85f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(MidPurple.copy(alpha = 0.5f), Color.White.copy(alpha = 0.06f))
        )
    }

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(Brush.verticalGradient(listOf(MidPurple, DeepPurple, CardSurface)))
            .border(
                width = if (isWinner) 1.5.dp else 1.dp,
                brush = borderBrush,
                shape = cardShape
            )
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = playerName.take(12).uppercase(),
            color = if (isWinner) GoldAccent else Color.White.copy(alpha = 0.38f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.5f),
            thickness = 0.5.dp,
            color = if (isWinner) GoldAccent.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f)
        )

        if (isWinner) {
            Text(
                text = pairs.size.toString(),
                style = TextStyle(
                    brush = Brush.verticalGradient(listOf(GoldLight, GoldAccent)),
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )
            )
        } else {
            Text(
                text = pairs.size.toString(),
                color = Color.White.copy(alpha = 0.28f),
                fontSize = 44.sp,
                fontWeight = FontWeight.Black
            )
        }

        Text(
            text = if (pairs.size == 1) "PAIR" else "PAIRS",
            color = if (isWinner) GoldAccent.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.2f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        if (pairs.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.7f),
                thickness = 0.5.dp,
                color = if (isWinner) GoldAccent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.06f)
            )

            pairs.chunked(4).forEach { rowSymbols ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowSymbols.forEach { symbol ->
                        Image(
                            painter = painterResource(symbol.drawableRes),
                            contentDescription = symbol.displayName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(26.dp)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }

        if (isWinner) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.5f),
                thickness = 0.5.dp,
                color = GoldAccent.copy(alpha = 0.3f)
            )
            Text(
                text = "WINNER",
                color = GoldAccent,
                fontSize = 9.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun ResultButton(
    text: String,
    icon: Int,
    containerBrush: Brush,
    textColor: Color,
    borderBrush: Brush? = null,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.95f else 1f
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer { scaleX = pressScale; scaleY = pressScale }
            .shadow(
                elevation = if (isPressed) 2.dp else 10.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.6f),
                spotColor = Color.Black.copy(alpha = 0.6f)
            )
            .clip(shape)
            .background(containerBrush)
            .then(
                if (borderBrush != null) Modifier.border(1.5.dp, borderBrush, shape)
                else Modifier
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
                        listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.18f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.09f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = text,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.85f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
