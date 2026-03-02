package dk.scuffed.whiteboardapp.pipeline.stages.segmentation_stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.segmentation.PPSegmentation

/**
 * Downscales and changes axis of input to match expected input of the segmentation.
 */
internal class SegmentationPreProcessingStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    private val segmentationModel: PPSegmentation.Model,
    pipeline: IPipeline,
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.segment_preprocess_shader, pipeline) {
    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, Size(segmentationModel.width, segmentationModel.height))
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // We don't need the framebuffer resolution as it is the same as resolution :^)

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "framebuffer")
        glUniform1i(framebufferTextureHandle, inputFramebufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFramebufferInfo.textureHandle)
    }
}