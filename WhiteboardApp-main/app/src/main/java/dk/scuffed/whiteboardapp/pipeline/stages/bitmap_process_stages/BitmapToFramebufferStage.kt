package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Size
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage

/**
 * Writes the bitmap onto the framebuffer each update.
 */
internal class BitmapToFramebufferStage(
    private val inputBitmap: BitmapOutputStage,
    private val pipeline: IPipeline
) : Stage(pipeline) {

    val frameBufferInfo = allocateTexture()

    override fun update() {
        glActiveTexture(frameBufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferInfo.textureHandle)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, inputBitmap.outputBitmap, 0)
    }

    private fun allocateTexture(): FramebufferInfo {
        val textureUnitPair = pipeline.allocateTextureUnit(this)
        glActiveTexture(textureUnitPair.textureUnit)

        val textureHandle = glGenTexture()
        glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, inputBitmap.outputBitmap, 0)
        glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return FramebufferInfo(
            999,
            textureHandle,
            textureUnitPair,
            GLES20.GL_RGBA,
            Size(inputBitmap.outputBitmap.width, inputBitmap.outputBitmap.height)
        )
    }
}