package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.OverlayStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.DraggablePointsStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.DrawCornersStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.ScreenCornerPointsStage

/**
 * Runs the entire perspective correction based on draggable points and screen points and an input framebuffer
 */
internal fun perspectiveCorrectionTestPipeline(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    val draggablePoints = DraggablePointsStage(
        pipeline
    )

    val screenPoints = ScreenCornerPointsStage(
        pipeline
    )


    val perspectiveTransformPointsStage = fullPerspectiveCorrection(
        context,
        inputStage,
        draggablePoints,
        screenPoints,
        pipeline
    )

    val drawCorners = DrawCornersStage(
        context,
        pipeline,
        draggablePoints,
        inputStage.frameBufferInfo.textureSize
    )

    val overlay = OverlayStage(
        context,
        perspectiveTransformPointsStage.frameBufferInfo,
        drawCorners.frameBufferInfo,
        pipeline
    )

    return overlay
}