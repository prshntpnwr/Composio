package io.prashantpanwar.composio.blob

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Encapsulates the visual styling of a morphing blob.
 */
data class BlobStyle(
    val effect: BlobEffect = BlobEffect(),
    val shader: BlobShader = BlobShader.Radial(),
    val shape: BlobShape = BlobShape.Fill
) {
    companion object {
        fun default(): BlobStyle = BlobStyle()

        fun glow(): BlobStyle = BlobStyle(
            effect = BlobEffect(alpha = 0.6f, blurRadius = 50f),
            shader = BlobShader.Radial(
                colors = listOf(Color(0xFFF44336), Color.Transparent)
            )
        )

        fun softStroke(): BlobStyle = BlobStyle(
            shape = BlobShape.Stroke(strokeWidth = 2f),
            effect = BlobEffect(alpha = 0.8f, blurRadius = 12f),
            shader = BlobShader.Linear(
                colors = listOf(Color.Cyan, Color.Magenta),
                to = Offset(0f, 400f)
            )
        )
    }
}