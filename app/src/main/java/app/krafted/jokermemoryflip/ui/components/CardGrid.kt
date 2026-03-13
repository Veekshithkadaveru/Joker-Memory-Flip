package app.krafted.jokermemoryflip.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.krafted.jokermemoryflip.game.CardState
import app.krafted.jokermemoryflip.game.GameConstants

@Composable
fun CardGrid(
    cards: List<CardState>,
    flippedIndices: List<Int>,
    mismatchIndices: List<Int> = emptyList(),
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCells = GameConstants.GRID_COLUMNS * GameConstants.GRID_ROWS

    LazyVerticalGrid(
        columns = GridCells.Fixed(GameConstants.GRID_COLUMNS),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            count = totalCells,
            key = { index -> index }
        ) { index ->
            if (index < GameConstants.TOTAL_CARDS) {
                val card = cards[index]
                CardItem(
                    card = card,
                    isFlipping = index in flippedIndices,
                    isMismatch = index in mismatchIndices,
                    onClick = { onCardClick(index) }
                )
            } else {
                Spacer(
                    modifier = Modifier.aspectRatio(0.714f)
                )
            }
        }
    }
}
