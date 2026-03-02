package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.readRawResource
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import dk.scuffed.whiteboardapp.utils.Color
import dk.scuffed.whiteboardapp.utils.Vec2Int
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
/**
 * This stage is used to scale the end result to the screens resolution while preserving the correct aspect ratio.
 */
internal class LetterboxingStage(
    private val context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    pipeline: IPipeline
) : Stage(pipeline) {
    private var program: Int = 999

    private val cordsPerVertex = 3

    //XYZ Coordinates for the square we are drawing on.
    private val squareCoords = floatArrayOf(
        -1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f
    )

    // order to draw vertices
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    // 4 bytes per vertex
    private val vertexStride: Int = cordsPerVertex * 4

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    val frameBufferInfo: FramebufferInfo

    init {
        frameBufferInfo = pipeline.allocateFramebuffer(
            this,
            GLES20.GL_RGBA,
            getResolution()
        )

        setupGlProgram()
    }

    private fun scaleResolution(inputResolution: Size, targetResolution: Size): Size {
        val widthFactor = targetResolution.width.toDouble() / inputResolution.width.toDouble()
        val heightFactor = targetResolution.height.toDouble() / inputResolution.height.toDouble()
        if (widthFactor < heightFactor) {
            // Width is the limiting factor
            val width = inputResolution.width.toDouble() * widthFactor
            val height = inputResolution.height.toDouble() * widthFactor

            return Size(width.toInt(), height.toInt())
        } else {
            // Height is the limiting factor
            val width = inputResolution.width.toDouble() * heightFactor
            val height = inputResolution.height.toDouble() * heightFactor

            return Size(width.toInt(), height.toInt())
        }
    }

    private fun setupGlProgram() {
        val vertexShaderCode = readRawResource(context, R.raw.vertex_shader)
        val fragmentShaderCode = readRawResource(context, R.raw.letterboxing_shader)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
    }

    override fun update() {
        glBindFramebuffer(frameBufferInfo.fboHandle)

        val resolution = Vec2Int(getResolution().width, getResolution().height)
        val inputResolutionSize = scaleResolution(inputFramebufferInfo.textureSize, getResolution())
        val inputResolution = Vec2Int(inputResolutionSize.width, inputResolutionSize.height)
        val start = (resolution / 2) - (inputResolution / 2)
        glClearColor(Color(0f, 0f, 0f, 1f))
        glViewport(Vec2Int(0, 0), getResolution())
        glClear(GLES20.GL_COLOR_BUFFER_BIT)
        glClearColorError()
        glViewport(start, inputResolutionSize)

        glBindFramebuffer(frameBufferInfo.fboHandle)

        glUseProgram(program)

        val positionHandle = glGetAttribLocation(program, "position")
        glEnableVertexAttribArray(positionHandle)
        glVertexAttribPointer(
            positionHandle,
            cordsPerVertex,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        // Always provide resolution
        val resolutionHandle = glGetUniformLocation(program, "resolution")
        glUniform2f(
            resolutionHandle,
            inputResolution.x.toFloat(),
            inputResolution.y.toFloat()
        )

        // To know the viewport's start point is placed
        val offsetHandle = glGetUniformLocation(program, "offset")
        glUniform2f(
            offsetHandle,
            start.x.toFloat(),
            start.y.toFloat()
        )

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "framebuffer")
        glUniform1i(framebufferTextureHandle, inputFramebufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFramebufferInfo.textureHandle)


        glDrawElements(
            GLES20.GL_TRIANGLES,
            drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )
        glDisableVertexAttribArray(positionHandle)
    }
}