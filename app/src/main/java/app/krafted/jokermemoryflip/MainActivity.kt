package app.krafted.jokermemoryflip

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import app.krafted.jokermemoryflip.ui.navigation.AppNavGraph
import app.krafted.jokermemoryflip.ui.theme.JokerMemoryFlipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setOnExitAnimationListener { provider ->
            val scaleX = ObjectAnimator.ofFloat(provider.view, View.SCALE_X, 1f, 1.2f, 0f)
            val scaleY = ObjectAnimator.ofFloat(provider.view, View.SCALE_Y, 1f, 1.2f, 0f)
            val fade  = ObjectAnimator.ofFloat(provider.view, View.ALPHA,   1f, 0f)

            AnimatorSet().apply {
                playTogether(scaleX, scaleY, fade)
                duration = 450
                interpolator = AccelerateInterpolator()
                doOnEnd { provider.remove() }
                start()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            JokerMemoryFlipTheme {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}
