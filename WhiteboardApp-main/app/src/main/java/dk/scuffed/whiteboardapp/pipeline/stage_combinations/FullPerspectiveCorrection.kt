package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.PerspectiveCorrectionStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.PerspectiveTransformPointsStage

/**
 * Runs the entire perspective correction based on two sets of points and an input framebuffer
 */
internal fun fullPerspectiveCorrection(
    context: Context,
    inputStage: GLOutputStage,
    pointsFrom: PointsOutputStage,
    pointsTo: PointsOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    val perspectiveTransformPointsStage = PerspectiveTransformPointsStage(
        pipeline,
        pointsFrom,
        pointsTo
    )

    val perspectiveCorrectionStage = PerspectiveCorrectionStage(
        context,
        inputStage.frameBufferInfo,
        perspectiveTransformPointsStage,
        pipeline
    )

    return perspectiveCorrectionStage
}