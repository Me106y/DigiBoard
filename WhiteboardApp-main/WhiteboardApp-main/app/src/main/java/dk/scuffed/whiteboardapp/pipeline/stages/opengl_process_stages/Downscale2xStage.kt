package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import android.util.Size
import kotlin.math.ceil

/**
 * Downscales 2x, taking the average of groups of 4 pixels.
 * Expects the input texture is sampled with linear filtering.
 */
internal class Downscale2xStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.texture, pipeline) {
    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, Size(ceil(inputFramebufferInfo.textureSize.width / 2.0).toInt(), ceil(inputFramebufferInfo.textureSize.height / 2.0).toInt()))
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // We don't need the framebuffer resolution as it is the same as resolution :^)

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "source_texture")
        glUniform1i(framebufferTextureHandle, inputFramebufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFramebufferInfo.textureHandle)
    }
}