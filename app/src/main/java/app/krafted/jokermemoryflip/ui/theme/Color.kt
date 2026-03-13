package app.krafted.jokermemoryflip.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core dark carnival palette
val DarkPurple = Color(0xFF0D0518)
val DeepPurple = Color(0xFF1A0B2E)
val MidPurple = Color(0xFF2D1B4E)
val RichPurple = Color(0xFF4A1F7A)

// Neon accents
val NeonGreen = Color(0xFF39FF14)
val NeonPurple = Color(0xFFB026FF)
val NeonPink = Color(0xFFFF2D95)
val NeonCyan = Color(0xFF00F0FF)

// Gold / warm highlights
val GoldAccent = Color(0xFFFFD700)
val GoldLight = Color(0xFFFFF1A8)
val GoldDark = Color(0xFFB8960F)
val WarmAmber = Color(0xFFFFAB00)

// UI / state colours
val DeepRed = Color(0xFF8B0000)
val MatchGreen = Color(0xFF00E676)
val MismatchRed = Color(0xFFFF1744)

// Surfaces
val CardSurface = Color(0xFF1E1230)
val OverlayBlack = Color(0xCC000000)
val DimBlack = Color(0x99000000)

// Pre-built gradients
val PurpleButtonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7B1FA2), Color(0xFFAA00FF), Color(0xFF7B1FA2))
)

val GoldButtonGradient = Brush.horizontalGradient(
    colors = listOf(GoldDark, GoldAccent, GoldDark)
)

val BackgroundVignette = Brush.verticalGradient(
    colors = listOf(
        Color(0xCC0D0518),
        Color(0x330D0518),
        Color(0x000D0518),
        Color(0x330D0518),
        Color(0xEE0D0518)
    )
)

val BottomFade = Brush.verticalGradient(
    colors = listOf(Color.Transparent, DarkPurple)
)
