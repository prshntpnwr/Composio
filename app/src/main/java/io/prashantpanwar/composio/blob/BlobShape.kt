package io.prashantpanwar.composio.blob

/**
 * Defines how the blob is visually shaped—either filled or stroked.
 */
sealed class BlobShape {
    data object Fill : BlobShape()

    data class Stroke(
        val strokeWidth: Float = 1.5f
    ) : BlobShape()
}
