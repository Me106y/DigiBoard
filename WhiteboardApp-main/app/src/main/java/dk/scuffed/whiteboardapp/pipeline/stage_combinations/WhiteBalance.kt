package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.*

/**
 * Applies the fast white balancing, by downscaling the image and using it as the background color.
 */
internal fun whiteBalance(
    context: Context,
    input: GLOutputStage,
    downscaledInput: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {

    return WhitebalanceStage(
        context,
        input.frameBufferInfo,
        downscaledInput.frameBufferInfo,
        pipeline
    )
}