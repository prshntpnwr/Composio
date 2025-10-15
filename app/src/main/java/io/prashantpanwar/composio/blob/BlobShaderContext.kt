package io.prashantpanwar.composio.blob

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

/**
 * Context passed into shaders containing draw-time info.
 */
data class BlobShaderContext(
    val center: Offset,
    val radius: Float,
    val size: Size,
    val density: Float
)