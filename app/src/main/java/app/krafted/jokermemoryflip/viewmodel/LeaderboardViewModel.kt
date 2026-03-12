package app.krafted.jokermemoryflip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import app.krafted.jokermemoryflip.data.MatchDao
import app.krafted.jokermemoryflip.data.MatchRecord
import app.krafted.jokermemoryflip.data.WinnerStat
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel(private val matchDao: MatchDao) : ViewModel() {
    val recentMatches: StateFlow<List<MatchRecord>> = matchDao.getRecentMatches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topWinners: StateFlow<List<WinnerStat>> = matchDao.getTopWinners()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    class Factory(private val matchDao: MatchDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LeaderboardViewModel(matchDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
