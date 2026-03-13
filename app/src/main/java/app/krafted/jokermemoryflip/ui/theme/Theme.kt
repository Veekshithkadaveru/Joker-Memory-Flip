package app.krafted.jokermemoryflip.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JokerColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = RichPurple,
    onPrimaryContainer = Color.White,
    secondary = GoldAccent,
    onSecondary = DarkPurple,
    secondaryContainer = GoldDark,
    onSecondaryContainer = Color.White,
    tertiary = NeonGreen,
    onTertiary = DarkPurple,
    background = DarkPurple,
    onBackground = Color.White,
    surface = DeepPurple,
    onSurface = Color.White,
    surfaceVariant = MidPurple,
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = MismatchRed,
    onError = Color.White,
    outline = RichPurple
)

@Composable
fun JokerMemoryFlipTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = JokerColorScheme,
        typography = Typography,
        content = content
    )
}
