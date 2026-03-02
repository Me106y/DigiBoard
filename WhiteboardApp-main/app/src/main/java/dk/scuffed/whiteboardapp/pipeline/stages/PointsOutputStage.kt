package dk.scuffed.whiteboardapp.pipeline.stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.utils.Vec2Int

/**
 * Baseclass for stages that output points.
 * @property points allows to read and write these points.
 */
internal abstract class PointsOutputStage(
    pipeline: IPipeline,
    vararg initialPoints: Vec2Int
) : Stage(pipeline) {
    val points = ArrayList(listOf(*initialPoints))
}