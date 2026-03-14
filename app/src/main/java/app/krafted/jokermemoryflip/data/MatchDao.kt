package app.krafted.jokermemoryflip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert
    suspend fun insertMatch(record: MatchRecord)

    @Query("SELECT * FROM match_records ORDER BY date DESC LIMIT 10")
    fun getRecentMatches(): Flow<List<MatchRecord>>

    @Query("SELECT winnerName, COUNT(*) as wins FROM match_records WHERE winnerName NOT LIKE 'AI (%)' GROUP BY winnerName ORDER BY wins DESC LIMIT 10")
    fun getTopWinners(): Flow<List<WinnerStat>>

    @Query("SELECT COUNT(*) FROM match_records WHERE winnerName = :name")
    fun getWinCount(name: String): Flow<Int>
}

data class WinnerStat(
    val winnerName: String,
    val wins: Int
)
