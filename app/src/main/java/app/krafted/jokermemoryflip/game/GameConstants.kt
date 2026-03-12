package app.krafted.jokermemoryflip.game

import app.krafted.jokermemoryflip.R

object GameConstants {
    const val TOTAL_CARDS = 14
    const val TOTAL_PAIRS = 7
    const val GRID_COLUMNS = 4
    const val GRID_ROWS = 4

    const val FLIP_HALF_DURATION_MS = 300
    const val FLIP_TOTAL_DURATION_MS = 600

    const val MISMATCH_REVEAL_MS = 1000L
    const val MISMATCH_REVEAL_EASY_MS = 1500L
    const val MATCH_GLOW_MS = 800L
    const val MATCH_SCALE_FACTOR = 1.1f

    const val AI_THINK_MIN_MS = 600L
    const val AI_THINK_MAX_MS = 900L
    const val AI_FLIP_DELAY_MS = 500L

    const val TURN_CHANGE_DELAY_MS = 500L

    val ROUND_BACKGROUNDS = listOf(
        R.drawable.bg_round_1,
        R.drawable.bg_round_2,
        R.drawable.bg_round_3,
        R.drawable.bg_round_4,
        R.drawable.bg_round_5
    )

    const val WIN_THRESHOLD = 4
}
