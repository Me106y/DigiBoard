package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GaussianBlurStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GenerateMipmapStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.ScaleToResolutionStage

/**
 * Runs full image enhancement with binarization, white balancing and colorization.
 */
internal fun fullImageEnhancement(
    context: Context,
    inputStage: GLOutputStage,
    pipeline: IPipeline
): GLOutputStage {
    GenerateMipmapStage(inputStage.frameBufferInfo, false, pipeline)

    val downscaledForWhitebalance = ScaleToResolutionStage(
        context, inputStage.frameBufferInfo,
        Size(inputStage.frameBufferInfo.textureSize.width / 32, inputStage.frameBufferInfo.textureSize.height / 32),
        pipeline
    )

    val whitebalance = whiteBalance(
        context,
        inputStage,
        downscaledForWhitebalance,
        pipeline
    )


    val downscaledForBinarization = ScaleToResolutionStage(
        context, inputStage.frameBufferInfo,
        Size(inputStage.frameBufferInfo.textureSize.width / 8, inputStage.frameBufferInfo.textureSize.height / 8),
        pipeline
    )

    val binarized = binarize(
        context,
        inputStage,
        downscaledForBinarization,
        7.5f,
        pipeline)

    
    val readdedColour = addColour(
        context,
        whitebalance,
        binarized,
        pipeline
    )

    return readdedColour
}