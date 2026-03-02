package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

// Does not need grayscale as input.
internal class OpenCVBinarization(
    private val bitmapStage: BitmapOutputStage,
    private val maxValue: Double,
    private val blockSize: Int, // The size of the pixel neighbourhood usedd for the thresholding. Has to be an odd number
    private val c: Double, // Constant weight that gets subtracted from the mean
    pipeline: IPipeline
) : BitmapOutputStage(
    pipeline,
    Size(bitmapStage.outputBitmap.width, bitmapStage.outputBitmap.height),
    bitmapStage.outputBitmap.config
) {
    private val img =
        Mat(bitmapStage.outputBitmap.width, bitmapStage.outputBitmap.height, CvType.CV_8UC1)

    override fun update() {
        Utils.bitmapToMat(bitmapStage.outputBitmap, img)

        val grayscale = Mat(img.size(), CvType.CV_8UC1)

        Imgproc.cvtColor(img, grayscale, Imgproc.COLOR_RGB2GRAY, 4)

        val binarized = Mat(img.size(), CvType.CV_8UC1)

        Imgproc.adaptiveThreshold(
            grayscale,
            binarized,
            maxValue,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            blockSize,
            c
        )

        Utils.matToBitmap(binarized, outputBitmap)

        img.release()
        grayscale.release()
        binarized.release()
    }
}