package app.krafted.jokermemoryflip.game

class MemoryEngine(private val difficulty: Difficulty) {
    private val seenCards = mutableMapOf<Int, CardSymbol>()
    private val recentBuffer = ArrayDeque<Int>()
    private val maxRecentSize = 4

    fun observeCard(index: Int, symbol: CardSymbol) {
        when (difficulty) {
            Difficulty.FORGETFUL -> { /* remembers nothing */ }
            Difficulty.AVERAGE -> {
                recentBuffer.addLast(index)
                if (recentBuffer.size > maxRecentSize) {
                    val removed = recentBuffer.removeFirst()
                    seenCards.remove(removed)
                }
                seenCards[index] = symbol
            }
            Difficulty.PHOTOGRAPHIC -> {
                seenCards[index] = symbol
            }
        }
    }

    fun findKnownMatch(symbol: CardSymbol, excludeIndex: Int, availableIndices: List<Int>): Int? {
        return seenCards.entries
            .firstOrNull { it.value == symbol && it.key != excludeIndex && it.key in availableIndices }
            ?.key
    }

    fun getKnownPairs(availableIndices: List<Int>): Pair<Int, Int>? {
        val available = seenCards.filter { it.key in availableIndices }
        val grouped = available.entries.groupBy { it.value }
        val pair = grouped.values.firstOrNull { it.size >= 2 }
        return pair?.let { Pair(it[0].key, it[1].key) }
    }

    fun reset() {
        seenCards.clear()
        recentBuffer.clear()
    }
}
