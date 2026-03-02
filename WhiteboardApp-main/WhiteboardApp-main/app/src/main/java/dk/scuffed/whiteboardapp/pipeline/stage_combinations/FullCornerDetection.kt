package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.graphics.Bitmap
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.FramebufferToBitmapPBOStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.FramebufferToBitmapStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.OpenCVLineDetectionStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.BiggestQuadStage
import dk.scuffed.whiteboardapp.pipeline.stages.lines_stages.LinesAngleDiscriminatorStage
import dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages.ThreadedBitmapInputPointOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.WeightedMovingAveragePointsStage
import dk.scuffed.whiteboardapp.pipeline.useDoubleBuffering
import dk.scuffed.whiteboardapp.utils.Vec2Int

/**
 * Does corner detection using hough line transforms and finding the biggest quadrilateral.
 * Preprocesses the image using canny edge detection.
 * @return points found
 */
internal fun fullCornerDetection(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): PointsOutputStage {

    val threadedBitmapInputPointOutputStage = ThreadedBitmapInputPointOutputStage(
        { pipeline ->
            val edges = fullCannyEdgeDetection(
                context,
                inputStage,
                pipeline
            )

            val edgesBitmapStage = if (useDoubleBuffering) {
                FramebufferToBitmapPBOStage(
                    edges.frameBufferInfo,
                    Bitmap.Config.ARGB_8888,
                    pipeline
                )
            }
            else{
                FramebufferToBitmapStage(
                    edges.frameBufferInfo,
                    Bitmap.Config.ARGB_8888,
                    pipeline
                )
            }
        },
        { inputBitmapStage, pipeline ->
            val openCVLineDetectionStage = OpenCVLineDetectionStage(
                inputBitmapStage,
                150,
                50,
                pipeline,
                1.0,
                Math.PI / 180.0,
                15.0,
                Math.PI / 75.0
            )

            val verticalLinesAngleDiscriminatorStage = LinesAngleDiscriminatorStage(
                openCVLineDetectionStage,
                -(Math.PI / 4.0f).toFloat(),
                (Math.PI / 4.0f).toFloat(),
                pipeline
            )

            val horizontalLinesAngleDiscriminatorStage = LinesAngleDiscriminatorStage(
                openCVLineDetectionStage,
                (Math.PI / 4.0f).toFloat(),
                (Math.PI / 2.0f + Math.PI / 4.0f).toFloat(),
                pipeline
            )

            val biggestQuadStage = BiggestQuadStage(
                horizontalLinesAngleDiscriminatorStage,
                verticalLinesAngleDiscriminatorStage,
                pipeline
            )

            val weightedCornerStage = WeightedMovingAveragePointsStage(
                biggestQuadStage,
                20,
                pipeline
            )
        },
        pipeline,
        Vec2Int(0,0),
        Vec2Int(0,0),
        Vec2Int(0,0),
        Vec2Int(0,0)
    )

    return threadedBitmapInputPointOutputStage.myOutputPointsStage
}