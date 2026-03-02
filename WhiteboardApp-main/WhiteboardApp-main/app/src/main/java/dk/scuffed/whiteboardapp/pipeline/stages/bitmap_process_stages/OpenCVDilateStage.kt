package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc

/**
 * Dilate bitmap using OpenCVs dilate function.
 */
internal class OpenCVDilateStage(
    private val bitmap: BitmapOutputStage,
    private val dilation: Double,
    pipeline: IPipeline
) : BitmapOutputStage(
    pipeline,
    Size(bitmap.outputBitmap.width, bitmap.outputBitmap.height),
    bitmap.outputBitmap.config
) {
    private var img = Mat()

    override fun update() {
        Utils.bitmapToMat(bitmap.outputBitmap, img)

        val dilated = Mat(img.size(), CvType.CV_8UC1)
        val kernelSize = dilation
        val elementType = Imgproc.CV_SHAPE_RECT
        val element = Imgproc.getStructuringElement(
            elementType, org.opencv.core.Size(2 * kernelSize + 1, 2 * kernelSize + 1),
            Point(kernelSize, kernelSize)
        )

        Imgproc.dilate(img, dilated, element)

        Utils.matToBitmap(dilated, outputBitmap)

        img.release()
        dilated.release()
        element.release()
    }

}