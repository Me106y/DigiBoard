package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import dk.scuffed.whiteboardapp.pipeline.readRawResource
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * This class is used to highlight different coordinates on the smartphone screen.
 * @property context the app.
 * @param pipeline the pipeline for analyses.
 * @property pointsStage stage supplying the points used for corners.
 */
internal class DrawCornersStage(
    private val context: Context,
    pipeline: IPipeline,
    private val pointsStage: PointsOutputStage,
    resolution: Size
) : Stage(pipeline) {
    //The radius of the circle
    private val circleRadius: Int = 25

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
            resolution
        )
        setupGlProgram()
    }

    /**
     * This function loads our shaders based from the res/raw
     */
    private fun setupGlProgram() {
        val vertexShaderCode = readRawResource(context, R.raw.vertex_shader)
        val fragmentShaderCode = readRawResource(context, R.raw.corner_shader)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
    }

    override fun update() {
        glBindFramebuffer(frameBufferInfo.fboHandle)
        glViewport(Vec2Int(0, 0), frameBufferInfo.textureSize)
        glClearColorClear()
        glClear(GLES20.GL_COLOR_BUFFER_BIT)
        glClearColorError() // set back to error color for future stages
        for (point in pointsStage.points) {
            drawPoint(point)
        }

    }

    /**
     * This function draws a circle based on a point.
     * @param point is a Vec2 which stores the x and y coordinate.
     */
    private fun drawPoint(point: Vec2Int) {
        val circleRadiusVec = Vec2Int(circleRadius, circleRadius)

        // Calculates the bottom left corner for the viewport
        val bottomLeftCorner = point - circleRadiusVec
        // Calculates the top right corner for the viewport
        val topRightCorner = point + circleRadiusVec

        val size = topRightCorner - bottomLeftCorner

        glViewport(bottomLeftCorner, Size(size.x, size.y))

        // Render to our framebuffer
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
            circleRadius * 2.toFloat(),
            circleRadius * 2.toFloat()
        )
        // To know the viewport's start point is placed
        val offsetHandle = glGetUniformLocation(program, "offset")
        glUniform2f(
            offsetHandle,
            bottomLeftCorner.x.toFloat(),
            bottomLeftCorner.y.toFloat()
        )
        glDrawElements(
            GLES20.GL_TRIANGLES,
            drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )
        glDisableVertexAttribArray(positionHandle)
    }
}
