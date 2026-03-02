package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.BitmapToFramebufferStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.FramebufferToBitmapPBOStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.FramebufferToBitmapStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.OpenCVDilateStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GrayscaleStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.MaskingStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.SegmentationAccumulationStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.StoreStage
import dk.scuffed.whiteboardapp.pipeline.stages.segmentation_stages.SegmentationPostProcessingStage
import dk.scuffed.whiteboardapp.pipeline.stages.segmentation_stages.SegmentationPreProcessingStage
import dk.scuffed.whiteboardapp.pipeline.stages.segmentation_stages.SegmentationStage
import dk.scuffed.whiteboardapp.pipeline.useDoubleBuffering
import dk.scuffed.whiteboardapp.segmentation.PPSegmentation

/**
 * Runs the entire perspective correction based on two sets of points and an input framebuffer
 */
internal fun fullSegmentation(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    val oldInput = if (useDoubleBuffering) {
        pipeline.allocateFramebuffer(
            inputStage,
            GLES20.GL_RGBA,
            inputStage.frameBufferInfo.textureSize
        )
    } else {
        inputStage.frameBufferInfo
    }

    val segPre = SegmentationPreProcessingStage(
        context,
        inputStage.frameBufferInfo,
        PPSegmentation.Model.PORTRAIT,
        pipeline
    )


    val segBitmap =
        if (useDoubleBuffering) {
            FramebufferToBitmapPBOStage(
                segPre.frameBufferInfo,
                Bitmap.Config.ARGB_8888,
                pipeline
            )
        } else {
            FramebufferToBitmapStage(
                segPre.frameBufferInfo,
                Bitmap.Config.ARGB_8888,
                pipeline
            )

        }

    val seg = SegmentationStage(
        context,
        PPSegmentation.Model.PORTRAIT,
        segBitmap.outputBitmap,
        pipeline
    )


    val dilation = OpenCVDilateStage(
        seg,
        2.0,
        pipeline
    )


    val segFramebuffer = BitmapToFramebufferStage(
        dilation,
        pipeline
    )

    val accumulationStage = SegmentationAccumulationStage(
        context,
        segFramebuffer.frameBufferInfo,
        0.2f,
        pipeline
    )

    val segPost = SegmentationPostProcessingStage(
        context,
        accumulationStage.frameBufferInfo,
        inputStage.frameBufferInfo.textureSize,
        pipeline
    )

    val storedFramebuffer = pipeline.allocateFramebuffer(
        inputStage,
        GLES20.GL_RGBA,
        oldInput.textureSize
    )


    val maskStage = MaskingStage(
        context,
        storedFramebuffer,
        oldInput,
        segPost.frameBufferInfo,
        pipeline
    )

    val storeStage = StoreStage(
        context,
        maskStage.frameBufferInfo,
        storedFramebuffer,
        pipeline
    )

    if (useDoubleBuffering) {
        val store1 = StoreStage(context, inputStage.frameBufferInfo, oldInput, pipeline)
    }

    return maskStage
}