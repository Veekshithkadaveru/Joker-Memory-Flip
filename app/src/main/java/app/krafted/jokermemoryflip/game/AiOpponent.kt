package app.krafted.jokermemoryflip.game

class AiOpponent(private val difficulty: Difficulty) {
    private val memoryEngine = MemoryEngine(difficulty)

    fun observeFlip(index: Int, symbol: CardSymbol) {
        memoryEngine.observeCard(index, symbol)
    }

    fun chooseFirstCard(availableIndices: List<Int>): Int {
        if (difficulty == Difficulty.PHOTOGRAPHIC) {
            val knownPair = memoryEngine.getKnownPairs(availableIndices)
            if (knownPair != null) return knownPair.first
        }
        return availableIndices.random()
    }

    fun chooseSecondCard(
        firstIndex: Int,
        firstSymbol: CardSymbol,
        availableIndices: List<Int>
    ): Int {
        val knownMatch = memoryEngine.findKnownMatch(firstSymbol, firstIndex, availableIndices)
        return knownMatch ?: availableIndices.filter { it != firstIndex }.random()
    }

    fun chooseStealTarget(opponentPairs: List<CardSymbol>): CardSymbol {
        return if (CardSymbol.JOKER in opponentPairs) CardSymbol.JOKER
        else opponentPairs.random()
    }

    fun reset() {
        memoryEngine.reset()
    }
}
