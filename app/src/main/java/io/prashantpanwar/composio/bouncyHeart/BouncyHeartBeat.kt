package io.prashantpanwar.composio.bouncyHeart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.prashantpanwar.composio.ui.theme.ComposioTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BouncyHeartBeat(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedLayeredHearts(modifier)
    }
}

@Composable
fun AnimatedLayeredHearts(
    modifier: Modifier = Modifier,
    heartColor: Color = Color(0xFFFF5A5F),
    centerSize: Dp = 64.dp
) {
    val baseScales = listOf(4.5f, 2.5f, 1f)
    val alphas = listOf(0.1f, 0.2f, 1f)
    val beatDuration = 500
    val delayBetween = 50

    val animatedScales = remember {
        baseScales.map { Animatable(1f) }
    }

    // Launch staggered animations
    LaunchedEffect(Unit) {
        while (true) {
            // Animate all hearts with staggered launch
            animatedScales.indices.reversed().forEach { actualIndex ->
                val anim = animatedScales[actualIndex]
                launch {
                    delay((animatedScales.size - 1 - actualIndex) * delayBetween.toLong())
                    anim.animateTo(
                        1.25f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    anim.animateTo(
                        1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }

            // Pause after all animations complete
            val totalCycleTime =
                delayBetween * (animatedScales.size - 1) + beatDuration // approx total duration
            delay(totalCycleTime + 700L) // 1-second rest after one full pulse cycle
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        animatedScales.zip(baseScales).zip(alphas).forEach { (scalePair, alpha) ->
            val (animScale, baseScale) = scalePair
            HeartShape(
                modifier = Modifier
                    .size(centerSize * baseScale)
                    .scale(animScale.value),
                color = heartColor.copy(alpha = alpha)
            )
        }
    }
}

@Composable
fun HeartShape(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(width / 2f, height * 1.1f)
            cubicTo(
                width * 1.4f, height * 0.6f,
                width * 1.1f, height * -0.15f,
                width / 2f, height * 0.2f
            )
            cubicTo(
                -width * 0.1f, height * -0.15f,
                -width * 0.4f, height * 0.6f,
                width / 2f, height * 1.1f
            )
        }

        drawPath(path = path, color = color, style = Fill)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStaticLayeredHearts() {
    ComposioTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AnimatedLayeredHearts()
        }
    }
}