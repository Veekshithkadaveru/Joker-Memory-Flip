package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.game.CardSymbol
import app.krafted.jokermemoryflip.game.GameMode
import app.krafted.jokermemoryflip.game.Player
import app.krafted.jokermemoryflip.game.TurnPhase
import app.krafted.jokermemoryflip.ui.components.BackgroundScene
import app.krafted.jokermemoryflip.ui.components.CardGrid
import app.krafted.jokermemoryflip.ui.components.TurnIndicator
import app.krafted.jokermemoryflip.ui.theme.CardSurface
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.GoldDark
import app.krafted.jokermemoryflip.ui.theme.GoldLight
import app.krafted.jokermemoryflip.ui.theme.MidPurple
import app.krafted.jokermemoryflip.ui.theme.NeonPurple
import app.krafted.jokermemoryflip.viewmodel.GameViewModel

@Composable
fun GameBoardScreen(
    viewModel: GameViewModel,
    onNavigateToResult: () -> Unit,
    onNavigateToSteal: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isGameOver) {
        if (uiState.isGameOver) onNavigateToResult()
    }

    LaunchedEffect(uiState.jokerStealPending) {
        if (uiState.jokerStealPending) {
            val shouldNavigate = uiState.gameMode == GameMode.VS_PLAYER ||
                    (uiState.gameMode == GameMode.VS_AI && uiState.stealingPlayer == Player.ONE)
            if (shouldNavigate) onNavigateToSteal()
        }
    }

    val mismatchIndices = if (uiState.turnPhase == TurnPhase.MISMATCH_REVEAL) {
        uiState.flippedIndices
    } else {
        emptyList()
    }

    val p1Pairs = uiState.collectedPairs[Player.ONE] ?: emptyList()
    val p2Pairs = uiState.collectedPairs[Player.TWO] ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundScene(
            backgroundRes = uiState.currentBackground,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            ScoreHeader(
                player1Name = uiState.player1Name,
                player2Name = uiState.player2Name,
                player1Pairs = p1Pairs,
                player2Pairs = p2Pairs,
                activePlayer = uiState.activePlayer,
                gameMode = uiState.gameMode
            )

            TurnIndicator(
                activePlayer = uiState.activePlayer,
                player1Name = uiState.player1Name,
                player2Name = uiState.player2Name,
                gameMode = uiState.gameMode
            )

            CardGrid(
                cards = uiState.cards,
                flippedIndices = uiState.flippedIndices,
                mismatchIndices = mismatchIndices,
                onCardClick = { index -> viewModel.onCardTapped(index) },
                modifier = Modifier.weight(1f)
            )
        }

        if (uiState.showPassScreen) {
            val nextPlayerName = if (uiState.activePlayer == Player.TWO) {
                uiState.player2Name
            } else {
                uiState.player1Name
            }
            PassScreen(
                nextPlayerName = nextPlayerName,
                onReady = { viewModel.dismissPassScreen() }
            )
        }
    }
}

@Composable
private fun ScoreHeader(
    player1Name: String,
    player2Name: String,
    player1Pairs: List<CardSymbol>,
    player2Pairs: List<CardSymbol>,
    activePlayer: Player,
    gameMode: GameMode
) {
    val isAiGame = gameMode == GameMode.VS_AI
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlayerChip(
            name = player1Name,
            pairs = player1Pairs,
            isActive = activePlayer == Player.ONE,
            isAi = false,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "VS",
            color = GoldAccent.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontSize = 10.sp
            )
        )

        PlayerChip(
            name = player2Name,
            pairs = player2Pairs,
            isActive = activePlayer == Player.TWO,
            isAi = isAiGame,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PlayerChip(
    name: String,
    pairs: List<CardSymbol>,
    isActive: Boolean,
    isAi: Boolean,
    modifier: Modifier = Modifier
) {
    val chipShape = RoundedCornerShape(14.dp)
    val accentColor = if (isAi) NeonPurple else GoldAccent

    Row(
        modifier = modifier
            .clip(chipShape)
            .background(
                color = if (isActive) accentColor.copy(alpha = 0.12f) else CardSurface.copy(alpha = 0.7f),
                shape = chipShape
            )
            .border(
                width = if (isActive) 1.dp else 0.5.dp,
                brush = if (isActive) {
                    Brush.horizontalGradient(
                        listOf(accentColor.copy(alpha = 0.3f), accentColor.copy(alpha = 0.7f), accentColor.copy(alpha = 0.3f))
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(MidPurple.copy(alpha = 0.3f), MidPurple.copy(alpha = 0.3f))
                    )
                },
                shape = chipShape
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    color = if (isActive) accentColor else MidPurple,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pairs.size.toString(),
                color = if (isActive) GoldDark else Color.Gray,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            )
        }

        Text(
            text = name.take(14),
            color = if (isActive) GoldLight else Color.Gray.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (pairs.isNotEmpty()) {
            val visible = pairs.takeLast(3)
            Row(horizontalArrangement = Arrangement.spacedBy((-4).dp)) {
                visible.forEach { symbol ->
                    Image(
                        painter = painterResource(id = symbol.drawableRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(CardSurface)
                    )
                }
                if (pairs.size > 3) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${pairs.size - 3}",
                        color = accentColor.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                    )
                }
            }
        }
    }
}
