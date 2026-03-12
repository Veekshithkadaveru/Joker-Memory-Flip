package app.krafted.jokermemoryflip.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ModeSelect : Screen("mode_select/{gameMode}") {
        fun createRoute(gameMode: String) = "mode_select/$gameMode"
    }
    object GameBoard : Screen("game_board")
    object Pass : Screen("pass/{playerName}") {
        fun createRoute(playerName: String) = "pass/$playerName"
    }
    object Steal : Screen("steal")
    object Result : Screen("result")
    object Leaderboard : Screen("leaderboard")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            // TODO: HomeScreen
        }
        composable(Screen.ModeSelect.route) { backStackEntry ->
            val gameMode = backStackEntry.arguments?.getString("gameMode") ?: "VS_AI"
            // TODO: ModeSelectScreen
        }
        composable(Screen.GameBoard.route) {
            // TODO: GameBoardScreen
        }
        composable(Screen.Pass.route) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: "Player"
            // TODO: PassScreen
        }
        composable(Screen.Steal.route) {
            // TODO: StealScreen
        }
        composable(Screen.Result.route) {
            // TODO: ResultScreen
        }
        composable(Screen.Leaderboard.route) {
            // TODO: LeaderboardScreen
        }
    }
}
