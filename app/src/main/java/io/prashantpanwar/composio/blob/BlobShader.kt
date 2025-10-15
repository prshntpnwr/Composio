package io.prashantpanwar.composio.blob

import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb

/**
 * Encapsulates shader behavior for blob fill.
 */
sealed class BlobShader {
    abstract fun toAndroidShader(context: BlobShaderContext): Shader

    /**
     * Radial gradient shader.
     */
    data class Radial(
        val colors: List<Color> = listOf(Color(0xFFE57373), Color.Transparent),
        val stops: List<Float>? = null,
        val tileMode: Shader.TileMode = Shader.TileMode.CLAMP
    ) : BlobShader() {
        override fun toAndroidShader(context: BlobShaderContext): Shader {
            return RadialGradient(
                context.center.x,
                context.center.y,
                context.radius.coerceAtLeast(1f),
                List(maxOf(2, colors.size)) { i -> colors[i % colors.size] }.map { it.toArgb() }
                    .toIntArray(),
                stops?.toFloatArray(),
                tileMode
            )
        }
    }

    /**
     * Linear gradient shader.
     */
    data class Linear(
        val colors: List<Color> = listOf(Color(0xFFE57373), Color(0xFFE57373)),
        val from: Offset = Offset.Zero,
        val to: Offset = Offset.Zero,
        val tileMode: TileMode = TileMode.Mirror
    ) : BlobShader() {
        override fun toAndroidShader(context: BlobShaderContext): Shader {
            return LinearGradientShader(
                from = from,
                to = to,
                colors = List(maxOf(2, colors.size)) { i -> colors[i % colors.size] },
                tileMode = tileMode
            )
        }
    }


    // Future: Sweep, Procedural, Noise, Bitmap etc.
    // data class Sweep(...)

}

