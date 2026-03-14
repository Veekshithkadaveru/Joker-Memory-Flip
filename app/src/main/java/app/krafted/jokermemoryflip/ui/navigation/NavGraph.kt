package app.krafted.jokermemoryflip.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.krafted.jokermemoryflip.data.AppDatabase
import app.krafted.jokermemoryflip.game.CardSymbol
import app.krafted.jokermemoryflip.game.Difficulty
import app.krafted.jokermemoryflip.game.GameMode
import app.krafted.jokermemoryflip.game.Player
import app.krafted.jokermemoryflip.ui.screens.GameBoardScreen
import app.krafted.jokermemoryflip.ui.screens.HomeScreen
import app.krafted.jokermemoryflip.ui.screens.LeaderboardScreen
import app.krafted.jokermemoryflip.ui.screens.ModeSelectScreen
import app.krafted.jokermemoryflip.ui.screens.StealScreen
import app.krafted.jokermemoryflip.ui.theme.*
import app.krafted.jokermemoryflip.viewmodel.GameViewModel
import app.krafted.jokermemoryflip.viewmodel.LeaderboardViewModel
import app.krafted.jokermemoryflip.viewmodel.MatchResult

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ModeSelect : Screen("mode_select/{gameMode}") {
        fun createRoute(gameMode: String) = "mode_select/$gameMode"
    }
    object GameBoard : Screen("game_board")

    // Pass screen is now an overlay inside GameBoardScreen — route kept for reference only
    object Pass : Screen("pass/{playerName}") {
        fun createRoute(playerName: String) = "pass/$playerName"
    }

    object Steal : Screen("steal")
    object Result : Screen("result")
    object Leaderboard : Screen("leaderboard")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModel.Factory(db.matchDao()))
    val leaderboardViewModel: LeaderboardViewModel =
        viewModel(factory = LeaderboardViewModel.Factory(db.matchDao()))

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        // ── Home ─────────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onPlayVsAiClick = {
                    navController.navigate(Screen.ModeSelect.createRoute("VS_AI"))
                },
                onPlayVsPlayerClick = {
                    navController.navigate(Screen.ModeSelect.createRoute("VS_PLAYER"))
                },
                onLeaderboardClick = {
                    navController.navigate(Screen.Leaderboard.route)
                }
            )
        }

        // ── Mode Select ───────────────────────────────────────────────────────
        composable(Screen.ModeSelect.route) { backStackEntry ->
            val gameModeStr = backStackEntry.arguments?.getString("gameMode") ?: "VS_AI"
            val gameMode = try {
                GameMode.valueOf(gameModeStr)
            } catch (e: Exception) {
                GameMode.VS_AI
            }

            ModeSelectScreen(
                gameMode = gameMode,
                onStartVsAi = { difficulty ->
                    gameViewModel.startGame(GameMode.VS_AI, difficulty, "Player", "")
                    navController.navigate(Screen.GameBoard.route)
                },
                onStartVsPlayer = { p1, p2 ->
                    gameViewModel.startGame(GameMode.VS_PLAYER, Difficulty.AVERAGE, p1, p2)
                    navController.navigate(Screen.GameBoard.route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Game Board ────────────────────────────────────────────────────────
        composable(Screen.GameBoard.route) {
            GameBoardScreen(
                viewModel = gameViewModel,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSteal = {
                    navController.navigate(Screen.Steal.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Steal ─────────────────────────────────────────────────────────────
        composable(Screen.Steal.route) {
            // Snapshot steal data once on entry — prevents flash when executeSteal()
            // clears stealingPlayer/jokerStealPending before the pop completes.
            val uiState = gameViewModel.uiState.collectAsState().value
            val stealingPlayer = remember { uiState.stealingPlayer }
            val victimPlayer   = remember { if (stealingPlayer == Player.ONE) Player.TWO else Player.ONE }
            val victimPairs    = remember { uiState.collectedPairs[victimPlayer] ?: emptyList() }
            val stealingName   = remember { if (stealingPlayer == Player.ONE) uiState.player1Name else uiState.player2Name }
            val victimName     = remember { if (victimPlayer   == Player.ONE) uiState.player1Name else uiState.player2Name }

            StealScreen(
                stealingPlayerName = stealingName,
                victimPlayerName   = victimName,
                victimPairs        = victimPairs,
                onSymbolChosen     = { symbol ->
                    navController.popBackStack()
                    gameViewModel.executeSteal(symbol)
                }
            )
        }

        // ── Result ────────────────────────────────────────────────────────────
        composable(Screen.Result.route) {
            val uiState by gameViewModel.uiState.collectAsState()
            val winner = uiState.matchResult as? MatchResult.Winner

            ResultScreenPlaceholder(
                winnerName = winner?.playerName ?: "Player",
                winnerPairs = winner?.pairs ?: 0,
                onPlayAgain = {
                    gameViewModel.resetGame()
                    navController.navigate(Screen.GameBoard.route) {
                        popUpTo(Screen.GameBoard.route) { inclusive = true }
                    }
                },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Leaderboard ───────────────────────────────────────────────────────
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                viewModel = leaderboardViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private placeholder composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ResultScreenPlaceholder(
    winnerName: String,
    winnerPairs: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkPurple),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "WINNER!",
                color = GoldAccent,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = winnerName,
                color = GoldLight,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "$winnerPairs pairs collected",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Play Again button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Brush.horizontalGradient(listOf(GoldDark, GoldAccent, GoldDark)))
                    .clickable { onPlayAgain() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PLAY AGAIN",
                    color = DarkPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }

            // Home button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .border(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(listOf(GoldDark, GoldAccent, GoldDark)),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .background(Color.Transparent)
                    .clickable { onHome() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HOME",
                    color = GoldAccent,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

