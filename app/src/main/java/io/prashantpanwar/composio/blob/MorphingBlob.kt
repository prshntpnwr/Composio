package io.prashantpanwar.composio.blob

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.prashantpanwar.composio.ui.theme.ComposioTheme

@Composable
fun MorphingBlob(
    modifier: Modifier = Modifier,
    morphPoints: Int = 6,
    durationMillis: Int = (4000 + Random.nextInt(2000)),
    blobStyle: BlobStyle = BlobStyle.default()
) {
    val path = remember { Path() }
    val radiusAnimValues = remember { List(morphPoints) { Animatable(Random.nextFloat()) } }

    val angleOffset = remember { (2 * Math.PI / morphPoints * Math.random()).toFloat() }
    val fractions = remember { FloatArray(morphPoints + 1) { it.toFloat() / morphPoints } }

    var translationCenter by remember { mutableStateOf(Offset.Zero) }
    var lastTranslationAngle by remember { mutableStateOf(0f) }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Animate radius
    LaunchedEffect(Unit) {
        radiusAnimValues.forEach { animatable ->
            launch {
                while (true) {
                    val dest = generateDestFractions(fractions)
                    animatable.animateTo(
                        targetValue = dest.random(),
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = durationMillis,
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
            }
        }
    }

    // Animate translation separately to avoid drift
    LaunchedEffect(canvasSize) {
        val width = canvasSize.width.toFloat()
        val height = canvasSize.height.toFloat()
        val outerRadius = (min(width, height) / 2f).coerceAtLeast(1f)
        val r = outerRadius / 4000f
        val outR = outerRadius / 6f

        val cx = width / 2f
        val cy = height / 2f

        while (true) {
            delay(30) // 30 FPS

            val vx = translationCenter.x - cx
            val vy = translationCenter.y - cy
            val ratio = 1 - r / outR
            val wx = vx * ratio
            val wy = vy * ratio

            lastTranslationAngle =
                ((Math.random() - 0.5) * Math.PI / 4 + lastTranslationAngle).toFloat()
            val distRatio = Math.random().toFloat()

            translationCenter = Offset(
                cx + wx + r * distRatio * cos(lastTranslationAngle.toDouble()).toFloat(),
                cy + wy + r * distRatio * sin(lastTranslationAngle.toDouble()).toFloat()
            )
        }
    }

    Canvas(modifier = modifier
        .graphicsLayer {
            alpha = blobStyle.effect.alpha
        }
        .onSizeChanged { size ->
            canvasSize = size
            translationCenter = Offset(size.width / 2f, size.height / 2f)
        }
    ) {
        if (canvasSize.width == 0 || canvasSize.height == 0) return@Canvas
        val width = size.width
        val height = size.height
        val cx = translationCenter.x
        val cy = translationCenter.y

        val outerRadius = min(width, height) / 2f
        val innerRadius = outerRadius * 0.75f
        val ringWidth = outerRadius - innerRadius

        val xs = FloatArray(morphPoints)
        val ys = FloatArray(morphPoints)

        val paint = Paint().asFrameworkPaint().apply {
            isAntiAlias = true

            // Apply background style
            when (blobStyle.shape) {
                is BlobShape.Fill -> {
                    style = android.graphics.Paint.Style.FILL
                }

                is BlobShape.Stroke -> {
                    style = android.graphics.Paint.Style.STROKE
                    strokeWidth = blobStyle.shape.strokeWidth * density
                }
            }

            // Apply shader
            shader = blobStyle.shader.toAndroidShader(
                BlobShaderContext(
                    center = Offset(cx, cy),
                    radius = outerRadius,
                    size = Size(width, height),
                    density = density
                )
            )

            // Apply glow and alpha
            maskFilter = BlurMaskFilter(
                blobStyle.effect.blurRadius.coerceAtLeast(1f) * density,
                BlurMaskFilter.Blur.NORMAL
            )
        }

        for (i in 0 until morphPoints) {
            val t = radiusAnimValues[i].value
            val r = innerRadius + ringWidth * t
            val angle = (2 * Math.PI / morphPoints * i).toFloat() + angleOffset
            xs[i] = cx + r * cos(angle.toDouble()).toFloat()
            ys[i] = cy + r * sin(angle.toDouble()).toFloat()
        }

        path.reset()
        path.moveTo(xs[0], ys[0])

        for (i in 0 until morphPoints) {
            val curr = Offset(xs.circular(i), ys.circular(i))
            val next = Offset(xs.circular(i + 1), ys.circular(i + 1))

            val v1 = getVector(xs, ys, i)
            val v2 = getVector(xs, ys, i + 1)

            path.cubicTo(
                curr.x + v1.x, curr.y + v1.y,
                next.x - v2.x, next.y - v2.y,
                next.x, next.y
            )
        }

        drawIntoCanvas {
            it.nativeCanvas.drawPath(path.asAndroidPath(), paint)
        }
    }
}

private fun FloatArray.circular(index: Int): Float {
    if (isEmpty()) error("Array cannot be empty")
    return this[(index % size + size) % size]
}

private const val LINE_SMOOTHNESS = 0.16f

private fun getVector(xs: FloatArray, ys: FloatArray, i: Int): Offset {
    val next = Offset(xs.circular(i + 1), ys.circular(i + 1))
    val prev = Offset(xs.circular(i - 1), ys.circular(i - 1))
    return (next - prev) * LINE_SMOOTHNESS
}

private fun generateDestFractions(fractions: FloatArray): List<Float> {
    if (fractions.isEmpty()) return emptyList()

    val startIndex = (fractions.indices).random()

    return buildList {
        addAll(fractions.slice(startIndex until fractions.size))
        addAll(fractions.slice(0 until startIndex).reversed())
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposioTheme {
        MorphingBlob(
            modifier = Modifier.size(260.dp),
            morphPoints = 6,
            blobStyle = BlobStyle(
                effect = BlobEffect(blurRadius = 1f, alpha = 0.5f),
                shader = BlobShader.Radial(colors = listOf(Color(0xFF673AB7), Color(0xFFF97272))),
                shape = BlobShape.Fill
            )
        )
    }
}