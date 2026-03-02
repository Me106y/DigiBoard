package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage

/**
 * Applies image adjustments in HSV space.
 * Hue, saturation and value
 */
internal class HSVAdjustmentsStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.saturate_shader, pipeline) {
    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, inputFramebufferInfo.textureSize)
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