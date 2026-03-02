package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage

/**
 * Performs whitebalancing on input1 based on input2, which should represent the background color of the whiteboard.
 */
internal class WhitebalanceStage(
    context: Context,
    private val rawInputFrameBuffer: FramebufferInfo,
    private val backgroundColorFrameBuffer: FramebufferInfo,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.whitebalance_shader, pipeline) {
    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, rawInputFrameBuffer.textureSize)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // We don't need the framebuffer resolution as it is the same as resolution :^)

        // Input framebuffer
        val rawInputFrameBufferTextureHandle = glGetUniformLocation(program, "rawInput")
        glUniform1i(rawInputFrameBufferTextureHandle, rawInputFrameBuffer.textureUnitPair.textureUnitIndex)
        glActiveTexture(rawInputFrameBuffer.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, rawInputFrameBuffer.textureHandle)

        // Input framebuffer
        val backgroundColorTextureHandle = glGetUniformLocation(program, "backgroundColor")
        glUniform1i(backgroundColorTextureHandle, backgroundColorFrameBuffer.textureUnitPair.textureUnitIndex)
        glActiveTexture(backgroundColorFrameBuffer.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, backgroundColorFrameBuffer.textureHandle)

    }
}