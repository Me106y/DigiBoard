package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.Pipeline

/**
 * Runs an optimized 5x5 gaussian blur with shader.
 */
internal class GaussianBlurStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    private val xDirection: Boolean,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.gaussian_shader, pipeline) {

    // https://www.rastergrid.com/blog/2010/09/efficient-gaussian-blur-with-linear-sampling/
    private val gaussianOffsets = floatArrayOf(0.0f, 1.3846153846f, 3.2307692308f)
    private val gaussianWeights = floatArrayOf(0.2270270270f, 0.3162162162f, 0.0702702703f)

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


        // Gaussian offsets and weights
        val gaussianOffsetsHandle = glGetUniformLocation(program, "gaussian_offsets")
        glUniform1fv(gaussianOffsetsHandle, gaussianOffsets.size, gaussianOffsets, 0)

        val gaussianWeightsHandle = glGetUniformLocation(program, "gaussian_weights")
        glUniform1fv(gaussianWeightsHandle, gaussianWeights.size, gaussianWeights, 0)

        // Direction
        val directionHandle = glGetUniformLocation(program, "direction")
        glUniform2f(
            directionHandle,
            if (xDirection) { 1.0f } else { 0.0f },
            if (xDirection) { 0.0f } else { 1.0f })
    }
}