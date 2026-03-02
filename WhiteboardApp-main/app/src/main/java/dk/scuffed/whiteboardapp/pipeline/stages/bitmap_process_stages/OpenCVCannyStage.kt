package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.graphics.Bitmap
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


/**
 * Runs the OpenCV Canny Edge detection.
 */
internal class OpenCVCannyStage(
    private val bitmapStage: BitmapOutputStage,
    pipeline: IPipeline
) : BitmapOutputStage(
    pipeline,
    Size(bitmapStage.outputBitmap.width, bitmapStage.outputBitmap.height),
    bitmapStage.outputBitmap.config
) {
    private var img = Mat()

    override fun update() {
        Utils.bitmapToMat(bitmapStage.outputBitmap, img)

        val edges = Mat(img.size(), CvType.CV_8UC1)

        Imgproc.Canny(img, edges, 80.0, 130.0)

        Utils.matToBitmap(edges, outputBitmap)

        img.release()
        edges.release()
    }

}