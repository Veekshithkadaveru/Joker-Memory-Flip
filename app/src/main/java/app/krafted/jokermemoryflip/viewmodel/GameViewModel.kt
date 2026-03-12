package app.krafted.jokermemoryflip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.data.MatchDao
import app.krafted.jokermemoryflip.data.MatchRecord
import app.krafted.jokermemoryflip.game.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val cards: List<CardState> = emptyList(),
    val flippedIndices: List<Int> = emptyList(),
    val collectedPairs: Map<Player, List<CardSymbol>> = mapOf(
        Player.ONE to emptyList(),
        Player.TWO to emptyList()
    ),
    val activePlayer: Player = Player.ONE,
    val turnPhase: TurnPhase = TurnPhase.SELECTING,
    val jokerStealPending: Boolean = false,
    val stealingPlayer: Player? = null,
    val matchResult: MatchResult? = null,
    val gameMode: GameMode = GameMode.VS_AI,
    val aiDifficulty: Difficulty = Difficulty.AVERAGE,
    val roundNumber: Int = 1,
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val currentBackground: Int = R.drawable.bg_round_1,
    val showPassScreen: Boolean = false,
    val isGameOver: Boolean = false
)

sealed class MatchResult {
    data class Winner(val player: Player, val playerName: String, val pairs: Int) : MatchResult()
}

class GameViewModel(private val matchDao: MatchDao) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aiOpponent: AiOpponent? = null

    fun startGame(mode: GameMode, difficulty: Difficulty, p1: String, p2: String) {
        val deck = CardDeck.generateShuffledDeck()
        aiOpponent = if (mode == GameMode.VS_AI) AiOpponent(difficulty) else null
        _uiState.value = GameUiState(
            cards = deck,
            gameMode = mode,
            aiDifficulty = difficulty,
            player1Name = p1.ifBlank { "Player 1" },
            player2Name = if (mode == GameMode.VS_AI) "AI (${difficulty.name.lowercase().replaceFirstChar { it.uppercase() }})" else p2.ifBlank { "Player 2" },
            currentBackground = GameConstants.ROUND_BACKGROUNDS[0]
        )
    }

    fun onCardTapped(index: Int) {
        val state = _uiState.value
        if (state.turnPhase != TurnPhase.SELECTING) return
        if (state.cards[index].isFaceUp || state.cards[index].isCollected) return
        if (index in state.flippedIndices) return
        // Block human taps during AI turn
        if (state.gameMode == GameMode.VS_AI && state.activePlayer == Player.TWO) return

        val updatedCards = state.cards.toMutableList()
        updatedCards[index] = updatedCards[index].copy(isFaceUp = true)

        aiOpponent?.observeFlip(index, updatedCards[index].symbol)

        val newFlipped = state.flippedIndices + index

        if (newFlipped.size == 1) {
            _uiState.update { it.copy(cards = updatedCards, flippedIndices = newFlipped) }
        } else if (newFlipped.size == 2) {
            _uiState.update {
                it.copy(
                    cards = updatedCards,
                    flippedIndices = newFlipped,
                    turnPhase = TurnPhase.EVALUATING
                )
            }
            evaluateMatch(newFlipped[0], newFlipped[1])
        }
    }

    private fun evaluateMatch(idx1: Int, idx2: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val card1 = state.cards[idx1]
            val card2 = state.cards[idx2]

            if (card1.symbol == card2.symbol) {
                delay(GameConstants.MATCH_GLOW_MS)
                collectPair(idx1, idx2, card1.symbol)
            } else {
                _uiState.update { it.copy(turnPhase = TurnPhase.MISMATCH_REVEAL) }
                delay(GameConstants.MISMATCH_REVEAL_MS)
                flipBack(idx1, idx2)
                passTurn()
            }
        }
    }

    private fun collectPair(idx1: Int, idx2: Int, symbol: CardSymbol) {
        val state = _uiState.value
        val player = state.activePlayer

        val updatedCards = state.cards.toMutableList()
        updatedCards[idx1] = updatedCards[idx1].copy(isCollected = true, isFaceUp = false, collectedBy = player)
        updatedCards[idx2] = updatedCards[idx2].copy(isCollected = true, isFaceUp = false, collectedBy = player)

        val updatedPairs = state.collectedPairs.toMutableMap()
        updatedPairs[player] = (updatedPairs[player] ?: emptyList()) + symbol

        val totalCollected = updatedPairs.values.sumOf { it.size }
        val bgIndex = ((totalCollected - 1).coerceAtLeast(0)) % GameConstants.ROUND_BACKGROUNDS.size

        _uiState.update {
            it.copy(
                cards = updatedCards,
                collectedPairs = updatedPairs,
                flippedIndices = emptyList(),
                roundNumber = totalCollected,
                currentBackground = GameConstants.ROUND_BACKGROUNDS[bgIndex]
            )
        }

        if (symbol == CardSymbol.JOKER) {
            val opponent = if (player == Player.ONE) Player.TWO else Player.ONE
            val opponentPairs = updatedPairs[opponent] ?: emptyList()
            if (opponentPairs.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        turnPhase = TurnPhase.STEAL_PENDING,
                        jokerStealPending = true,
                        stealingPlayer = player
                    )
                }
                // If AI is stealing, auto-select
                if (state.gameMode == GameMode.VS_AI && player == Player.TWO) {
                    viewModelScope.launch {
                        delay(GameConstants.AI_THINK_MIN_MS)
                        val target = aiOpponent?.chooseStealTarget(opponentPairs) ?: opponentPairs.random()
                        executeSteal(target)
                    }
                }
                return
            }
        }

        checkGameOver()
    }

    fun executeSteal(stolenSymbol: CardSymbol) {
        val state = _uiState.value
        val stealer = state.stealingPlayer ?: return
        val victim = if (stealer == Player.ONE) Player.TWO else Player.ONE

        val updatedPairs = state.collectedPairs.toMutableMap()
        val victimPairs = (updatedPairs[victim] ?: emptyList()).toMutableList()
        val stealerPairs = (updatedPairs[stealer] ?: emptyList()).toMutableList()

        victimPairs.remove(stolenSymbol)
        stealerPairs.add(stolenSymbol)

        updatedPairs[victim] = victimPairs
        updatedPairs[stealer] = stealerPairs

        _uiState.update {
            it.copy(
                collectedPairs = updatedPairs,
                jokerStealPending = false,
                stealingPlayer = null,
                turnPhase = TurnPhase.SELECTING
            )
        }

        checkGameOver()
    }

    private fun checkGameOver() {
        val state = _uiState.value
        val totalCollected = state.cards.count { it.isCollected }
        if (totalCollected >= GameConstants.TOTAL_CARDS) {
            val p1Pairs = (state.collectedPairs[Player.ONE] ?: emptyList()).size
            val p2Pairs = (state.collectedPairs[Player.TWO] ?: emptyList()).size
            val winner = if (p1Pairs >= p2Pairs) Player.ONE else Player.TWO
            val winnerName = if (winner == Player.ONE) state.player1Name else state.player2Name
            val winnerPairs = maxOf(p1Pairs, p2Pairs)

            _uiState.update {
                it.copy(
                    turnPhase = TurnPhase.GAME_OVER,
                    isGameOver = true,
                    matchResult = MatchResult.Winner(winner, winnerName, winnerPairs)
                )
            }

            viewModelScope.launch {
                matchDao.insertMatch(
                    MatchRecord(
                        winnerName = winnerName,
                        loserName = if (winner == Player.ONE) state.player2Name else state.player1Name,
                        winnerPairs = maxOf(p1Pairs, p2Pairs),
                        loserPairs = minOf(p1Pairs, p2Pairs),
                        gameMode = state.gameMode.name,
                        aiDifficulty = if (state.gameMode == GameMode.VS_AI) state.aiDifficulty.name else null
                    )
                )
            }
        } else {
            // Player matched — they keep their turn
            _uiState.update { it.copy(turnPhase = TurnPhase.SELECTING) }
            // If AI's turn continues, trigger next AI move
            val currentState = _uiState.value
            if (currentState.gameMode == GameMode.VS_AI && currentState.activePlayer == Player.TWO) {
                triggerAiTurn()
            }
        }
    }

    private fun flipBack(idx1: Int, idx2: Int) {
        val state = _uiState.value
        val updatedCards = state.cards.toMutableList()
        updatedCards[idx1] = updatedCards[idx1].copy(isFaceUp = false)
        updatedCards[idx2] = updatedCards[idx2].copy(isFaceUp = false)
        _uiState.update { it.copy(cards = updatedCards, flippedIndices = emptyList()) }
    }

    private fun passTurn() {
        val state = _uiState.value
        val nextPlayer = if (state.activePlayer == Player.ONE) Player.TWO else Player.ONE

        if (state.gameMode == GameMode.VS_PLAYER) {
            // PvP: show pass screen
            _uiState.update {
                it.copy(
                    activePlayer = nextPlayer,
                    turnPhase = TurnPhase.PASSING,
                    showPassScreen = true
                )
            }
        } else {
            // VS AI
            _uiState.update {
                it.copy(
                    activePlayer = nextPlayer,
                    turnPhase = TurnPhase.SELECTING
                )
            }
            if (nextPlayer == Player.TWO) {
                triggerAiTurn()
            }
        }
    }

    fun dismissPassScreen() {
        _uiState.update {
            it.copy(
                showPassScreen = false,
                turnPhase = TurnPhase.SELECTING
            )
        }
    }

    private fun triggerAiTurn() {
        val ai = aiOpponent ?: return
        viewModelScope.launch {
            val thinkTime = (GameConstants.AI_THINK_MIN_MS..GameConstants.AI_THINK_MAX_MS).random()
            delay(thinkTime)

            val state = _uiState.value
            val availableIndices = state.cards.indices.filter { !state.cards[it].isCollected && !state.cards[it].isFaceUp }
            if (availableIndices.isEmpty()) return@launch

            // First card
            val firstIdx = ai.chooseFirstCard(availableIndices)
            val updatedCards1 = state.cards.toMutableList()
            updatedCards1[firstIdx] = updatedCards1[firstIdx].copy(isFaceUp = true)
            ai.observeFlip(firstIdx, updatedCards1[firstIdx].symbol)
            _uiState.update { it.copy(cards = updatedCards1, flippedIndices = listOf(firstIdx)) }

            delay(GameConstants.AI_FLIP_DELAY_MS)

            // Second card
            val state2 = _uiState.value
            val availableForSecond = state2.cards.indices.filter {
                !state2.cards[it].isCollected && !state2.cards[it].isFaceUp
            }
            if (availableForSecond.isEmpty()) return@launch

            val secondIdx = ai.chooseSecondCard(firstIdx, state2.cards[firstIdx].symbol, availableForSecond)
            val updatedCards2 = state2.cards.toMutableList()
            updatedCards2[secondIdx] = updatedCards2[secondIdx].copy(isFaceUp = true)
            ai.observeFlip(secondIdx, updatedCards2[secondIdx].symbol)
            _uiState.update {
                it.copy(
                    cards = updatedCards2,
                    flippedIndices = listOf(firstIdx, secondIdx),
                    turnPhase = TurnPhase.EVALUATING
                )
            }

            evaluateMatch(firstIdx, secondIdx)
        }
    }

    fun resetGame() {
        aiOpponent?.reset()
        val state = _uiState.value
        startGame(state.gameMode, state.aiDifficulty, state.player1Name, state.player2Name)
    }

    class Factory(private val matchDao: MatchDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GameViewModel(matchDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
