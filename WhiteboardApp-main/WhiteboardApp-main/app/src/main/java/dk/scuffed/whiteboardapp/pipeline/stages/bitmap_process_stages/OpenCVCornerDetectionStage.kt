package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint
import org.opencv.core.Scalar
import org.opencv.features2d.FastFeatureDetector
import org.opencv.features2d.Features2d

internal class OpenCVCornerDetectionStage(
    private val bitmapOutputStage: BitmapOutputStage,
    pipeline: IPipeline,
    vararg initialPoints: Vec2Int
) : PointsOutputStage(pipeline, *initialPoints) {

    private val fastFeatureDetector = FastFeatureDetector.create(20)
    private val img = Mat()

    override fun update() {
        Utils.bitmapToMat(bitmapOutputStage.outputBitmap, img)

        val pointsMat = MatOfKeyPoint()
        fastFeatureDetector.detect(img, pointsMat)
        img.release()

        val kvPoints = pointsMat.toArray()

        points.clear()
        for (kv in kvPoints) {
            points.add(Vec2Int(kv.pt.x.toInt(), kv.pt.y.toInt()))
        }

        pointsMat.release()
    }
}