package dk.scuffed.whiteboardapp.pipeline.stages

import android.graphics.Bitmap
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline

/**
 * Baseclass for stages that output bitmaps.
 * @property outputBitmap allows to read and write this bitmap.
 */
internal abstract class BitmapOutputStage(
    pipeline: IPipeline,
    resolution: Size,
    config: Bitmap.Config
) : Stage(pipeline) {
    var outputBitmap: Bitmap

    init {
        outputBitmap = Bitmap.createBitmap(resolution.width, resolution.height, config)
    }
}