package io.prashantpanwar.composio.blob

/**
 * Defines visual effects like transparency, blur, and optional blend mode.
 */
data class BlobEffect(
    val alpha: Float = 1f,
    val blurRadius: Float = 0f,
)
