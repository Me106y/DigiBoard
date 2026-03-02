package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage

/**
 * Accumulates the segmentation stage onto a accumulator,
 * requiring multiple frames before counting as not segmented.
 */
internal class SegmentationAccumulationStage(
    context: Context,
    private val segmentationFramebuffer: FramebufferInfo,
    private val accumulationFactor: Float,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.segmentation_accumulation_shader, pipeline) {

    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, segmentationFramebuffer.textureSize)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "segmentation")
        glUniform1i(framebufferTextureHandle, segmentationFramebuffer.textureUnitPair.textureUnitIndex)
        glActiveTexture(segmentationFramebuffer.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, segmentationFramebuffer.textureHandle)

        // Input old accumulator framebuffer
        val oldAccumulatorHandle = glGetUniformLocation(program, "oldAccumulator")
        glUniform1i(oldAccumulatorHandle, frameBufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(frameBufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferInfo.textureHandle)

        // Input old accumulator framebuffer
        val accumulationFactorHandle = glGetUniformLocation(program, "accumulation_factor")
        glUniform1f(accumulationFactorHandle, accumulationFactor)


    }
}