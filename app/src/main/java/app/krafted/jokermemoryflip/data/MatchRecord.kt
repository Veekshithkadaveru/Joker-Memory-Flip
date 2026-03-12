package app.krafted.jokermemoryflip.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_records")
data class MatchRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val winnerName: String,
    val loserName: String,
    val winnerPairs: Int,
    val loserPairs: Int,
    val gameMode: String,
    val aiDifficulty: String? = null,
    val jokerStealsUsed: Int = 0,
    val date: Long = System.currentTimeMillis()
)
