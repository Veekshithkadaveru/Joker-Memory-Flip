package app.krafted.jokermemoryflip.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.jokermemoryflip.game.GameMode
import app.krafted.jokermemoryflip.game.Player
import app.krafted.jokermemoryflip.ui.theme.GoldAccent
import app.krafted.jokermemoryflip.ui.theme.GoldDark
import app.krafted.jokermemoryflip.ui.theme.GoldLight
import app.krafted.jokermemoryflip.ui.theme.MidPurple
import app.krafted.jokermemoryflip.ui.theme.NeonPurple
import app.krafted.jokermemoryflip.ui.theme.RichPurple

@Composable
fun TurnIndicator(
    activePlayer: Player,
    player1Name: String,
    player2Name: String,
    gameMode: GameMode,
    modifier: Modifier = Modifier
) {
    val activeName = if (activePlayer == Player.ONE) player1Name else player2Name
    val isAi = gameMode == GameMode.VS_AI && activePlayer == Player.TWO
    val pillShape = RoundedCornerShape(24.dp)
    val glowColor = if (isAi) NeonPurple else GoldAccent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .drawBehind {
                    val expandPx = 10.dp.toPx()
                    drawRoundRect(
                        color = glowColor.copy(alpha = 0.08f),
                        topLeft = Offset(-expandPx * 2, -expandPx * 2),
                        size = Size(size.width + expandPx * 4, size.height + expandPx * 4),
                        cornerRadius = CornerRadius(52f, 52f)
                    )
                    drawRoundRect(
                        color = glowColor.copy(alpha = 0.14f),
                        topLeft = Offset(-expandPx, -expandPx),
                        size = Size(size.width + expandPx * 2, size.height + expandPx * 2),
                        cornerRadius = CornerRadius(48f, 48f)
                    )
                }
                .clip(pillShape)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            RichPurple.copy(alpha = 0.9f),
                            MidPurple.copy(alpha = 0.95f),
                            RichPurple.copy(alpha = 0.9f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.3f),
                            glowColor.copy(alpha = 0.8f),
                            glowColor.copy(alpha = 0.3f)
                        )
                    ),
                    shape = pillShape
                )
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            AnimatedContent(
                targetState = activeName to isAi,
                transitionSpec = {
                    (fadeIn(tween(200)) + slideInVertically(tween(200)) { -12 })
                        .togetherWith(fadeOut(tween(150)) + slideOutVertically(tween(150)) { 12 })
                },
                label = "turnIndicatorContent"
            ) { (name, aiTurn) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TurnDot(color = glowColor)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = name.take(14).uppercase(),
                        color = if (aiTurn) Color.White else GoldLight,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "· TURN",
                        color = glowColor.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TurnDot(color = glowColor)
                }
            }
        }
    }
}

@Composable
private fun TurnDot(color: Color) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color, GoldDark.copy(alpha = 0.3f))
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            )
    )
}
