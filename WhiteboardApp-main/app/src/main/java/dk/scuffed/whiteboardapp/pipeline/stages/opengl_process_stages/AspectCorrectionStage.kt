package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import kotlin.math.roundToInt

internal class AspectCorrectionStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    private val targetAspectRatio: Double,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.passthrough_shader, pipeline) {
    override fun setupFramebufferInfo() {
        val targetResolution = convertToAspectRatio(inputFramebufferInfo.textureSize, targetAspectRatio)
        return allocateFramebuffer(GLES20.GL_RGBA, targetResolution)
    }

    companion object {
        fun convertToAspectRatio(sourceResolution: Size, aspectRatio: Double): Size {
            val sourceAspectRatio = sourceResolution.width.toDouble() / sourceResolution.height.toDouble()
            return if (sourceAspectRatio < aspectRatio) {
                val width = sourceResolution.height * aspectRatio
                Size(width.roundToInt(), sourceResolution.height)
            } else if (sourceAspectRatio > aspectRatio) {
                val height = sourceResolution.width.toDouble() / aspectRatio
                Size(sourceResolution.width, height.roundToInt())
            } else {
                sourceResolution
            }
        }
    }
}