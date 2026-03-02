package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import android.util.Size
import com.google.common.primitives.Floats
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.QuadFloat
import dk.scuffed.whiteboardapp.utils.Vec2Float
import dk.scuffed.whiteboardapp.utils.Vec2Int
import org.opencv.core.Core
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc


/**
 * Stage that outputs the points required so that pointsFrom will be at pointsTo.
 * Requires everything to have 4 points, as this is designed for quads.
 * @param pointsFrom the points forming a quad that we want to be moved to pointsTo.
 * @param pointsTo the points forming a quad that we want pointsFrom to be moved to.
 * @returns the points of the quad that will ensure pointsFrom is distorted so they are at pointsTo.
 */
internal class PerspectiveTransformPointsStage(
    pipeline: IPipeline,
    private val pointsFrom: PointsOutputStage,
    private val pointsTo: PointsOutputStage
) : PointsOutputStage(
    pipeline,
    pointsFrom.points[0],
    pointsFrom.points[1],
    pointsFrom.points[2],
    pointsFrom.points[3],
) {
    var scaledResolution = getResolution()

    override fun update() {


        assert(pointsFrom.points.size == 4)
        assert(pointsTo.points.size == 4)

        val boundingBox = boundingBox(
            QuadFloat(
                pointsFrom.points[0].toVec2Float(),
                pointsFrom.points[1].toVec2Float(),
                pointsFrom.points[2].toVec2Float(),
                pointsFrom.points[3].toVec2Float()
            )
        )
        val width = (boundingBox.b.x - boundingBox.a.x).toInt()
        val height = (boundingBox.d.y - boundingBox.a.y).toInt()
        scaledResolution =
            scaleResolution(Size(width, height), Size(pointsTo.points[3].x, pointsTo.points[3].y))

        val src = MatOfPoint2f(
            Point(pointsFrom.points[0].x.toDouble(), pointsFrom.points[0].y.toDouble()),
            Point(pointsFrom.points[1].x.toDouble(), pointsFrom.points[1].y.toDouble()),
            Point(pointsFrom.points[2].x.toDouble(), pointsFrom.points[2].y.toDouble()),
            Point(pointsFrom.points[3].x.toDouble(), pointsFrom.points[3].y.toDouble()),
        )

        val dst = MatOfPoint2f(
            Point(pointsTo.points[0].x.toDouble(), pointsTo.points[0].y.toDouble()),
            Point(pointsTo.points[1].x.toDouble(), pointsTo.points[1].y.toDouble()),
            Point(pointsTo.points[2].x.toDouble(), pointsTo.points[2].y.toDouble()),
            Point(pointsTo.points[3].x.toDouble(), pointsTo.points[3].y.toDouble()),
        )

        val perspectiveTransformMatrix = Imgproc.getPerspectiveTransform(src, dst)

        val result = MatOfPoint2f()

        Core.perspectiveTransform(dst, result, perspectiveTransformMatrix)

        val newPoints = result.toArray()

        for (i in points.indices) {
            points[i] = Vec2Int(newPoints[i].x.toInt(), newPoints[i].y.toInt())
        }

        perspectiveTransformMatrix.release()
        result.release()
        src.release()
        dst.release()
    }

    private fun boundingBox(quad: QuadFloat): QuadFloat {
        val xMin = Floats.min(quad.a.x, quad.b.x, quad.c.x, quad.d.x)
        val xMax = Floats.max(quad.a.x, quad.b.x, quad.c.x, quad.d.x)
        val yMin = Floats.min(quad.a.y, quad.b.y, quad.c.y, quad.d.y)
        val yMax = Floats.max(quad.a.y, quad.b.y, quad.c.y, quad.d.y)

        val bottomLeft = Vec2Float(xMin, yMin)
        val bottomRight = Vec2Float(xMax, yMin)
        val topRight = Vec2Float(xMax, yMax)
        val topLeft = Vec2Float(xMin, yMax)


        return QuadFloat(bottomLeft, bottomRight, topRight, topLeft)
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
}