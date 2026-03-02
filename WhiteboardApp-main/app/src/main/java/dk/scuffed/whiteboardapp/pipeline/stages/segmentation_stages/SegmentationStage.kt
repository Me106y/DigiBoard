package dk.scuffed.whiteboardapp.pipeline.stages.segmentation_stages

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import dk.scuffed.whiteboardapp.segmentation.PPSegmentation

/**
 * Outputs a mask that covers all humans.
 */
internal class SegmentationStage(
    context: Context,
    segmentationModel: PPSegmentation.Model,
    private val inputBitmap: Bitmap,
    pipeline: IPipeline
) : BitmapOutputStage(pipeline, Size(inputBitmap.width, inputBitmap.height), inputBitmap.config) {

    private val segmentor = PPSegmentation(context, segmentationModel)

    override fun update() {
        outputBitmap = segmentor.segment(inputBitmap)
    }
}