package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.graphics.Bitmap
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int
import java.nio.ByteBuffer

/**
 * Writes the framebuffer into the bitmap each update.
 */
internal class FramebufferToBitmapStage(
    private val inputFramebufferInfo: FramebufferInfo,
    config: Bitmap.Config,
    pipeline: IPipeline
) : BitmapOutputStage(
    pipeline,
    inputFramebufferInfo.textureSize,
    config
) {

    private val byteBuffer: ByteBuffer

    init {
        val width = inputFramebufferInfo.textureSize.width
        val height = inputFramebufferInfo.textureSize.height
        val bytesPerPixel = bytesPerPixel(inputFramebufferInfo.textureFormat)

        byteBuffer = ByteBuffer.allocateDirect(width * height * bytesPerPixel)
    }

    override fun update() {
        readFrameBuffer(outputBitmap, inputFramebufferInfo)
    }

    private fun readFrameBuffer(bitmap: Bitmap, framebufferInfo: FramebufferInfo) {
        byteBuffer.position(0)

        glBindFramebuffer(inputFramebufferInfo.fboHandle)
        glActiveTexture(inputFramebufferInfo.textureUnitPair.textureUnit)
        glReadPixels(Vec2Int(0, 0), inputFramebufferInfo.textureSize, GLES20.GL_RGBA, byteBuffer)

        bitmap.copyPixelsFromBuffer(byteBuffer)
    }
}