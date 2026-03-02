package dk.scuffed.whiteboardapp.pipeline

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.util.Size
import com.google.common.io.ByteStreams
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import dk.scuffed.whiteboardapp.utils.Vec2Float
import java.nio.ByteBuffer
import kotlin.math.abs

@Suppress("UnstableApiUsage")
fun readRawResource(context: Context, resourceId: Int): String {
    val stream =
        context.resources.openRawResource(resourceId)
    val string = String(ByteStreams.toByteArray(stream))
    stream.close()

    return string
}

// https://www.mathopenref.com/coordtrianglearea.html
fun areaOfTriangle(a: Vec2Float, b: Vec2Float, c: Vec2Float): Float {
    return abs((a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y)) / 2.0f)
}

/**
 * Loads a texture from a bitmap
 * @return A pair of textureUnitPair and the textureHandle
 */
internal fun loadTexture(texture: Bitmap, pipeline: IPipeline, thisStage: Stage): Pair<TextureUnitPair, Int> {
    // Copy into a byte buffer so it can be used by GLES2
    val width = texture.width
    val height = texture.height
    val size = texture.rowBytes * texture.height
    val textureBuffer = ByteBuffer.allocate(size)
    texture.copyPixelsToBuffer(textureBuffer)
    textureBuffer.position(0)

    // Setup the GLES2 texture stuff
    val textureUnitPair = pipeline.allocateTextureUnit(thisStage)
    glActiveTexture(textureUnitPair.textureUnit)

    val textureHandle = glGenTexture()

    glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
    glTexImage2D(
        GLES20.GL_TEXTURE_2D,
        0,
        GLES20.GL_RGBA,
        Size(width, height),
        GLES20.GL_UNSIGNED_BYTE,
        textureBuffer
    )
    glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
    glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
    glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
    glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    return Pair(textureUnitPair, textureHandle)
}