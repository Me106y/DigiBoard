package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.LinesOutputStage
import dk.scuffed.whiteboardapp.utils.LineFloat
import dk.scuffed.whiteboardapp.utils.Vec2Float
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Finds distinct lines in the input image.
 * @param bitmapOutputStage is the input framebuffer lines will be found in
 * @param threshold is the amount of votes a line needs in order to be included
 * @param maxLines limits the amount of distinct lines that are at most output.
 * @param rhoResolution is the resolution of the distances of lines in the Hough Line Transform
 * @param thetaResolution is the resolution of the angles of the lines in the Hough Line Transform
 * @param rhoResolution is the distance where two lines are seen as not distinct
 * @param thetaResolution is the angle where two lines are seen as not distinct
 */
internal class OpenCVLineDetectionStage(
    private val bitmapOutputStage: BitmapOutputStage,
    private val threshold: Int,
    private val maxLines: Int,
    pipeline: IPipeline,
    private val rhoResolution: Double = 1.0,
    private val thetaResolution: Double = Math.PI / 180.0,
    private val rhoThreshold: Double = 0.0,
    private val thetaThreshold: Double = 0.0
) : LinesOutputStage(pipeline) {

    private val inputMat = Mat()
    override fun update() {
        Utils.bitmapToMat(bitmapOutputStage.outputBitmap, inputMat)

        val grayMat = Mat(inputMat.size(), CvType.CV_8UC1)
        Imgproc.cvtColor(inputMat, grayMat, Imgproc.COLOR_RGBA2GRAY)
        inputMat.release()

        val linesMat = Mat(grayMat.size(), CvType.CV_8UC1)
        Imgproc.HoughLines(grayMat, linesMat, rhoResolution, thetaResolution, threshold)

        val distinctLinesData = findDistinctLines(linesMat)

        lines.clear()
        for (l in distinctLinesData) {
            val rho = l.first
            val theta = l.second
            val directionX = cos(theta)
            val directionY = sin(theta)
            val x0 = directionX * rho
            val y0 = directionY * rho
            val pt1 = Vec2Float((x0 + 2000.0f * -directionY).toFloat(), (y0 + 2000.0f * directionX).toFloat())
            val pt2 = Vec2Float((x0 - 2000.0f * -directionY).toFloat(), (y0 - 2000.0f * directionX).toFloat())

            lines.add(LineFloat(pt1, pt2))
        }

        linesMat.release()
        grayMat.release()
    }

    private fun findDistinctLines(linesMat: Mat): ArrayList<Pair<Double, Double>> {

        val distinctLines: ArrayList<Pair<Double, Double>> = arrayListOf()

        for (x in 0 until linesMat.rows()) {
            val rho = linesMat.get(x, 0)[0]
            val theta = linesMat.get(x, 0)[1]

            val line = Pair(rho, theta)

            if (isDistinct(line, distinctLines)) {
                distinctLines.add(line)
                if (distinctLines.size >= maxLines){
                    return distinctLines
                }
            }
        }
        return distinctLines

    }

    private fun isDistinct(line: Pair<Double, Double>, lines: ArrayList<Pair<Double, Double>>): Boolean {
        for (l in lines) {
            if (abs(l.first - line.first) <= rhoThreshold && abs(l.second - line.second) <= thetaThreshold) {
                return false
            }
        }
        return true
    }
}
