package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc


/**
 * Blur bitmap using OpenCVs gaussian blur.
 */
internal class OpenCVGaussianBlurStage(
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

        val blurredImg = Mat(img.size(), CvType.CV_8UC1)

        Imgproc.GaussianBlur(img, blurredImg, org.opencv.core.Size(5.0, 5.0), 1.0, 1.0)

        Utils.matToBitmap(blurredImg, outputBitmap)

        img.release()
        blurredImg.release()
    }

}