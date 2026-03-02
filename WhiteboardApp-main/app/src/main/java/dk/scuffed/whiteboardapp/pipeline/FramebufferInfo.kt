package dk.scuffed.whiteboardapp.pipeline

import android.util.Size

internal class FramebufferInfo(
    val fboHandle: Int,
    val textureHandle: Int,
    val textureUnitPair: TextureUnitPair,
    val textureFormat: Int,
    val textureSize: Size
)