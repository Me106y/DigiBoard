package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.opengl.GLES20
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.*
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.MaskingStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.OverlayStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.StoreStage
import dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages.SwitchablePointPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.CornersFromResolutionStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.DraggablePointsStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.DrawCornersStage
import dk.scuffed.whiteboardapp.pipeline.useDoubleBuffering

/**
 * Our canonical full pipeline that does everything except input/output
 */
internal fun fullPipeline(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): Pair<SwitchablePointPipeline, GLOutputStage> {
    val fullSegmentation = fullSegmentation(context, inputStage, pipeline)

    // Get location of the whiteboard's corners, either manually or automatic
    val switchablePointPipeline = SwitchablePointPipeline(
        context,
        { pipeline -> DraggablePointsStage(pipeline) },
        { pipeline -> fullCornerDetection(context, fullSegmentation, pipeline) },
        pipeline
    )


    val cameraPointsStage =
        CornersFromResolutionStage(inputStage.frameBufferInfo.textureSize, pipeline)

    val perspectiveCorrected = fullPerspectiveCorrection(
        context,
        fullSegmentation,
        switchablePointPipeline.pointsOutputStage,
        cameraPointsStage,
        pipeline
    )


    val imageEnhancement = fullImageEnhancement(context, perspectiveCorrected, pipeline)


    return Pair(switchablePointPipeline, imageEnhancement)
}