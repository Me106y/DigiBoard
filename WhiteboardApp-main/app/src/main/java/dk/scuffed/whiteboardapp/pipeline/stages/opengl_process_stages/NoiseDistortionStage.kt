package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import java.nio.ByteBuffer


/**
 * A silly shader that distorts the UVs of the input over time.
 */
internal class NoiseDistortionStage(
    context: Context,
    private val inputFrameBufferInfo: FramebufferInfo,
    pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.shaderfun, pipeline) {
    private val textureUnitPair: TextureUnitPair
    private val textureHandle: Int

    private val time = System.currentTimeMillis()

    init {
        setup()

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.noisetexture)
        val pair = loadTexture(bitmap, pipeline, this)
        textureUnitPair = pair.first
        textureHandle = pair.second
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, inputFrameBufferInfo.textureSize)
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

        // Pass in the noise texture
        val noiseTextureHandle = glGetUniformLocation(program, "noise")
        glUniform1i(noiseTextureHandle, textureUnitPair.textureUnitIndex)
        glActiveTexture(textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)

        // Pass in time (LOW PRECISION)
        val timeHandle = glGetUniformLocation(program, "time")
        glUniform1f(timeHandle, (System.currentTimeMillis() - time) / 1000f)
    }
}
