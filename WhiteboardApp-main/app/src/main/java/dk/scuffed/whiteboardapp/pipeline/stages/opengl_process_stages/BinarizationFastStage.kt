package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage

/**
 * Does binarization using an adaptive threshold with a shader.
 */
internal class BinarizationFastStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    private val downscaledInputFramebufferInfo: FramebufferInfo,
    private val threshold: Float,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.binarization_fast_shader, pipeline) {

    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, inputFramebufferInfo.textureSize)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "framebuffer")
        glUniform1i(framebufferTextureHandle, inputFramebufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFramebufferInfo.textureHandle)

        // Input downscaled framebuffer
        val framebufferDownscaledTextureHandle = glGetUniformLocation(program, "downscaledFramebuffer")
        glUniform1i(framebufferDownscaledTextureHandle, downscaledInputFramebufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(downscaledInputFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, downscaledInputFramebufferInfo.textureHandle)

        // threshold
        val thresholdHandle = glGetUniformLocation(program, "threshold")
        glUniform1f(thresholdHandle, threshold)

    }
}