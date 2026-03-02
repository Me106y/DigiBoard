package dk.scuffed.whiteboardapp.pipeline.stage_combinations

import android.content.Context
import android.graphics.Bitmap
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.DumpToGalleryStage
import dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages.FramebufferToBitmapStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.GaussianBlurStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.HSVAdjustmentsStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.MaskingStage
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.OpenGLToBitmapCoordinate

/**
 * Dumps the framebuffer into the gallery on button press.
 * Note it is slow and runs even if button isnt pressed.
 */
internal fun dumpToGalleryFull(
    context: Context,
    input: FramebufferInfo,
    pipeline: IPipeline
) {
    val adjusted = OpenGLToBitmapCoordinate(context, input, pipeline)

    val bitmap = FramebufferToBitmapStage(adjusted.frameBufferInfo, Bitmap.Config.ARGB_8888, pipeline)

    DumpToGalleryStage(context, bitmap, pipeline)
}