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
            app.krafted.jokermemoryflip.ui.screens.HomeScreen(
                onPlayVsAiClick = { navController.navigate(Screen.ModeSelect.createRoute("VS_AI")) },
                onPlayVsPlayerClick = { navController.navigate(Screen.ModeSelect.createRoute("VS_PLAYER")) },
                onLeaderboardClick = { navController.navigate(Screen.Leaderboard.route) }
            )
        }
        composable(Screen.ModeSelect.route) { backStackEntry ->
            val gameMode = backStackEntry.arguments?.getString("gameMode") ?: "VS_AI"
        }
        composable(Screen.GameBoard.route) {
        }
        composable(Screen.Pass.route) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: "Player"
        }
        composable(Screen.Steal.route) {
        }
        composable(Screen.Result.route) {
        }
        composable(Screen.Leaderboard.route) {
        }
    }
}
