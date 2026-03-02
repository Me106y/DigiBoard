package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import android.util.Log
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.LinesOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.QuadFloat
import dk.scuffed.whiteboardapp.utils.Vec2Float
import dk.scuffed.whiteboardapp.utils.Vec2Int
import java.util.*
import kotlin.math.acos

internal class BiggestQuadStage(
    private val horizontalLinesStage: LinesOutputStage,
    private val verticalLinesStage: LinesOutputStage,
    pipeline: IPipeline
) :
    PointsOutputStage(
        pipeline,
        Vec2Int(0, pipeline.getInitialResolution().height),
        Vec2Int(0, 0),
        Vec2Int(pipeline.getInitialResolution().width, 0),
        Vec2Int(pipeline.getInitialResolution().width, pipeline.getInitialResolution().height))
{

    override fun update() {
        var biggestQuad = QuadFloat()
        var biggestQuadArea = 0f

        for (x1 in 0 until horizontalLinesStage.lines.size) {
            for (x2 in x1 + 1 until horizontalLinesStage.lines.size) {
                for (y1 in 0 until verticalLinesStage.lines.size) {
                    for (y2 in y1 + 1 until verticalLinesStage.lines.size) {
                        val lx1 = horizontalLinesStage.lines[x1]
                        val lx2 = horizontalLinesStage.lines[x2]
                        val ly1 = verticalLinesStage.lines[y1]
                        val ly2 = verticalLinesStage.lines[y2]

                        val p1 = lx1.intersect(ly1)
                        val p2 = lx1.intersect(ly2)
                        val p3 = lx2.intersect(ly1)
                        val p4 = lx2.intersect(ly2)

                        if (p1 != null && p2 != null && p3 != null && p4 != null) {
                            val quad = makeQuadConvex(QuadFloat(p1, p2, p3, p4))

                            val area = quad.area()
                            if (area > biggestQuadArea) {
                                biggestQuad = quad
                                biggestQuadArea = area
                            }
                        }
                    }
                }
            }
        }

        // Use the last found quad if we don't detect any
        if (biggestQuadArea != 0f) {
            val fixedQuad = fixupQuad(biggestQuad)

            // LIMITATION: If the biggest quad is not perspective correctable we don't go to the
            // biggest perspective correctable quad.
            // To fix this we would have to run fixupQuad and isPerspectiveCorrectable on all found
            // quads
            if (isQuadReasonable(biggestQuad)) {
                points[0] = fixedQuad.a.toVec2Int()
                points[1] = fixedQuad.b.toVec2Int()
                points[2] = fixedQuad.c.toVec2Int()
                points[3] = fixedQuad.d.toVec2Int()
            }
            else {
                Log.d("BiggestSquare", "Quad is not perspective correctable")
            }
        }
    }

    // Expects a quad from fixupQuad
    private fun isQuadReasonable(quad: QuadFloat): Boolean {
        val maxAngle = (130.0 * Math.PI) / 180.0
        val minAngle = (60.0 * Math.PI) / 180.0

        val checkAngle = {centerPoint: Vec2Float, beforePoint: Vec2Float, afterPoint: Vec2Float ->
            val angle = angleBetweenThreePoints(centerPoint, beforePoint, afterPoint)
            angle in minAngle..maxAngle
        }

        // Check top left corner angle
        if (!checkAngle(quad.a, quad.d, quad.b)) {
            return false
        }

        // Check bottom left corner angle
        if (!checkAngle(quad.b, quad.a, quad.c)) {
            return false
        }

        // Check bottom right corner angle
        if (!checkAngle(quad.c, quad.b, quad.d)) {
            return false
        }

        // Check top right corner angle
        if (!checkAngle(quad.d, quad.c, quad.a)) {
            return false
        }

        return true
    }

    private fun angleBetweenThreePoints(centerPoint: Vec2Float, beforePoint: Vec2Float, afterPoint: Vec2Float): Float {
        val a = centerPoint - beforePoint
        val b = centerPoint - afterPoint

        val dot = a.dot(b)
        val mag = a.length() * b.length()
        val angle = acos(dot/mag)

        return angle
    }

    // The point a should be the top left corner
    // The quad should be counter-clockwise
    private fun fixupQuad(quad: QuadFloat): QuadFloat {
        val points = ArrayList(listOf(*quad.toVec2FloatArray()))

        val bottomLeftPoint = points.reduce {acc, point -> if (point.x + point.y < acc.x + acc.y) { point } else { acc } }
        points.remove(bottomLeftPoint)

        val bottomRightPoint = points.reduce {acc, point -> if ((-point.x) + point.y < (-acc.x) + acc.y) { point } else { acc } }
        points.remove(bottomRightPoint)

        val topRightPoint = points.reduce {acc, point -> if (point.x + point.y > acc.x + acc.y) { point } else { acc } }
        points.remove(topRightPoint)

        val topLeftPoint = points[0]

        return QuadFloat(topLeftPoint, bottomLeftPoint, bottomRightPoint, topRightPoint)
    }

    private fun makeQuadConvex(quad: QuadFloat): QuadFloat {
        val points = ArrayList(listOf(*quad.toVec2FloatArray()))

        val a = points[0]
        points.remove(a)

        val b = shortestDistance(a, points)
        points.remove(b)

        val c = shortestDistance(b, points)
        points.remove(c)

        val d = points[0]

        return QuadFloat(a, b, c, d)
    }

    private fun shortestDistance(point: Vec2Float, points: ArrayList<Vec2Float>): Vec2Float {
        var shortestDistance = Float.MAX_VALUE
        var shortestPoint = Vec2Float(0f, 0f)

        for (other in points) {
            val distance = point.distance(other)
            if (distance < shortestDistance) {
                shortestDistance = distance
                shortestPoint = other
            }
        }

        return shortestPoint
    }
}