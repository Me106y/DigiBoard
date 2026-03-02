package dk.scuffed.whiteboardapp.pipeline.stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.utils.LineFloat

internal abstract class LinesOutputStage(pipeline: IPipeline) : Stage(pipeline) {
    val lines = ArrayList<LineFloat>()
}