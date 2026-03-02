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
import dk.scuffed.whiteboardapp.pipeline.stages.LinesOutputStage
import dk.scuffed.whiteboardapp.utils.*
import dk.scuffed.whiteboardapp.utils.Color
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.*

internal class DrawLinesStage(
    private val context: Context,
    private val linesOutputStage: LinesOutputStage,
    private val color: Color,
    resolution: Size,
    pipeline: IPipeline,
) : Stage(pipeline) {
    private var program: Int = 999

    //Half the line width
    private val lineWidth: Float = 5.0f

    //Number of vertexes
    private val cordsPerVertex = 3

    // 4 bytes per vertex
    private val vertexStride: Int = cordsPerVertex * 4


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
        val fragmentShaderCode = readRawResource(context, R.raw.line_shader)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
    }

    override fun update() {
        drawLines()
    }

    /**
     * Returns the angle between two vectors based on the x axis.
     */
    private fun angleBetweenVectors(vec1: Vec2Float, vec2: Vec2Float): Float {
        val vec = vec2 - vec1
        return (atan2(vec.y, vec.x) + (PI / 2.0f)).toFloat()
    }

    /**
     * Returns the unit vector for vec2-vec1
     */
    private fun getUnitVec(vec1: Vec2Float, vec2: Vec2Float): Vec2Float {
        val angle = angleBetweenVectors(vec1, vec2)
        return Vec2Float(cos(angle), sin(angle))
    }

    /**
     * Expand a line into a quad using lineWidth
     */
    private fun lineToQuad(line: LineFloat): QuadFloat {
        //The scaled unit vector
        val scaledUnitVector = getUnitVec(line.startPoint, line.endPoint) * lineWidth

        //The vector between the two points
        val vecBetween = line.endPoint - line.startPoint

        // Top- and bottom left corners for the square
        val topLeftCorner = scaledUnitVector + line.startPoint
        val bottomLeftCorner = line.startPoint - scaledUnitVector
        // Top- and bottom right corners for the square
        val topRightCorner = topLeftCorner + vecBetween
        val bottomRightCorner = bottomLeftCorner + vecBetween

        return QuadFloat(
            map(topLeftCorner, getResolution()),
            map(bottomLeftCorner, getResolution()),
            map(bottomRightCorner, getResolution()),
            map(topRightCorner, getResolution())
        )
    }

    private fun map(point: Vec2Float, resolution: Size): Vec2Float {
        return Vec2Float(map(point.x, resolution.width), map(point.y, resolution.height))
    }

    /**
     * This function maps the coordinates of the vector from the range 0-
     */
    private fun map(value: Float, res: Int): Float {
        val min1 = 0.0f
        val min2 = -1.0f
        val max2 = 1.0f
        return min2 + (value - min1) * (max2 - min2) / (res - min1)
    }

    /**
     * Draws the line between the coordinates
     */
    private fun drawLines() {
        // order to draw vertices
        val drawOrder = createDrawOrder(linesOutputStage.lines.size)

        //Sets the coordinates for the squares
        val squareCoords = createVertices()

        // initialize byte buffer for the draw list
        val drawListBuffer: ShortBuffer =
            // (# of coordinate values * 2 bytes per short)
            ByteBuffer.allocateDirect(drawOrder.size * 2).run {
                order(ByteOrder.nativeOrder())
                asShortBuffer().apply {
                    put(drawOrder)
                    position(0)
                }
            }

        val vertexBuffer: FloatBuffer =
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer.allocateDirect(squareCoords.size * 4).run {
                order(ByteOrder.nativeOrder())
                asFloatBuffer().apply {
                    put(squareCoords)
                    position(0)
                }
            }

        glViewport(
            Vec2Int(0, 0),
            getResolution()
        )

        // Render to our framebuffer
        glBindFramebuffer(frameBufferInfo.fboHandle)
        glClearColorClear()
        glClear(GLES20.GL_COLOR_BUFFER_BIT)
        glClearColorError() // set back to error color for future stages

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

        val colorHandle = glGetUniformLocation(program, "color")
        glUniform4f(colorHandle, color.r, color.g, color.b, color.a)

        glDrawElements(
            GLES20.GL_TRIANGLES,
            drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )
        glDisableVertexAttribArray(positionHandle)
    }

    /**
     * Returns the draw order for the squares in a ShortArray
     * @param squares The numbers of squares
     */
    private fun createDrawOrder(squares: Int): ShortArray {
        val drawOrder = ShortArray(squares * 6)
        //The specific draw order is set in the for loop
        for (i: Int in 0 until squares) {
            drawOrder[i * 6] = (i * 4).toShort()
            drawOrder[i * 6 + 1] = (i * 4 + 1).toShort()
            drawOrder[i * 6 + 2] = (i * 4 + 2).toShort()
            drawOrder[i * 6 + 3] = (i * 4).toShort()
            drawOrder[i * 6 + 4] = (i * 4 + 2).toShort()
            drawOrder[i * 6 + 5] = (i * 4 + 3).toShort()
        }
        return drawOrder
    }

    private fun createVertices(): FloatArray {
        val vertices = ArrayList<Float>()

        for (line in linesOutputStage.lines) {
            val quad = lineToQuad(line)
            val pointArray = quad.toVec2FloatArray()
            for (point in pointArray) {
                vertices.add(point.x)
                vertices.add(point.y)
                vertices.add(0f)
            }
        }

        return vertices.toFloatArray()
    }
}