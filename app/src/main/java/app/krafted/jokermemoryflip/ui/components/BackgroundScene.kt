package app.krafted.jokermemoryflip.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import app.krafted.jokermemoryflip.ui.theme.DarkPurple

@Composable
fun BackgroundScene(
    backgroundRes: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = backgroundRes,
            transitionSpec = {
                fadeIn(animationSpec = tween(500))
                    .togetherWith(fadeOut(animationSpec = tween(500)))
                    .using(SizeTransform(clip = false))
            },
            label = "backgroundTransition"
        ) { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = DarkPurple.copy(alpha = 0.65f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            DarkPurple.copy(alpha = 0.8f)
                        ),
                        radius = 1200f
                    )
                )
        )
    }
}
