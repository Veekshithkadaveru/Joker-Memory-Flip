package app.krafted.jokermemoryflip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import app.krafted.jokermemoryflip.ui.navigation.AppNavGraph
import app.krafted.jokermemoryflip.ui.theme.JokerMemoryFlipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            JokerMemoryFlipTheme {
                val navController = rememberNavController()
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}
