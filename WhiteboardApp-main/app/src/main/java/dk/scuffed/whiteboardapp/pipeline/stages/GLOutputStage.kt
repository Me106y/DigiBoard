package dk.scuffed.whiteboardapp.pipeline.stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.readRawResource
import dk.scuffed.whiteboardapp.utils.Vec2Float
import dk.scuffed.whiteboardapp.utils.Vec2Int
import dk.scuffed.whiteboardapp.utils.Vec3Float
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * Baseclass for stages that output to an OpenGL framebuffer.
 * @property frameBufferInfo holds information of the framebuffer that is written into.
 */
internal abstract class GLOutputStage(
    private val context: Context,
    private val vertexShaderResource: Int,
    private val fragmentShaderResource: Int,
    private val pipeline: IPipeline
) : Stage(pipeline) {
    private var program: Int = 999

    private val coordsPerVertex = 3
    private val texCoordsPerVertex = 3

    //XYZ Coordinates for the square we are drawing on.
    private val squareCoords = floatArrayOf(
        -1.0f, 1.0f, 0.0f,
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f, 1.0f, 0.0f
    )

    // order to draw vertices
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3)

    private val textureCoords = floatArrayOf(
        0f, 1f, 0f,
        0f, 0f, 0f,
        1f, 0f, 0f,
        1f, 1f, 0f,
        0f, 1f, 0f,
        1f, 0f, 0f,
    )

    // 4 bytes per vertex
    private val vertexStride: Int = coordsPerVertex * 4

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

    private val vertexTexCoordBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
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

    lateinit var frameBufferInfo: FramebufferInfo

    protected fun setup() {
        setupGlProgram()
        setupFramebufferInfo()
    }

    protected fun setupGlProgram() {
        val vertexShaderCode = readRawResource(context, vertexShaderResource)
        val fragmentShaderCode = readRawResource(context, fragmentShaderResource)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
    }

    protected abstract fun setupFramebufferInfo()

    protected fun allocateFramebuffer(textureFormat: Int, resolution: Size) {
        frameBufferInfo =
            pipeline.allocateFramebuffer(this, textureFormat, resolution)
    }

    protected open fun setupUniforms(program: Int) {

    }

    protected open fun clear() {
        glClear(0)
    }

    protected open fun viewport() {
        glViewport(Vec2Int(0, 0), frameBufferInfo.textureSize)
    }

    protected fun reassignVertices(vertices: ArrayList<Vec2Float>) {
        vertexBuffer.position(0)
        for (v in vertices) {
            vertexBuffer.put(v.x)
            vertexBuffer.put(v.y)
            vertexBuffer.put(0f)
        }
        vertexBuffer.position(0)

    }

    protected fun reassignTexCoord(vertices: ArrayList<Vec3Float>) {
        vertexTexCoordBuffer.position(0)
        for (v in vertices) {
            vertexTexCoordBuffer.put(v.x)
            vertexTexCoordBuffer.put(v.y)
            vertexTexCoordBuffer.put(v.z)
        }
        vertexTexCoordBuffer.position(0)

    }

    final override fun update() {
        val size = frameBufferInfo.textureSize
        viewport()

        // Render to our framebuffer
        glBindFramebuffer(frameBufferInfo.fboHandle)
        clear()
        glUseProgram(program)

        val positionHandle = glGetAttribLocation(program, "position")
        glEnableVertexAttribArray(positionHandle)
        glVertexAttribPointer(
            positionHandle,
            coordsPerVertex,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        val textureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate")
        // Only set if it exists in the shader - only few of our shaders actually have texture coordinates
        if (textureCoordinateHandle != -1) {
            glEnableVertexAttribArray(textureCoordinateHandle)
            glVertexAttribPointer(
                textureCoordinateHandle,
                texCoordsPerVertex,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexTexCoordBuffer
            )
        }

        // Always provide resolution
        val resolutionHandle = glGetUniformLocation(program, "resolution")
        glUniform2f(resolutionHandle, size.width.toFloat(), size.height.toFloat())

        // Set up user uniforms
        setupUniforms(program)

        glDrawElements(
            GLES20.GL_TRIANGLES,
            drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT,
            drawListBuffer
        )
        glDisableVertexAttribArray(positionHandle)
    }
}
