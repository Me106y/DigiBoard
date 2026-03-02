package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.CannyStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GrayscaleStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.SobelStage

/**
 * Runs canny edge detection entirely, including grayscaling, blur, sobel and the canny edge detection itself.
 */
internal fun fullCannyEdgeDetection(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    val grayscale = GrayscaleStage(
        context,
        inputStage.frameBufferInfo,
        pipeline,
    )

    val gaussian = fullGaussian(
        context,
        grayscale,
        pipeline,
    )

    val sobelStage = SobelStage(
        context,
        gaussian.frameBufferInfo,
        pipeline
    )

    val cannyStage = CannyStage(
        context,
        sobelStage.frameBufferInfo,
        0.1f,
        0.2f,
        pipeline
    )

    return cannyStage
}
