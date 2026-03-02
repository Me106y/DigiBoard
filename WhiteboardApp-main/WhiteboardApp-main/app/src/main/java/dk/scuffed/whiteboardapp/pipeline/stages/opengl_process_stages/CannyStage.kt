package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage

/**
 * Runs canny edge detection.
 * Input should be sobel.
 */
internal class CannyStage(
    context: Context,
    private val inputSobelFramebufferInfo: FramebufferInfo,
    private val weakEdgeThreshold: Float,
    private val hardEdgeThreshold: Float,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.canny_shader, pipeline) {

    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, inputSobelFramebufferInfo.textureSize)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        val weakEdgeThresholdHandle = glGetUniformLocation(program, "weak_edge_threshold")
        glUniform1f(weakEdgeThresholdHandle, weakEdgeThreshold)

        val hardEdgeThresholdHandle = glGetUniformLocation(program, "hard_edge_threshold")
        glUniform1f(hardEdgeThresholdHandle, hardEdgeThreshold)

        val inputFramebufferHandle = glGetUniformLocation(program, "input_framebuffer")
        glUniform1i(
            inputFramebufferHandle,
            inputSobelFramebufferInfo.textureUnitPair.textureUnitIndex
        )
        glActiveTexture(inputSobelFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputSobelFramebufferInfo.textureHandle)
    }
}