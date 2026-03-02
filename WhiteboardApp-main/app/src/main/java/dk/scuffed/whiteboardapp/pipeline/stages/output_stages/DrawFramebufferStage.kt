package dk.scuffed.whiteboardapp.pipeline.stages.output_stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair

/**
 * Draws a framebuffer to the screen.
 */
internal class DrawFramebufferStage(
    context: Context,
    private val inputFrameBufferInfo: FramebufferInfo,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.passthrough_shader, pipeline) {

    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        // Set framebuffer to screen
        frameBufferInfo = FramebufferInfo(0, 0, TextureUnitPair(0, 0), 0, getResolution())
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // Input framebuffer resolution
        val framebufferResolutionHandle = glGetUniformLocation(program, "framebuffer_resolution")
        glUniform2f(
            framebufferResolutionHandle,
            inputFrameBufferInfo.textureSize.width.toFloat(),
            inputFrameBufferInfo.textureSize.height.toFloat()
        )

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "framebuffer")
        glUniform1i(framebufferTextureHandle, inputFrameBufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFrameBufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFrameBufferInfo.textureHandle)
    }
}