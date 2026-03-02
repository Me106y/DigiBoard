package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.graphics.Bitmap
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/**
 * Runs OpenCVs sobel edge detection on bitmap.
 */
internal class OpenCVSobelStage(
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

        val sobelEdgesX = Mat(img.size(), CvType.CV_8UC1)
        val sobelEdgesY = Mat(img.size(), CvType.CV_8UC1)
        val edges = Mat(img.size(), CvType.CV_8UC1)

        Imgproc.Sobel(img, sobelEdgesX, CvType.CV_8UC1, 1, 0, 3)
        Imgproc.Sobel(img, sobelEdgesY, CvType.CV_8UC1, 0, 1, 3)

        Core.addWeighted(sobelEdgesX, 0.5, sobelEdgesY, 0.5, 0.0, edges)

        Utils.matToBitmap(edges, outputBitmap)

        img.release()
        sobelEdgesX.release()
        sobelEdgesY.release()
        edges.release()
    }

}