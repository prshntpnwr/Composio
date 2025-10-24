package io.prashantpanwar.composio.rippleWave

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.util.lerp
import androidx.core.graphics.toColorInt
import kotlin.math.hypot
import kotlin.math.min

@Composable
fun ContinuousNeumorphicRipple(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFF0F0F3),
    rings: Int = 3,
    durationMillis: Int = 10000
) {
    val infinite = rememberInfiniteTransition()

    val clock = infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier.background(backgroundColor)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = hypot(size.width, size.height) / 2f * 1.2f // flow outside bounds
        val baseStroke = min(size.width, size.height) / 14f

        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            val offsetPx = 6f
            val blurRadius = 20f
            val highlightColor = android.graphics.Color.WHITE
            val shadowColor = "#AEAEC0".toColorInt()

            repeat(rings) { index ->
                // Each ripple is just the same clock, shifted in phase
                val phaseShift = 1f / rings * index
                val progress = (clock.value + phaseShift) % 1f

                val radius = lerp(0f, maxRadius, progress)
                val stroke = baseStroke * (1f - progress * 0.7f)
                val alpha = (1f - progress).coerceIn(0f, 1f)

                // Highlight ring
                val highlightPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = stroke
                    color = highlightColor
                    this.alpha = (alpha * 255).toInt()
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                }
                native.drawCircle(cx - offsetPx, cy - offsetPx, radius, highlightPaint)

                // Shadow ring
                val shadowPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = stroke
                    color = shadowColor
                    this.alpha = (alpha * 255 * 0.25f).toInt()
                    maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                }
                native.drawCircle(cx + offsetPx, cy + offsetPx, radius, shadowPaint)
            }

            // center cutout (static)
            val ringSpacing = baseStroke * 2f
            val cutoutRadius = maxRadius - rings * ringSpacing + (ringSpacing / 2f)
            val fillPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                color = backgroundColor.toArgb()
            }
            native.drawCircle(cx, cy, cutoutRadius, fillPaint)
        }
    }
}
