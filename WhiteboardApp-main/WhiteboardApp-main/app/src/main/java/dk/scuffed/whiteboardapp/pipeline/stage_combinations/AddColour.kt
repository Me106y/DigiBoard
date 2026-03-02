package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GaussianBlurStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.HSVAdjustmentsStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.MaskingStage

/**
 * Adds colour from camera back in the binarization
 */
internal fun addColour(
    context: Context,
    colours: GLOutputStage,
    mask: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    val hsvAdjustmentsStage = HSVAdjustmentsStage(context, colours.frameBufferInfo, pipeline)

    val colourApply = MaskingStage(
        context,
        hsvAdjustmentsStage.frameBufferInfo,
        mask.frameBufferInfo,
        mask.frameBufferInfo,
        pipeline
    )

    return colourApply
}