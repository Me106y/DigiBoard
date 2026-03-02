package dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.PerspectiveTransformPointsStage
import dk.scuffed.whiteboardapp.utils.*

/**
 * Distorts the input image by rendering with the inputVertices so that it appears as if it has been distorted in 3D
 * @param inputFramebufferInfo is reference to the framebuffer that will be distorted
 * @param inputVertices are the vertices the quad is distorted to
 */
internal class PerspectiveCorrectionStage(
    context: Context,
    private val inputFramebufferInfo: FramebufferInfo,
    private val inputVertices: PerspectiveTransformPointsStage,
    pipeline: IPipeline
) : GLOutputStage(
    context,
    R.raw.vertex_perspective_correction_shader,
    R.raw.texture_distortable, pipeline
) {
    init {
        setup()
    }

    override fun setupFramebufferInfo() {
        allocateFramebuffer(GLES20.GL_RGBA, inputFramebufferInfo.textureSize)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        // Input framebuffer
        val framebufferTextureHandle = glGetUniformLocation(program, "source_texture")
        glUniform1i(framebufferTextureHandle, inputFramebufferInfo.textureUnitPair.textureUnitIndex)
        glActiveTexture(inputFramebufferInfo.textureUnitPair.textureUnit)
        glBindTexture(GLES20.GL_TEXTURE_2D, inputFramebufferInfo.textureHandle)

        // Converts the input vertices from screenspace in resolution to -1 to 1
        val convertedPoints = arrayListOf(
            convertToVertexSpace(inputVertices.points[0]),
            convertToVertexSpace(inputVertices.points[1]),
            convertToVertexSpace(inputVertices.points[2]),
            convertToVertexSpace(inputVertices.points[3])
        )

        // Calculates the projection
        val projected = calculateProjectedTexcoords(convertedPoints)

        // Updates the vertices and texture coordinates of the GLOutputStage to achieve the distortion
        reassignVertices(convertedPoints)
        if (projected != null) {
            reassignTexCoord(projected)
        }
    }

    override fun viewport() {
        // Calculate the bottom left corner so that the view is centralized.
        val offsetX = (inputFramebufferInfo.textureSize.width - inputVertices.scaledResolution.width) / 2
        val offsetY = (inputFramebufferInfo.textureSize.height - inputVertices.scaledResolution.height) / 2
        glViewport(Vec2Int(offsetX, offsetY), Size(inputVertices.scaledResolution.width, inputVertices.scaledResolution.height))
    }

    override fun clear() {
        glClearColor(Color(0f, 0f, 0f, 0f))
        glClear(GLES20.GL_COLOR_BUFFER_BIT)
        glClearColorError()
    }

    // Uses projective transformation to get the correctly distorted texture coordinates for the quad
    // See https://www.reedbeta.com/blog/quadrilateral-interpolation-part-1/ for indepth explanation
    private fun calculateProjectedTexcoords(convertedPoints: ArrayList<Vec2Float>): ArrayList<Vec3Float>? {

        val intersection = LineFloat(
            convertedPoints[0],
            convertedPoints[2]
        ).intersect(LineFloat(convertedPoints[1], convertedPoints[3]))
            ?: return null

        val distances = arrayListOf(
            convertedPoints[0].distance(intersection),
            convertedPoints[1].distance(intersection),
            convertedPoints[2].distance(intersection),
            convertedPoints[3].distance(intersection),
        )

        val zValues = arrayListOf(
            calculateZValue(distances[0], distances[2]),
            calculateZValue(distances[1], distances[3]),
            calculateZValue(distances[2], distances[0]),
            calculateZValue(distances[3], distances[1]),
        )

        val texCoords = arrayListOf(
            Vec3Float(0f, 1f, 1f) * zValues[0],
            Vec3Float(0f, 0f, 1f) * zValues[1],
            Vec3Float(1f, 0f, 1f) * zValues[2],
            Vec3Float(1f, 1f, 1f) * zValues[3],
        )

        return texCoords
    }

    // Calculate some kind of pseudo depth used for the projection
    // See details at https://www.reedbeta.com/blog/quadrilateral-interpolation-part-1/
    private fun calculateZValue(d: Float, dOpp: Float): Float {
        return (d + dOpp) / dOpp
    }

    // Converts the input vertices from screenspace (resolution) to vertex space (-1 to 1)
    private fun convertToVertexSpace(v: Vec2Int): Vec2Float {
        return (Vec2Float(
            ((v.x / inputFramebufferInfo.textureSize.width.toFloat()) - 0.5f) * 2f,
            ((v.y / inputFramebufferInfo.textureSize.height.toFloat()) - 0.5f) * 2f
        ))
    }
}