---
name: Joker Memory Flip v1.0
overview: Build a competitive memory card-flipping game for two players (pass-and-play or vs AI). 14 Joker-themed face-down cards on a dark carnival stage. Players flip pairs — match a symbol to collect it. The Joker card is a wildcard that steals an opponent's collected pair. Most pairs wins. Pure Jetpack Compose with MVVM, Room persistence, and the Jok_012_N asset pack. 3-day delivery sprint.
todos:
  - id: project-setup
    content: Setup project with Compose, Room, Navigation, KSP, and copy Jok_012_N asset pack into drawable resources
    status: completed
  - id: data-layer
    content: Build Room database with MatchRecord entity, MatchDao, and AppDatabase for win record persistence
    status: pending
    dependencies:
      - project-setup
  - id: game-engine
    content: Implement CardDeck (14-card shuffle), AiOpponent (Forgetful / Average / Photographic), and MemoryEngine (AI memory tracking)
    status: pending
    dependencies:
      - project-setup
  - id: viewmodels
    content: Build GameViewModel (GameUiState, turn phase state machine, Joker steal logic) and LeaderboardViewModel (Room queries)
    status: pending
    dependencies:
      - data-layer
      - game-engine
  - id: home-screen
    content: Build HomeScreen with Joker Girl, Play vs AI / Play vs Player buttons, Leaderboard nav
    status: pending
    dependencies:
      - viewmodels
  - id: mode-select-screen
    content: Create ModeSelectScreen with AI difficulty picker (3 levels) or Player 1 + Player 2 name input
    status: pending
    dependencies:
      - viewmodels
  - id: game-board-screen
    content: Implement GameBoardScreen with 4×4 card grid, scaleX card flip animation, match/mismatch logic, turn indicator, score display, background cycling
    status: pending
    dependencies:
      - viewmodels
      - home-screen
      - mode-select-screen
  - id: pass-screen
    content: Build PassScreen full-screen overlay for PvP phone handoff between turns
    status: pending
    dependencies:
      - game-board-screen
  - id: steal-screen
    content: Create StealScreen with full-screen Joker animation, opponent pair display, and steal selection
    status: pending
    dependencies:
      - game-board-screen
  - id: result-screen
    content: Build ResultScreen with winner declaration, Joker Girl reaction, pairs tally, play again
    status: pending
    dependencies:
      - game-board-screen
  - id: leaderboard-screen
    content: Create LeaderboardScreen showing top 10 win records from Room database
    status: pending
    dependencies:
      - data-layer
  - id: audio-haptics
    content: Add sound effects (flip, match, mismatch, steal, win) and haptic feedback on match and steal
    status: pending
    dependencies:
      - game-board-screen
      - steal-screen
  - id: polish
    content: Edge case handling, UI polish, background cycling per round, animations tuning, theme consistency
    status: pending
    dependencies:
      - audio-haptics
      - result-screen
      - pass-screen
      - leaderboard-screen
  - id: qa-delivery
    content: Acceptance criteria verification across 10 full matches in both modes, client APK build
    status: pending
    dependencies:
      - polish
---

# Joker Memory Flip — Phase 1 Implementation Plan

> **Overview**: Build a competitive memory card-flipping game for two players (pass-and-play or vs AI). 14 Joker-themed face-down cards on a dark carnival stage. Players flip pairs — match a symbol to collect it. The Joker card is a wildcard that steals an opponent's collected pair. Most pairs wins. Pure Jetpack Compose with MVVM, Room persistence, and the Jok_012_N asset pack. 3-day delivery sprint.

> **Asset Note**: Agents must copy assets from `/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/` into `app/src/main/res/drawable/`. Card symbols are `1.png`–`7.png`, backgrounds are `back_1.png`–`back_5.png`. Rename files to lowercase snake_case during copy (e.g., `card_joker.png`, `card_diamond.png`, `bg_round_1.png`).

## ✅ Project Status & Todos

### 🏗 Phase A: Core Engine (Day 1)
- [x] **A1: Project Setup** <!-- id: project-setup -->
- [ ] **A2: Data Layer** <!-- id: data-layer -->
- [ ] **A3: Game Engine** <!-- id: game-engine -->
- [ ] **A4: ViewModels** <!-- id: viewmodels -->

### 🃏 Phase B: Game Board & Screens (Day 2)
- [ ] **B1: Home Screen** <!-- id: home-screen -->
- [ ] **B2: Mode Select Screen** <!-- id: mode-select-screen -->
- [ ] **B3: Game Board Screen** <!-- id: game-board-screen -->
- [ ] **B4: Pass Screen** <!-- id: pass-screen -->

### ⚡ Phase C: Special Screens & Polish (Day 3)
- [ ] **C1: Steal Screen** <!-- id: steal-screen -->
- [ ] **C2: Result Screen** <!-- id: result-screen -->
- [ ] **C3: Leaderboard Screen** <!-- id: leaderboard-screen -->
- [ ] **C4: Audio & Haptics** <!-- id: audio-haptics -->
- [ ] **C5: Polish & Edge Cases** <!-- id: polish -->
- [ ] **C6: QA & Delivery** <!-- id: qa-delivery -->

---

## 🏗 System Architecture

### 1. High-Level Architecture (MVVM + Compose)
```
┌─────────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI Layer                  │
│   (HomeScreen, ModeSelectScreen, GameBoardScreen, etc.)    │
├─────────────────────────────────────────────────────────────┤
│                     ViewModel Layer                         │
│     (GameViewModel + LeaderboardViewModel via StateFlow)   │
├─────────────────────────────────────────────────────────────┤
│                    Game Logic Layer                         │
│        (CardDeck, AiOpponent, MemoryEngine)                │
├─────────────────────────────────────────────────────────────┤
│                      Data Layer                            │
│         (Room DB: MatchRecord, MatchDao)                   │
└─────────────────────────────────────────────────────────────┘
```

### 2. State Architecture (StateFlow + MVVM)
```
GameViewModel
    ├── GameUiState (StateFlow)
    │   ├── cards: List<CardState>              // 14 cards
    │   ├── flippedIndices: List<Int>           // max 2 at a time
    │   ├── collectedPairs: Map<Player, List<CardSymbol>>
    │   ├── activePlayer: Player
    │   ├── turnPhase: TurnPhase
    │   ├── jokerStealPending: Boolean
    │   ├── matchResult: MatchResult?
    │   ├── gameMode: GameMode
    │   ├── aiDifficulty: Difficulty
    │   └── roundNumber: Int
    ├── CardDeck (shuffle + generation)
    ├── AiOpponent (difficulty-based card selection)
    └── MemoryEngine (AI memory state tracking)

LeaderboardViewModel
    ├── leaderboard: StateFlow<List<MatchRecord>>
    └── Room DAO (persist win records)
```

### 3. Project File Structure
```
app/krafted/jokermemoryflip/
├── MainActivity.kt                    # Compose NavHost, entry point
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt             # Joker Girl, mode buttons, leaderboard nav
│   │   ├── ModeSelectScreen.kt       # AI difficulty picker or player name input
│   │   ├── GameBoardScreen.kt        # 4×4 card grid, turn indicator, score display
│   │   ├── PassScreen.kt             # PvP phone handoff overlay
│   │   ├── StealScreen.kt            # Full-screen Joker steal selection
│   │   ├── ResultScreen.kt           # Winner, Joker Girl reaction, play again
│   │   └── LeaderboardScreen.kt      # Room-powered top 10
│   ├── components/
│   │   ├── CardItem.kt               # Single card with flip animation (scaleX)
│   │   ├── CardGrid.kt               # 4×4 grid layout for 14 cards
│   │   ├── TurnIndicator.kt          # Active player glow + "YOUR TURN" banner
│   │   ├── ScoreDisplay.kt           # Player score + collected pairs tray
│   │   ├── JokerGirl.kt              # Joker Girl character reactions
│   │   └── BackgroundScene.kt        # Cycling carnival background
│   ├── navigation/
│   │   └── NavGraph.kt               # Navigation routes
│   └── theme/
│       ├── Theme.kt                   # Dark carnival theme
│       ├── Color.kt                   # Joker-themed colours (neon, dark purple)
│       └── Type.kt                    # Typography
├── game/
│   ├── CardDeck.kt                    # Generates and shuffles 14-card deck
│   ├── AiOpponent.kt                 # Forgetful / Average / Photographic logic
│   ├── MemoryEngine.kt               # AI memory state — tracks seen cards by position
│   └── GameConstants.kt              # Timing, grid layout, scoring constants
├── viewmodel/
│   ├── GameViewModel.kt              # All game state via StateFlow
│   └── LeaderboardViewModel.kt       # Leaderboard queries
├── data/
│   ├── MatchRecord.kt                # Room entity (winner, mode, difficulty, date)
│   ├── MatchDao.kt                   # Room DAO
│   └── AppDatabase.kt                # Room database
└── res/
    └── drawable/
        ├── card_joker.png             # 1.png → 🃏 Joker Face (SPECIAL — steal)
        ├── card_diamond.png           # 2.png → ♦️ Diamond
        ├── card_spade.png             # 3.png → ♠️ Spade
        ├── card_heart.png             # 4.png → ♥️ Heart
        ├── card_club.png              # 5.png → ♣️ Club
        ├── card_crown.png             # 6.png → 👑 Crown
        ├── card_star.png              # 7.png → ⭐ Star
        ├── bg_round_1.png             # back_1.png → Round 1 background
        ├── bg_round_2.png             # back_2.png → Round 2 background
        ├── bg_round_3.png             # back_3.png → Round 3 background
        ├── bg_round_4.png             # back_4.png → Round 4 background
        └── bg_round_5.png             # back_5.png → Round 5 background
```

### 4. Asset Mapping Reference
| Source File | Drawable Name | Symbol | Card Name | Special |
|-------------|---------------|--------|-----------|---------|
| `1.png` | `card_joker.png` | 🃏 Joker Face | The Joker | ⭐ SPECIAL — steal mechanic |
| `2.png` | `card_diamond.png` | ♦️ Diamond | Diamond Card | Standard pair |
| `3.png` | `card_spade.png` | ♠️ Spade | Spade Card | Standard pair |
| `4.png` | `card_heart.png` | ♥️ Heart | Heart Card | Standard pair |
| `5.png` | `card_club.png` | ♣️ Club | Club Card | Standard pair |
| `6.png` | `card_crown.png` | 👑 Crown | Crown Card | Standard pair |
| `7.png` | `card_star.png` | ⭐ Star | Star Card | Standard pair |
| `back_1.png` | `bg_round_1.png` | — | Background Round 1 | Cycles per round |
| `back_2.png` | `bg_round_2.png` | — | Background Round 2 | Cycles per round |
| `back_3.png` | `bg_round_3.png` | — | Background Round 3 | Cycles per round |
| `back_4.png` | `bg_round_4.png` | — | Background Round 4 | Cycles per round |
| `back_5.png` | `bg_round_5.png` | — | Background Round 5 | Cycles per round |

---

## 🚀 Detailed Implementation Roadmap

---

## Phase A: Core Engine (Day 1)

### A1: Project Setup <!-- id: project-setup -->
> **Goal**: Configure project with Compose, Room, Navigation, KSP dependencies and copy Jok_012_N asset pack into drawable resources.

**Duration**: 2 Hours

**Files to create/modify:**
| File | Description |
|------|-------------|
| `build.gradle.kts` (project) | Add KSP plugin for Room annotation processing |
| `build.gradle.kts` (app) | Add Compose, Room, Navigation, Lifecycle, KSP dependencies |
| `gradle/libs.versions.toml` | Version catalog updates for Room, Navigation, KSP |
| `res/drawable/card_*.png` | Copy and rename 7 card symbol assets |
| `res/drawable/bg_round_*.png` | Copy and rename 5 background assets |
| `ui/navigation/NavGraph.kt` | Navigation routes and NavHost |
| `MainActivity.kt` | Compose NavHost entry point |

**Key Dependencies:**
```kotlin
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.activity:activity-compose:1.8.2")
implementation("androidx.compose.animation:animation")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

**Asset Copy Commands:**
```bash
# Card symbols (rename to snake_case)
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/1.png" app/src/main/res/drawable/card_joker.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/2.png" app/src/main/res/drawable/card_diamond.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/3.png" app/src/main/res/drawable/card_spade.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/4.png" app/src/main/res/drawable/card_heart.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/5.png" app/src/main/res/drawable/card_club.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/6.png" app/src/main/res/drawable/card_crown.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/7.png" app/src/main/res/drawable/card_star.png

# Backgrounds (rename to snake_case)
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/back_1.png" app/src/main/res/drawable/bg_round_1.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/back_2.png" app/src/main/res/drawable/bg_round_2.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/back_3.png" app/src/main/res/drawable/bg_round_3.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/back_4.png" app/src/main/res/drawable/bg_round_4.png
cp "/Users/veekshith/Downloads/Jok_012_N_elem/Jok_012_N_elem/back_5.png" app/src/main/res/drawable/bg_round_5.png

# Joker Girl / promo assets from main pack
cp "/Users/veekshith/Downloads/Jok_012_N/Jok_012_N/1.png" app/src/main/res/drawable/joker_girl_1.png
cp "/Users/veekshith/Downloads/Jok_012_N/Jok_012_N/2.png" app/src/main/res/drawable/joker_girl_2.png
cp "/Users/veekshith/Downloads/Jok_012_N/Jok_012_N/3.png" app/src/main/res/drawable/joker_girl_3.png
cp "/Users/veekshith/Downloads/Jok_012_N/Jok_012_N/4.png" app/src/main/res/drawable/joker_girl_4.png
```

**Navigation Routes:**
```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ModeSelect : Screen("mode_select/{gameMode}") {
        fun createRoute(gameMode: String) = "mode_select/$gameMode"
    }
    object GameBoard : Screen("game_board")
    object Pass : Screen("pass/{playerName}") {
        fun createRoute(playerName: String) = "pass/$playerName"
    }
    object Steal : Screen("steal")
    object Result : Screen("result")
    object Leaderboard : Screen("leaderboard")
}
```

**Exit Criteria:**
- [ ] Project builds and runs on emulator
- [ ] All 12 assets (7 symbols + 5 backgrounds) render correctly in drawable
- [ ] Joker Girl assets copied from main pack
- [ ] NavHost navigates between placeholder screens
- [ ] No build errors or dependency conflicts

---

### A2: Data Layer <!-- id: data-layer -->
> **Goal**: Build Room database with match record persistence and leaderboard storage.

**Duration**: 1 Hour

**Files to create:**
| File | Description |
|------|-------------|
| `data/MatchRecord.kt` | Room entity — winner, mode, difficulty, player names, date |
| `data/MatchDao.kt` | Room DAO — insert match, query top 10 |
| `data/AppDatabase.kt` | Room database singleton |

**Room Entity — MatchRecord:**
```kotlin
@Entity(tableName = "match_records")
data class MatchRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val winnerName: String,
    val loserName: String,
    val winnerPairs: Int,
    val loserPairs: Int,
    val gameMode: String,           // "VS_AI" or "VS_PLAYER"
    val aiDifficulty: String? = null, // "FORGETFUL", "AVERAGE", "PHOTOGRAPHIC"
    val jokerStealsUsed: Int = 0,
    val date: Long = System.currentTimeMillis()
)
```

**DAO:**
```kotlin
@Dao
interface MatchDao {
    @Insert
    suspend fun insertMatch(record: MatchRecord)

    @Query("SELECT * FROM match_records ORDER BY date DESC LIMIT 10")
    fun getRecentMatches(): Flow<List<MatchRecord>>

    @Query("SELECT winnerName, COUNT(*) as wins FROM match_records GROUP BY winnerName ORDER BY wins DESC LIMIT 10")
    fun getTopWinners(): Flow<List<WinnerStat>>

    @Query("SELECT COUNT(*) FROM match_records WHERE winnerName = :name")
    fun getWinCount(name: String): Flow<Int>
}

data class WinnerStat(
    val winnerName: String,
    val wins: Int
)
```

**Exit Criteria:**
- [ ] Room database creates on first launch
- [ ] Match records persist after app kill and restart
- [ ] Top 10 leaderboard queries return correct results sorted by wins
- [ ] No Room schema errors

---

### A3: Game Engine <!-- id: game-engine -->
> **Goal**: Implement the core memory game mechanics — 14-card deck generation, AI opponent with 3 difficulty levels, and AI memory tracking engine.

**Duration**: 2.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `game/CardDeck.kt` | Generates and shuffles 14-card deck (7 symbols × 2) |
| `game/AiOpponent.kt` | Forgetful / Average / Photographic AI logic |
| `game/MemoryEngine.kt` | AI memory state — tracks seen cards by position |
| `game/GameConstants.kt` | All timing, scoring, and grid layout constants |

**Card Enums & State:**
```kotlin
enum class CardSymbol(val displayName: String, val drawableRes: Int) {
    JOKER("The Joker", R.drawable.card_joker),
    DIAMOND("Diamond", R.drawable.card_diamond),
    SPADE("Spade", R.drawable.card_spade),
    HEART("Heart", R.drawable.card_heart),
    CLUB("Club", R.drawable.card_club),
    CROWN("Crown", R.drawable.card_crown),
    STAR("Star", R.drawable.card_star)
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
```

**CardDeck — Shuffle Generation:**
```kotlin
object CardDeck {
    fun generateShuffledDeck(): List<CardState> {
        val symbols = CardSymbol.entries.toList()  // 7 symbols
        val deck = (symbols + symbols)              // 14 cards — each symbol twice
            .shuffled()
            .mapIndexed { index, symbol ->
                CardState(id = index, symbol = symbol)
            }
        return deck
    }
}
```

**MemoryEngine — AI Memory Tracking:**
```kotlin
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
```

**AiOpponent — Turn Logic:**
```kotlin
class AiOpponent(private val difficulty: Difficulty) {
    private val memoryEngine = MemoryEngine(difficulty)

    fun observeFlip(index: Int, symbol: CardSymbol) {
        memoryEngine.observeCard(index, symbol)
    }

    fun chooseFirstCard(availableIndices: List<Int>): Int {
        // Photographic: if we know a full pair, pick the first card of it
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
        // Prioritise stealing Joker if opponent has it (prevents re-steal)
        // Otherwise steal randomly
        return if (CardSymbol.JOKER in opponentPairs) CardSymbol.JOKER
        else opponentPairs.random()
    }

    fun reset() {
        memoryEngine.reset()
    }
}
```

**AI Difficulty Behaviour Reference:**
| Level | AI Behaviour | Description |
|-------|-------------|-------------|
| 🤡 Forgetful | Remembers 0% of previously seen cards | Picks randomly every time — feels chaotic |
| 🃏 Average | Remembers last 4 cards seen | Makes smart matches occasionally, misses others |
| 👁️ Photographic | Remembers all previously seen cards | Always matches if it has seen both cards — ruthless |

**AI Turn Timing:**
- AI "thinks" for 600–900ms before each flip (random delay, feels natural)
- AI flips first card → pauses 500ms → flips second card
- On Photographic difficulty, AI prioritises Joker pair if both have been seen

**GameConstants:**
```kotlin
object GameConstants {
    const val TOTAL_CARDS = 14
    const val TOTAL_PAIRS = 7
    const val GRID_COLUMNS = 4
    const val GRID_ROWS = 4       // 14 cards + 2 filler positions

    // Card flip animation timing
    const val FLIP_HALF_DURATION_MS = 300
    const val FLIP_TOTAL_DURATION_MS = 600

    // Match/mismatch reveal
    const val MISMATCH_REVEAL_MS = 1000L
    const val MISMATCH_REVEAL_EASY_MS = 1500L
    const val MATCH_GLOW_MS = 800L
    const val MATCH_SCALE_FACTOR = 1.1f

    // AI timing
    const val AI_THINK_MIN_MS = 600L
    const val AI_THINK_MAX_MS = 900L
    const val AI_FLIP_DELAY_MS = 500L

    // Turn change
    const val TURN_CHANGE_DELAY_MS = 500L

    // Background cycling
    val ROUND_BACKGROUNDS = listOf(
        R.drawable.bg_round_1,
        R.drawable.bg_round_2,
        R.drawable.bg_round_3,
        R.drawable.bg_round_4,
        R.drawable.bg_round_5
    )

    // Win condition: majority of 7 pairs = 4+
    const val WIN_THRESHOLD = 4
}
```

**Exit Criteria:**
- [ ] `generateShuffledDeck()` produces 14 cards with exactly 7 unique symbols, each appearing twice
- [ ] Log 5 deck generations, verify no duplicates in position
- [ ] Forgetful AI never uses memory (random picks verified over 20 turns)
- [ ] Average AI remembers only last 4 seen cards
- [ ] Photographic AI always matches cards it has previously seen
- [ ] `MemoryEngine.reset()` clears all memory state

---

### A4: ViewModels <!-- id: viewmodels -->
> **Goal**: Build GameViewModel with complete GameUiState and turn phase state machine, plus LeaderboardViewModel for Room queries.

**Duration**: 2.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `viewmodel/GameViewModel.kt` | All game state via StateFlow — turn logic, match/mismatch, steal |
| `viewmodel/LeaderboardViewModel.kt` | Leaderboard queries from Room |

**GameUiState:**
```kotlin
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
```

**Turn Phase State Machine:**
```
SELECTING → player taps first card → card flips up
    ↓ player taps second card
EVALUATING → compare symbols
    ├── Match → collect pair, check Joker
    │   ├── Normal match → player keeps turn → SELECTING
    │   └── Joker match → STEAL_PENDING
    ├── Mismatch → MISMATCH_REVEAL (1000ms)
    │   └── flip back → PASSING
    │       ├── VS_AI → AI takes turn automatically
    │       └── VS_PLAYER → show PassScreen
STEAL_PENDING → player selects opponent pair → steal → SELECTING
GAME_OVER → all 14 cards collected → ResultScreen
```

**GameViewModel Core Logic:**
```kotlin
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
            player1Name = p1,
            player2Name = if (mode == GameMode.VS_AI) "AI ($difficulty)" else p2,
            currentBackground = GameConstants.ROUND_BACKGROUNDS[0]
        )
    }

    fun onCardTapped(index: Int) {
        val state = _uiState.value
        if (state.turnPhase != TurnPhase.SELECTING) return
        if (state.cards[index].isFaceUp || state.cards[index].isCollected) return
        if (index in state.flippedIndices) return

        // Flip the card face up
        val updatedCards = state.cards.toMutableList()
        updatedCards[index] = updatedCards[index].copy(isFaceUp = true)

        // AI observes every flip
        aiOpponent?.observeFlip(index, updatedCards[index].symbol)

        val newFlipped = state.flippedIndices + index

        if (newFlipped.size == 1) {
            _uiState.update { it.copy(cards = updatedCards, flippedIndices = newFlipped) }
        } else if (newFlipped.size == 2) {
            _uiState.update { it.copy(
                cards = updatedCards,
                flippedIndices = newFlipped,
                turnPhase = TurnPhase.EVALUATING
            )}
            evaluateMatch(newFlipped[0], newFlipped[1])
        }
    }

    private fun evaluateMatch(idx1: Int, idx2: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            val card1 = state.cards[idx1]
            val card2 = state.cards[idx2]

            if (card1.symbol == card2.symbol) {
                // MATCH
                delay(GameConstants.MATCH_GLOW_MS)
                collectPair(idx1, idx2, card1.symbol)
            } else {
                // MISMATCH
                val revealTime = if (state.aiDifficulty == Difficulty.FORGETFUL)
                    GameConstants.MISMATCH_REVEAL_EASY_MS
                    else GameConstants.MISMATCH_REVEAL_MS
                _uiState.update { it.copy(turnPhase = TurnPhase.MISMATCH_REVEAL) }
                delay(revealTime)
                flipBack(idx1, idx2)
                passTurn()
            }
        }
    }

    private fun collectPair(idx1: Int, idx2: Int, symbol: CardSymbol) {
        val state = _uiState.value
        val player = state.activePlayer

        val updatedCards = state.cards.toMutableList()
        updatedCards[idx1] = updatedCards[idx1].copy(isCollected = true, collectedBy = player)
        updatedCards[idx2] = updatedCards[idx2].copy(isCollected = true, collectedBy = player)

        val updatedPairs = state.collectedPairs.toMutableMap()
        updatedPairs[player] = (updatedPairs[player] ?: emptyList()) + symbol

        val roundNum = updatedPairs.values.sumOf { it.size }
        val bgIndex = ((roundNum - 1) / 1).coerceIn(0, GameConstants.ROUND_BACKGROUNDS.size - 1)

        _uiState.update { it.copy(
            cards = updatedCards,
            collectedPairs = updatedPairs,
            flippedIndices = emptyList(),
            roundNumber = roundNum,
            currentBackground = GameConstants.ROUND_BACKGROUNDS[bgIndex % GameConstants.ROUND_BACKGROUNDS.size]
        )}

        if (symbol == CardSymbol.JOKER) {
            // Joker steal!
            val opponent = if (player == Player.ONE) Player.TWO else Player.ONE
            val opponentPairs = state.collectedPairs[opponent] ?: emptyList()
            if (opponentPairs.isNotEmpty()) {
                _uiState.update { it.copy(
                    turnPhase = TurnPhase.STEAL_PENDING,
                    jokerStealPending = true,
                    stealingPlayer = player
                )}
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

        _uiState.update { it.copy(
            collectedPairs = updatedPairs,
            jokerStealPending = false,
            stealingPlayer = null,
            turnPhase = TurnPhase.SELECTING
        )}

        checkGameOver()
    }

    private fun checkGameOver() {
        val state = _uiState.value
        val totalCollected = state.cards.count { it.isCollected }
        if (totalCollected >= GameConstants.TOTAL_CARDS) {
            val p1Pairs = (state.collectedPairs[Player.ONE] ?: emptyList()).size
            val p2Pairs = (state.collectedPairs[Player.TWO] ?: emptyList()).size
            val winner = if (p1Pairs >= GameConstants.WIN_THRESHOLD) Player.ONE else Player.TWO
            val winnerName = if (winner == Player.ONE) state.player1Name else state.player2Name

            _uiState.update { it.copy(
                turnPhase = TurnPhase.GAME_OVER,
                isGameOver = true,
                matchResult = MatchResult.Winner(winner, winnerName, maxOf(p1Pairs, p2Pairs))
            )}

            // Persist to Room
            viewModelScope.launch {
                matchDao.insertMatch(MatchRecord(
                    winnerName = winnerName,
                    loserName = if (winner == Player.ONE) state.player2Name else state.player1Name,
                    winnerPairs = maxOf(p1Pairs, p2Pairs),
                    loserPairs = minOf(p1Pairs, p2Pairs),
                    gameMode = state.gameMode.name,
                    aiDifficulty = if (state.gameMode == GameMode.VS_AI) state.aiDifficulty.name else null
                ))
            }
        } else {
            // Player matched — they get another turn
            _uiState.update { it.copy(turnPhase = TurnPhase.SELECTING) }
        }
    }

    private fun passTurn() { /* switch activePlayer, trigger AI or PassScreen */ }
    private fun flipBack(idx1: Int, idx2: Int) { /* reset isFaceUp for both cards */ }
    private fun triggerAiTurn() { /* delayed AI card selection with think time */ }
}
```

**LeaderboardViewModel:**
```kotlin
class LeaderboardViewModel(private val matchDao: MatchDao) : ViewModel() {
    val recentMatches: StateFlow<List<MatchRecord>> = matchDao.getRecentMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topWinners: StateFlow<List<WinnerStat>> = matchDao.getTopWinners()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
```

**Exit Criteria:**
- [ ] `startGame()` initialises 14 shuffled cards with correct state
- [ ] Tapping a face-down card flips it face up
- [ ] Tapping a second card triggers match evaluation
- [ ] Match: both cards collected, player scores +1, player keeps turn
- [ ] Joker match triggers steal pending state
- [ ] Mismatch: cards flip back after 1000ms, turn passes
- [ ] Game ends when all 14 cards collected
- [ ] Winner determined by majority of 7 pairs (4+)
- [ ] Match record persists to Room on game over
- [ ] All state changes emit via StateFlow

---

## Phase B: Game Board & Screens (Day 2)

### B1: Home Screen <!-- id: home-screen -->
> **Goal**: Build the main hub with Joker Girl character, game mode buttons, and leaderboard navigation.

**Duration**: 2 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/HomeScreen.kt` | Main hub layout |
| `ui/components/JokerGirl.kt` | Joker Girl character display with reactions |

**Layout (Top to Bottom):**
```
[ Dark carnival background                      ]
[ ────────────────────────────────────────────── ]
[        Joker Girl character (animated idle)    ]
[        🃏 JOKER MEMORY FLIP title             ]
[ ────────────────────────────────────────────── ]
[     ⚔️ PLAY vs AI (button)                    ]
[     👥 PLAY vs PLAYER (button)                ]
[ ────────────────────────────────────────────── ]
[     🏆 Leaderboard (button)                   ]
[ ────────────────────────────────────────────── ]
```

**Key Interactions:**
- Tap "PLAY vs AI" → navigates to ModeSelect with `GameMode.VS_AI`
- Tap "PLAY vs PLAYER" → navigates to ModeSelect with `GameMode.VS_PLAYER`
- Tap "Leaderboard" → navigates to LeaderboardScreen
- Joker Girl has subtle idle animation (breathing/bobbing)

**Exit Criteria:**
- [ ] Joker Girl character renders from asset
- [ ] Navigation to ModeSelect works for both game modes
- [ ] Navigation to Leaderboard works
- [ ] Dark carnival visual theme applied

---

### B2: Mode Select Screen <!-- id: mode-select-screen -->
> **Goal**: Create the mode selection screen — AI difficulty picker for VS AI mode, or player name input for VS Player mode.

**Duration**: 1.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/ModeSelectScreen.kt` | AI difficulty picker or player name input |

**Layout — VS AI Mode:**
```
[ Dark carnival background                      ]
[ ────────────────────────────────────────────── ]
[        CHOOSE YOUR OPPONENT                    ]
[ ────────────────────────────────────────────── ]
[   🤡 FORGETFUL   — "The Clown"               ]
[   "Remembers nothing. Pure chaos."            ]
[ ────────────────────────────────────────────── ]
[   🃏 AVERAGE     — "The Dealer"               ]
[   "Remembers the last 4 cards."               ]
[ ────────────────────────────────────────────── ]
[   👁️ PHOTOGRAPHIC — "The Mind Reader"          ]
[   "Remembers everything. Good luck."          ]
[ ────────────────────────────────────────────── ]
```

**Layout — VS Player Mode:**
```
[ Dark carnival background                      ]
[ ────────────────────────────────────────────── ]
[        ENTER PLAYER NAMES                      ]
[ ────────────────────────────────────────────── ]
[   Player 1: [ text input ]                    ]
[   Player 2: [ text input ]                    ]
[ ────────────────────────────────────────────── ]
[   [ START GAME ] button                       ]
[ ────────────────────────────────────────────── ]
```

**Key Interactions:**
- VS AI: Tap difficulty → start game immediately with "Player" vs "AI (difficulty)"
- VS Player: Enter names → tap "Start Game" → begin match
- Default names: "Player 1" and "Player 2" if left blank

**Exit Criteria:**
- [ ] VS AI mode shows 3 difficulty options with descriptions
- [ ] VS Player mode shows 2 name input fields
- [ ] Tapping difficulty or Start Game navigates to GameBoard
- [ ] GameViewModel receives correct mode, difficulty, and player names

---

### B3: Game Board Screen <!-- id: game-board-screen -->
> **Goal**: Implement the main game screen with 4×4 card grid, scaleX card flip animation, match/mismatch logic, turn indicator, score display, and background cycling.

**Duration**: 4 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/GameBoardScreen.kt` | Main game layout and orchestration |
| `ui/components/CardItem.kt` | Single card with scaleX flip animation |
| `ui/components/CardGrid.kt` | 4×4 grid layout for 14 cards + 2 fillers |
| `ui/components/TurnIndicator.kt` | Active player glow + "YOUR TURN" banner |
| `ui/components/ScoreDisplay.kt` | Player score + collected pairs tray |
| `ui/components/BackgroundScene.kt` | Cycling carnival background per round |

**Game Board Layout:**
```
[ Active player indicator — top bar              ]
[ Opponent score + collected pairs display        ]
[ ─────────────────────────────────────────────  ]
[                                                 ]
[   [ Card ]  [ Card ]  [ Card ]  [ Card ]        ]
[   [ Card ]  [ Card ]  [ Card ]  [ Card ]        ]
[   [ Card ]  [ Card ]  [ Card ]  [ Card ]        ]
[   [ Card ]  [ Card ]  [ Card ]  [ Card ]        ]  ← 2 fillers
[                                                 ]
[ ─────────────────────────────────────────────  ]
[ Active player score + collected pairs display   ]
[ YOUR TURN banner                                ]
```

**Card Flip Animation (scaleX Illusion):**
```kotlin
@Composable
fun CardItem(
    card: CardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(card.isFaceUp) {
        if (card.isFaceUp) {
            // Face-down → Face-up: scaleX 1→0 (fold), swap image, 0→1 (unfold)
            rotation.animateTo(
                targetValue = 180f,
                animationSpec = tween(
                    durationMillis = GameConstants.FLIP_TOTAL_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        } else {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = GameConstants.FLIP_TOTAL_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    val scaleX = if (rotation.value <= 90f) {
        1f - (rotation.value / 90f)
    } else {
        (rotation.value - 90f) / 90f
    }

    val showFace = rotation.value > 90f

    Card(
        modifier = modifier
            .graphicsLayer { this.scaleX = scaleX }
            .clickable(enabled = !card.isFaceUp && !card.isCollected) { onClick() }
    ) {
        if (showFace) {
            Image(painterResource(card.symbol.drawableRes), contentDescription = card.symbol.displayName)
        } else {
            // Card back — dark carnival pattern
            Image(painterResource(R.drawable.bg_round_1), contentDescription = "Card Back")
        }
    }
}
```

**Match Animation:**
- Matched pair glows green for 800ms
- Cards scale up to 1.1× briefly then slide to player's collection tray
- Score counter increments with a bounce animation

**Mismatch Animation:**
- Both cards shake horizontally (spring animation, 200ms)
- Cards fade briefly to red tint
- Flip back after 1000ms reveal window

**Turn Indicator:**
- Active player's side glows with neon border (purple/green)
- "YOUR TURN" banner animates in on turn change
- PvP: "Pass the phone to [Player 2]" overlay appears on turn change

**Background Cycling:**
- Background changes per round: Round 1 → `bg_round_1`, Round 2 → `bg_round_2`, etc.
- Loops after 5 backgrounds

**Exit Criteria:**
- [ ] 4×4 grid displays 14 cards + 2 filler positions correctly
- [ ] Card flip animation plays smoothly (scaleX fold/unfold, 600ms total)
- [ ] Matched pair glows green, scales up, and slides to collection
- [ ] Mismatch cards shake, tint red, flip back after 1000ms
- [ ] Turn indicator shows active player with neon glow
- [ ] "YOUR TURN" banner animates on turn change
- [ ] Background cycles per round through 5 assets
- [ ] Score display shows collected pairs per player
- [ ] AI turn triggers automatically with think delay
- [ ] Cannot tap already-flipped or collected cards

---

### B4: Pass Screen <!-- id: pass-screen -->
> **Goal**: Build the full-screen overlay for PvP pass-and-play phone handoff between turns.

**Duration**: 1 Hour

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/PassScreen.kt` | Full-screen phone handoff overlay |

**Layout:**
```
[ Full opaque dark overlay — hides entire board  ]
[ ────────────────────────────────────────────── ]
[                                                ]
[     🃏 Pass the phone to                      ]
[     [ PLAYER 2 NAME ]                          ]
[                                                ]
[     "Don't peek at the cards!"                 ]
[                                                ]
[     [ I'M READY ] button                       ]
[                                                ]
[ ────────────────────────────────────────────── ]
```

**Behaviour:**
- Appears after every mismatch in VS_PLAYER mode
- Completely covers the board so next player can't see memorised positions
- Shows the name of the next player
- "I'm Ready" button dismisses overlay and reveals board
- Optional: rotate text 180° if Player 2 is sitting across the table

**Exit Criteria:**
- [ ] Pass screen covers the entire game board
- [ ] No card positions visible through the overlay
- [ ] Shows correct next player name
- [ ] "I'm Ready" button dismisses and starts next turn
- [ ] Only appears in VS_PLAYER mode, not VS_AI

---

## Phase C: Special Screens & Polish (Day 3)

### C1: Steal Screen <!-- id: steal-screen -->
> **Goal**: Create the full-screen Joker steal animation with opponent pair display and steal selection.

**Duration**: 2 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/StealScreen.kt` | Full-screen Joker steal selection overlay |

**Steal Sequence:**
1. Screen dims to black (300ms fade)
2. Joker Girl slides in from side with taunt animation
3. "JOKER STEAL!" title slams in with scale animation
4. Opponent's collected pairs display as selectable cards
5. Stealing player taps the pair they want to steal
6. Stolen pair flies across screen to stealer's tray with trail effect
7. Screen returns to game board

**Layout:**
```
[ Full dark overlay with neon accents            ]
[ ────────────────────────────────────────────── ]
[   Joker Girl character (taunt pose)            ]
[   🃏 JOKER STEAL! (animated title)            ]
[ ────────────────────────────────────────────── ]
[   "[Opponent]'s pairs — choose one to steal"   ]
[   [ ♦️ ] [ ♠️ ] [ ♥️ ] [ ♣️ ]  (tappable)      ]
[ ────────────────────────────────────────────── ]
```

**Steal Rules:**
- Only triggers when both Joker cards are matched
- Player who matched Jokers selects 1 of opponent's collected pairs
- If opponent has 0 pairs, steal is skipped (still counts as a match)
- Joker Girl plays special taunt animation on steal
- In VS_AI mode with AI as stealer, AI auto-selects a pair

**Exit Criteria:**
- [ ] Steal screen triggers only on Joker pair match
- [ ] Opponent's collected pairs display as selectable cards
- [ ] Tapping a pair steals it and moves to stealer's collection
- [ ] Stolen pair visually flies across screen
- [ ] Score updates correctly after steal
- [ ] Steal skipped gracefully if opponent has 0 pairs
- [ ] Joker Girl taunt animation plays

---

### C2: Result Screen <!-- id: result-screen -->
> **Goal**: Build the game over screen with winner declaration, Joker Girl reaction, pairs tally, and play again option.

**Duration**: 1.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/ResultScreen.kt` | Winner display, pair tally, play again |

**Layout:**
```
[ Dark carnival background with winner spotlight ]
[ ────────────────────────────────────────────── ]
[   Joker Girl (celebrating / consoling)         ]
[ ────────────────────────────────────────────── ]
[   🏆 [WINNER NAME] WINS!                      ]
[   "with X pairs collected"                     ]
[ ────────────────────────────────────────────── ]
[   Player 1: X pairs   |   Player 2: Y pairs   ]
[   [ ♦️ ♠️ ♥️ ]         |   [ ♣️ 👑 ⭐ 🃏 ]       ]
[ ────────────────────────────────────────────── ]
[   Joker steals used: Z                         ]
[ ────────────────────────────────────────────── ]
[   [ PLAY AGAIN ] button                        ]
[   [ HOME ] button                              ]
[ ────────────────────────────────────────────── ]
```

**Joker Girl Reactions:**
- Winner = Player → Joker Girl celebrates (happy pose)
- Winner = AI → Joker Girl taunts (smirk pose)
- Close game (4-3) → Joker Girl dramatic reaction

**Exit Criteria:**
- [ ] Winner correctly identified (player with 4+ pairs)
- [ ] Pairs tally shows each player's collected symbols
- [ ] Joker Girl reaction matches game outcome
- [ ] "Play Again" restarts with same settings
- [ ] "Home" navigates back to HomeScreen
- [ ] Match record saved to Room before screen displays

---

### C3: Leaderboard Screen <!-- id: leaderboard-screen -->
> **Goal**: Display top 10 win records from Room database.

**Duration**: 1 Hour

**Files to create:**
| File | Description |
|------|-------------|
| `ui/screens/LeaderboardScreen.kt` | Top 10 win records list |

**Layout:**
```
[ Dark carnival background                       ]
[ ────────────────────────────────────────────── ]
[     🏆 HALL OF JOKERS 🏆                      ]
[ ────────────────────────────────────────────── ]
[  #1  🃏 "Player" — 5 wins        Mar 12, 2026 ]
[  #2  🃏 "Alice" — 3 wins         Mar 11, 2026 ]
[  #3  🃏 "Bob" — 2 wins           Mar 10, 2026 ]
[  ...                                           ]
[  #10 🃏 "AI" — 1 win             Mar 08, 2026 ]
[ ────────────────────────────────────────────── ]
[     Recent Matches:                            ]
[     "Player" beat "AI (Average)" 4-3           ]
[     "Alice" beat "Bob" 5-2                     ]
[ ────────────────────────────────────────────── ]
[     [ BACK ] button                            ]
```

**Data Display:**
- Top 10 sorted by win count (from `getTopWinners()` DAO query)
- Each entry shows rank, player name, win count, and last win date
- Recent matches section shows last 10 match results with scores
- Back button returns to Home

**Exit Criteria:**
- [ ] Top 10 winners display from Room
- [ ] Scores sorted by descending win count
- [ ] Recent matches show winner, loser, and pair counts
- [ ] Navigation back to Home works
- [ ] Empty state handles gracefully (no matches yet)

---

### C4: Audio & Haptics <!-- id: audio-haptics -->
> **Goal**: Add sound effects and haptic feedback for all key game interactions.

**Duration**: 1.5 Hours

**Files to create:**
| File | Description |
|------|-------------|
| `game/SoundManager.kt` | SoundPool-based audio playback |
| `game/HapticManager.kt` | Vibration patterns for game events |

**Sound Effects:**
| Event | Sound | Duration |
|-------|-------|----------|
| Card flip | Quick card snap / flip | 0.2s |
| Match found | Positive chime / collect | 0.5s |
| Mismatch | Low buzzer / wrong tone | 0.3s |
| Joker steal triggered | Dramatic sting / laugh | 1.0s |
| Pair stolen | Swoosh / grab | 0.5s |
| Game win | Victory fanfare | 1.5s |
| Game loss | Sad trombone / deflation | 1.0s |
| Turn change | Subtle click | 0.1s |
| Button press | Soft tap | 0.1s |

**Haptic Feedback:**
```kotlin
// Card flip: light pulse (30ms)
// Match found: success double-tap (50ms-30ms-50ms)
// Mismatch: single medium pulse (40ms)
// Joker steal: strong extended (200ms)
// Game win: celebration pattern (100ms-50ms-100ms-50ms-100ms)
```

**Exit Criteria:**
- [ ] All sound effects play at correct moments
- [ ] No audio overlap or lag
- [ ] Haptic feedback fires on flip, match, mismatch, steal, win
- [ ] Sounds respect device volume settings
- [ ] SoundPool properly released on ViewModel clear

---

### C5: Polish & Edge Cases <!-- id: polish -->
> **Goal**: Handle all edge cases, polish UI transitions, and ensure robust state management.

**Duration**: 2 Hours

**Edge Cases to Handle:**
| Edge Case | Expected Behaviour |
|-----------|-------------------|
| Rapid double-tap on same card | Ignore second tap (card already flipping) |
| Tap third card while two are revealed | Ignore until evaluation completes |
| Joker steal with opponent at 0 pairs | Skip steal, still counts as match, player keeps turn |
| All 14 cards collected simultaneously | Game over triggers immediately, winner declared |
| App killed mid-game | Game state lost (acceptable for v1.0 — no save/resume) |
| Device rotation mid-game | State preserved via ViewModel |
| AI vs AI infinite game (impossible) | Player 1 is always human |
| PvP both players same name | Allowed — names are cosmetic only |
| Empty leaderboard on first launch | Show "No matches played yet" message |
| Back button during game | Confirm dialog: "Leave game? Progress will be lost." |

**UI Polish Tasks:**
- Smooth screen transitions (fade/slide via Compose Navigation)
- Consistent dark carnival theme (deep purple, neon green, gold accents)
- Card back design using background asset for uniform Joker card back
- "YOUR TURN" banner with spring animation (overshoots then settles)
- Score counter increments with bounce animation using `animateIntAsState`
- Neon glow border on active player's side using `drawBehind` + `drawRoundRect`
- Card collection tray shows miniature versions of collected symbols
- Filler positions in 4×4 grid are invisible (no card, no tap target)

**Exit Criteria:**
- [ ] All edge cases handled gracefully
- [ ] No crashes across 10 complete matches in both modes
- [ ] Screen transitions are smooth
- [ ] State persists through configuration changes
- [ ] Theme is consistent across all screens
- [ ] Back button shows confirmation dialog during active game

---

### C6: QA & Delivery <!-- id: qa-delivery -->
> **Goal**: Run acceptance criteria verification and build client APK.

**Duration**: 2 Hours

**Acceptance Criteria Checklist:**
| Criterion | Verification Method |
|-----------|-------------------|
| All 14 cards shuffle to unique random positions every new game | Log 5 deck generations, verify no duplicates in position |
| Card flip animation plays smoothly without jank | Visual QA on real device, 20 flips |
| Matched pair is collected and removed from board correctly | QA all 7 symbol pairs including Joker |
| Joker steal triggers only on Joker pair match | Force Joker match in debug, verify steal screen |
| Stolen pair moves from opponent's tray to stealer's tray | Visual QA, verify score updates correctly |
| Mismatch cards flip back after exactly 1000ms | Time with stopwatch, 10 mismatches |
| PvP pass screen fully covers board before Player 2 sees it | Manual test — no card positions visible through overlay |
| Photographic AI always matches cards it has previously seen | Play 3 games on Photographic, verify AI never misses a known pair |
| Forgetful AI never uses memory (random picks only) | Log AI decisions over 20 turns, verify no pattern |
| Win is correctly awarded to player with most pairs (4+) | QA both win scenarios for P1 and P2 |
| Match records persist in Room after app restart | Kill app, reopen leaderboard, verify records intact |
| No crashes across 10 complete matches in both modes | Full QA run |

**Debug Helpers (remove before release):**
```kotlin
object DebugDeck {
    var forceDeck: List<CardState>? = null
    fun generateDeck(): List<CardState> =
        forceDeck ?: CardDeck.generateShuffledDeck()
}

// Force Joker cards to positions 0 and 1 for steal testing
fun debugForceJokerFirst() {
    DebugDeck.forceDeck = CardDeck.generateShuffledDeck().let { deck ->
        val jokerIndices = deck.withIndex().filter { it.value.symbol == CardSymbol.JOKER }.map { it.index }
        deck.toMutableList().apply {
            if (jokerIndices.size == 2) {
                val temp0 = this[0]; this[0] = this[jokerIndices[0]]; this[jokerIndices[0]] = temp0
                val temp1 = this[1]; this[1] = this[jokerIndices[1]]; this[jokerIndices[1]] = temp1
            }
        }
    }
}
```

**Build Tasks:**
- [ ] Remove all debug helpers
- [ ] ProGuard/R8 minification enabled
- [ ] Signed release APK/AAB generated
- [ ] APK size < 15 MB (static assets only, no video/audio files for v1)
- [ ] Test on real device (API 26+ minimum)
- [ ] minSdk set to 26 per PRD requirements

**Exit Criteria:**
- [ ] All 12 acceptance criteria pass
- [ ] Signed APK builds successfully
- [ ] App runs on real Android device (API 26+)
- [ ] No crashes, no ANRs
- [ ] Client APK delivered

---

## 📊 Timeline Summary

```
Day 1 (AM)  ████████████  Phase A1: Project Setup + A2: Data Layer
Day 1 (PM)  ████████████  Phase A3: Game Engine + A4: ViewModels
Day 2 (AM)  ████████████  Phase B1: Home Screen + B2: Mode Select
Day 2 (PM)  ████████████  Phase B3: Game Board Screen + B4: Pass Screen
Day 3 (AM)  ████████████  Phase C1: Steal Screen + C2: Result Screen + C3: Leaderboard
Day 3 (PM)  ████████████  Phase C4-C6: Audio, Polish, QA, APK Delivery
```

**Total Duration: 3 Days**

---

## 🎯 Success Metrics

| Metric | Target |
|--------|--------|
| Frame Rate | 60 FPS on mid-range device |
| Load Time | < 2 seconds to Home Screen |
| APK Size | < 15 MB |
| Card Flip | 600ms total animation (smooth scaleX fold/unfold) |
| Mismatch Reveal | Exactly 1000ms before flip-back |
| AI Think Time | 600–900ms per flip (natural feel) |
| Crash Rate | 0 crashes in 10 complete matches |
| Deck Randomness | No position duplicates across 5 shuffle tests |
| Room Persistence | 100% across app kill/restart |
| Match Duration | 3–5 minutes per match (target) |

---

## ⚠️ Risk Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Card flip animation jank | Medium | High | Use `Animatable` with `tween` + `scaleX`, avoid recomposition in hot path |
| Room DB corruption | Low | High | Default values in entity, fallback to empty leaderboard on error |
| Asset loading lag on grid | Low | Medium | Preload all 7 card drawables + 5 backgrounds on game start |
| AI infinite memory exploit | Low | Medium | Average AI capped at 4-card buffer, tested with logging |
| PvP peek-through on pass | Medium | High | Full opaque overlay, tested manually for card position leaks |
| Joker steal edge case crash | Medium | Medium | Null-safe steal logic, skip if opponent has 0 pairs |
| 4×4 grid layout overflow | Low | Medium | Use 2 invisible filler positions, test on small screens |
| Compose Canvas perf | Low | Medium | Minimal Canvas usage — neon glow only, no particle system for v1 |

---

## 🚫 Scope Boundaries

### In Scope (v1.0)
- 14-card grid (7 symbols × 2) with scaleX card flip animation
- Full match / mismatch / Joker steal mechanics
- Player vs AI — 3 difficulty levels (Forgetful / Average / Photographic)
- Player vs Player — pass-and-play with phone handoff overlay
- Joker steal full-screen animation with pair selection
- Turn indicator with active player neon glow
- Joker Girl reactions on Home, Steal, and Result screens
- Background cycling across 5 assets per round
- Win/loss record leaderboard (Room, top 10)
- Sound effects (flip, match, mismatch, steal, win)
- Haptic feedback on match and steal

### Out of Scope (v1.0)
| Excluded | Reason |
|----------|--------|
| Online multiplayer | Backend required, out of timeline |
| Card power-ups beyond Joker steal | Scope creep, keep mechanics clean |
| Multiple board sizes (e.g. 5×4 = 20 cards) | Post-launch difficulty setting |
| Animated Joker Girl sprite sheet | Static PNG sufficient for v1 |
| Tournament / streak mode | Post-launch feature |
| In-app purchases | Not requested by client |
| Timed mode (countdown per turn) | Post-launch variant |
| Save/resume mid-game | Acceptable loss for 3–5 min matches |

---

*Document Version: 1.0*
*Last Updated: March 2026*
*Project: Joker Memory Flip — Kotlin + Jetpack Compose*
