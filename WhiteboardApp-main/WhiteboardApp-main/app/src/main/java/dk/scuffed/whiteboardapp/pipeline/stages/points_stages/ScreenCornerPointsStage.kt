package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int

/**
 * Stage that always has the points given in the constructor.
 */
internal class ScreenCornerPointsStage(
    pipeline: IPipeline
) : PointsOutputStage(
    pipeline,
    Vec2Int(0, pipeline.getInitialResolution().height),
    Vec2Int(0, 0),
    Vec2Int(pipeline.getInitialResolution().width, 0),
    Vec2Int(pipeline.getInitialResolution().width, pipeline.getInitialResolution().height)) {
    override fun update() {
        points[0] = Vec2Int(0, getResolution().height)
        points[1] = Vec2Int(0, 0)
        points[2] = Vec2Int(getResolution().width, 0)
        points[3] = Vec2Int(getResolution().width, getResolution().height)

    }
}