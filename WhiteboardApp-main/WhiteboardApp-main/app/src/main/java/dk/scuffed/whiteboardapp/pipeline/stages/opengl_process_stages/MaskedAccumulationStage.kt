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
internal class MaskedAccumulationStage(
    context: Context,
    private val inputFramebuffer: FramebufferInfo,
    private val oldAccumulator: FramebufferInfo,
    private val maskFramebuffer: FramebufferInfo,
    private val accumulationFactor: Float,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.masked_accumulation_shader, pipeline) {

    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, inputFramebuffer.textureSize)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "framebuffer")
        glUniform1i(framebufferTextureHandle, inputFramebuffer.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFramebuffer.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFramebuffer.textureHandle)

        // Input old accumulator framebuffer
        val oldAccumulatorHandle = glGetUniformLocation(program, "old_accumulator_framebuffer")
        glUniform1i(oldAccumulatorHandle, oldAccumulator.textureUnitPair.textureUnitIndex)
        glActiveTexture(oldAccumulator.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, oldAccumulator.textureHandle)

        // Input mask framebuffer
        val maskHandle = glGetUniformLocation(program, "mask")
        glUniform1i(maskHandle, maskFramebuffer.textureUnitPair.textureUnitIndex)
        glActiveTexture(maskFramebuffer.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, maskFramebuffer.textureHandle)

        // Input accumulation factor
        val accumulationFactorHandle = glGetUniformLocation(program, "accumulation_factor")
        glUniform1f(accumulationFactorHandle, accumulationFactor)
    }
}