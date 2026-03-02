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
 * Greyscale bitmap using OpenCVs RGB2GRAY conversion.
 */
internal class OpenCVGrayScaleStage(
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

        val grayscale = Mat(img.size(), CvType.CV_8UC1)

        Imgproc.cvtColor(img, grayscale, Imgproc.COLOR_RGB2GRAY, 4)

        Utils.matToBitmap(grayscale, outputBitmap)

        img.release()
        grayscale.release()
    }
}