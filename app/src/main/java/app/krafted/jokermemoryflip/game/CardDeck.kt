package app.krafted.jokermemoryflip.game

import app.krafted.jokermemoryflip.R

enum class CardSymbol(val displayName: String, val drawableRes: Int) {
    JOKER("The Joker", R.drawable.card_joker),
    DIAMOND("Strawberry", R.drawable.card_diamond),
    SPADE("Lemon", R.drawable.card_spade),
    HEART("Crown", R.drawable.card_heart),
    CLUB("Horseshoe", R.drawable.card_club),
    CROWN("Diamond", R.drawable.card_crown),
    STAR("Heart", R.drawable.card_star)
}

data class CardState(
    val id: Int,
    val symbol: CardSymbol,
    val isFaceUp: Boolean = false,
    val isCollected: Boolean = false,
    val collectedBy: Player? = null
)

enum class Player { ONE, TWO }
enum class GameMode { VS_AI, VS_PLAYER }
enum class Difficulty { FORGETFUL, AVERAGE, PHOTOGRAPHIC }
enum class TurnPhase { SELECTING, EVALUATING, MISMATCH_REVEAL, PASSING, STEAL_PENDING, GAME_OVER }

object CardDeck {
    fun generateShuffledDeck(): List<CardState> {
        val symbols = CardSymbol.entries.toList()
        return (symbols + symbols)
            .shuffled()
            .mapIndexed { index, symbol ->
                CardState(id = index, symbol = symbol)
            }
    }
}
