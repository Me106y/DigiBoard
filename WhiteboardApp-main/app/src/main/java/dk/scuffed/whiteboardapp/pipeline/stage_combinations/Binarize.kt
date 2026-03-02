package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.icu.number.Scale
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.*
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.BinarizationStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GrayscaleStage

/**
 * Binarizes a coloured image using adaptive thresholding.
 */
internal fun binarize(
    context: Context,
    input: GLOutputStage,
    downscaledInput: GLOutputStage,
    threshold: Float,
    pipeline: IPipeline,
): GLOutputStage {
    val grayscaleStage = GrayscaleStage(
        context,
        input.frameBufferInfo,
        pipeline
    )
    val grayscaleDownscaledStage = GrayscaleStage(
        context,
        downscaledInput.frameBufferInfo,
        pipeline
    )

    val binarizeFast = BinarizationFastStage(context, grayscaleStage.frameBufferInfo, grayscaleDownscaledStage.frameBufferInfo, threshold, pipeline)

    return binarizeFast
}