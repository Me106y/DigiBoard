package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.graphics.Bitmap
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.FramebufferToBitmapStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.OpenCVLineDetectionStage
import dk.scuffed.whiteboardapp.pipeline.stages.lines_stages.LinesAngleDiscriminatorStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.LetterboxingStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.OverlayStage
import dk.scuffed.whiteboardapp.pipeline.stages.points_stages.DrawLinesStage
import dk.scuffed.whiteboardapp.utils.Color

/**
 * Runs the hough line line detection and outputs the lines into a framebuffer for debugging.
 * Preprocesses the image using canny edge detection.
 */
internal fun lineDetectionDebugOverlayDraw(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {

    val edges = fullCannyEdgeDetection(
        context,
        inputStage,
        pipeline
    )

    val edgesBitmapStage = FramebufferToBitmapStage(
        edges.frameBufferInfo,
        Bitmap.Config.ARGB_8888,
        pipeline
    )

    val openCVLineDetectionStage = OpenCVLineDetectionStage(
        edgesBitmapStage,
        325,
        10,
        pipeline,
        3.0,
        Math.PI / 135,
        100.0,
        Math.PI / 15
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


    val drawLinesVert = DrawLinesStage(context, verticalLinesAngleDiscriminatorStage, Color(1f, 0f, 0f, 0.5f), inputStage.frameBufferInfo.textureSize, pipeline)

    val drawLinesHor = DrawLinesStage(context, horizontalLinesAngleDiscriminatorStage, Color(0f, 1f, 0f, 0.5f), inputStage.frameBufferInfo.textureSize, pipeline)

    val inputStageLetterboxed = LetterboxingStage(context, inputStage.frameBufferInfo, pipeline)
    val drawLinesVertLetterboxed = LetterboxingStage(context, drawLinesVert.frameBufferInfo, pipeline)
    val drawLinesHorLetterBoxed = LetterboxingStage(context, drawLinesHor.frameBufferInfo, pipeline)


    val overlay1 = OverlayStage(context, inputStageLetterboxed.frameBufferInfo, drawLinesVertLetterboxed.frameBufferInfo, pipeline)
    val overlay2 = OverlayStage(context, overlay1.frameBufferInfo, drawLinesHorLetterBoxed.frameBufferInfo, pipeline)



    return overlay2
}