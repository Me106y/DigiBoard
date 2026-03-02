package dk.scuffed.whiteboardapp.pipeline

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.stages.Stage

internal interface IPipeline {
    fun draw()
    fun onResolutionChanged(resolution: Size)
    fun getInitialResolution(): Size
    fun addStage(stage: Stage)
    fun allocateFramebuffer(
        stage: Stage,
        textureFormat: Int,
        size: Size
    ): FramebufferInfo

    fun allocateTextureUnit(stage: Stage): TextureUnitPair
}