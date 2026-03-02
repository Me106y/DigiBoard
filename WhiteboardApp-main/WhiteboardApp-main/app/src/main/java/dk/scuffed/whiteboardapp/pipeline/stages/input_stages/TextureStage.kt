package dk.scuffed.whiteboardapp.pipeline.stages.input_stages

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import java.nio.ByteBuffer


/**
 * Outputs a bitmap given in constructor as a OpenGL framebuffer.
 */
internal class TextureStage(
    context: Context,
    private val texture: Bitmap,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.image_shader, pipeline) {
    private val textureUnitPair: TextureUnitPair
    private val textureHandle: Int

    init {
        setup()
        val pair = loadTexture(texture, pipeline, this)
        textureUnitPair = pair.first
        textureHandle = pair.second

    }

    override fun setupFramebufferInfo() {
        val resolution = Size(texture.height, texture.width)
        allocateFramebuffer(GLES20.GL_RGBA, resolution)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // Pass in the texture
        val noiseTextureHandle = glGetUniformLocation(program, "source_texture")
        glUniform1i(noiseTextureHandle, textureUnitPair.textureUnitIndex)
        glActiveTexture(textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)

        // Input framebuffer resolution
        val framebufferResolutionHandle = glGetUniformLocation(program, "resolution")
        glUniform2f(framebufferResolutionHandle, texture.height.toFloat(), texture.width.toFloat())
    }
}