package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GaussianBlurStage

/**
 * Runs an efficient 5x5 gaussian blur
 */
internal fun fullGaussian(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    val gaussianx = GaussianBlurStage(
        context,
        inputStage.frameBufferInfo,
        true,
        pipeline,
    )

    val gaussiany = GaussianBlurStage(
        context,
        gaussianx.frameBufferInfo,
        false,
        pipeline,
    )

    return gaussiany
}