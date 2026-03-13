package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.game.Difficulty
import app.krafted.jokermemoryflip.game.GameMode
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
import kotlinx.coroutines.launch

@Composable
fun ModeSelectScreen(
    gameMode: GameMode,
    onStartVsAi: (Difficulty) -> Unit,
    onStartVsPlayer: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var animateIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateIn = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurple)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_round_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
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
                            OverlayBlack.copy(alpha = 0.2f),
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = GoldLight
                    )
                }
                Text(
                    text = if (gameMode == GameMode.VS_AI) "OPPONENT" else "PLAYERS",
                    color = GoldLight,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
            ) {
                if (gameMode == GameMode.VS_AI) {
                    AiSelectionContent(onStartVsAi = onStartVsAi)
                } else {
                    PlayerInputContent(onStartVsPlayer = onStartVsPlayer)
                }
            }
        }
    }
}

@Composable
private fun AiSelectionContent(onStartVsAi: (Difficulty) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CHOOSE YOUR\nDIFFICULTY",
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.White.copy(alpha = 0.7f))
                ),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                    blurRadius = 8f
                )
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        DifficultyCard(
            title = "FORGETFUL",
            description = "Remembers nothing. Pure chaos.",
            icon = R.drawable.card_club,
            containerBrush = Brush.horizontalGradient(listOf(RichPurple.copy(alpha = 0.8f), DeepPurple.copy(alpha = 0.9f))),
            onClick = { onStartVsAi(Difficulty.FORGETFUL) }
        )

        DifficultyCard(
            title = "AVERAGE",
            description = "Remembers the last 4 cards.",
            icon = R.drawable.card_spade,
            containerBrush = Brush.horizontalGradient(listOf(DeepPurple.copy(alpha = 0.9f), CardSurface.copy(alpha = 0.8f))),
            onClick = { onStartVsAi(Difficulty.AVERAGE) }
        )

        DifficultyCard(
            title = "PHOTOGRAPHIC",
            description = "Remembers everything. Good luck.",
            icon = R.drawable.card_diamond,
            containerBrush = Brush.horizontalGradient(listOf(CardSurface.copy(alpha = 0.8f), MidPurple.copy(alpha = 0.8f))),
            onClick = { onStartVsAi(Difficulty.PHOTOGRAPHIC) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerInputContent(onStartVsPlayer: (String, String) -> Unit) {
    var p1Name by remember { mutableStateOf("") }
    var p2Name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ENTER PLAYER\nNAMES",
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color.White.copy(alpha = 0.7f))
                ),
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                shadow = androidx.compose.ui.graphics.Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                    blurRadius = 8f
                )
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        PlayerTextField(
            value = p1Name,
            onValueChange = { if (it.length <= 12) p1Name = it },
            label = "Player 1",
            icon = R.drawable.card_heart
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlayerTextField(
            value = p2Name,
            onValueChange = { if (it.length <= 12) p2Name = it },
            label = "Player 2",
            icon = R.drawable.card_spade
        )

        Spacer(modifier = Modifier.weight(1f))

        val isReady = p1Name.isNotBlank() && p2Name.isNotBlank()

        StartButton(
            onClick = { onStartVsPlayer(p1Name, p2Name) },
            enabled = true,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
private fun DifficultyCard(
    title: String,
    description: String,
    icon: Int,
    containerBrush: Brush,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.95f else 1f
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(containerBrush)
            .border(
                width = 1.dp,
                color = GoldAccent.copy(alpha = 0.2f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 18.sp
                    ),
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White.copy(alpha = 0.6f)) },
        leadingIcon = {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 4.dp)
                    .size(24.dp),
                contentScale = ContentScale.Fit
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldAccent,
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            cursorColor = GoldAccent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Black.copy(alpha = 0.2f),
            unfocusedContainerColor = Color.Black.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StartButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale = if (isPressed) 0.95f else 1f
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = GoldAccent.copy(alpha = 0.4f),
                spotColor = GoldAccent.copy(alpha = 0.4f)
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GoldDark, GoldAccent, GoldDark)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                onClick = onClick,
                enabled = enabled
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "START GAME",
                color = DarkPurple,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = DarkPurple,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
