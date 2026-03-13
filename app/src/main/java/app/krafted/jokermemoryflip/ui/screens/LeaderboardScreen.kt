package app.krafted.jokermemoryflip.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.R
import app.krafted.jokermemoryflip.data.MatchRecord
import app.krafted.jokermemoryflip.data.WinnerStat
import app.krafted.jokermemoryflip.ui.theme.*
import app.krafted.jokermemoryflip.viewmodel.LeaderboardViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel,
    onBackClick: () -> Unit
) {
    val topWinners by viewModel.topWinners.collectAsState()
    val recentMatches by viewModel.recentMatches.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_round_5),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.20f,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkPurple.copy(alpha = 0.85f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "HALL OF JOKERS",
                    color = GoldAccent,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
            }

            HorizontalDivider(
                color = GoldAccent.copy(alpha = 0.4f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (topWinners.isEmpty() && recentMatches.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matches played yet",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (topWinners.isNotEmpty()) {
                        item {
                            SectionHeader(title = "TOP WINNERS")
                        }

                        itemsIndexed(topWinners) { index, winner ->
                            WinnerRow(rank = index + 1, winner = winner)
                        }

                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }

                    if (recentMatches.isNotEmpty()) {
                        item {
                            SectionHeader(title = "RECENT MATCHES")
                        }

                        itemsIndexed(recentMatches) { _, match ->
                            RecentMatchRow(match = match)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = GoldAccent,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun WinnerRow(rank: Int, winner: WinnerStat) {
    val rankColor = when (rank) {
        1 -> GoldAccent
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color.White.copy(alpha = 0.6f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .border(
                width = 1.dp,
                color = rankColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(rankColor.copy(alpha = 0.15f))
                .border(width = 1.dp, color = rankColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                color = rankColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = winner.winnerName,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${winner.wins}",
                color = rankColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (winner.wins == 1) "win" else "wins",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun RecentMatchRow(match: MatchRecord) {
    val dateStr = remember(match.date) {
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(match.date))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DeepPurple)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = match.winnerName,
                    color = GoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "  vs  ",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 13.sp
                )
                Text(
                    text = match.loserName,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${match.winnerPairs} - ${match.loserPairs}",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Text(
                    text = "  \u2022  ",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 12.sp
                )
                Text(
                    text = match.gameMode.replace("_", " "),
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = dateStr,
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 11.sp,
            textAlign = TextAlign.End
        )
    }
}
