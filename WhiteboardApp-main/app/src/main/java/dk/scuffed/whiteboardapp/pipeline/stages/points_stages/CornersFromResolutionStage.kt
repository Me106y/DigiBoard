package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int
/**
 * The stages instantiate the corners location as the location of the resolution.
 */
internal class CornersFromResolutionStage(
    resolution: Size,
    pipeline: IPipeline
) :
    PointsOutputStage(
        pipeline,
        Vec2Int(0, resolution.height),
        Vec2Int(0, 0),
        Vec2Int(resolution.width, 0),
        Vec2Int(resolution.width, resolution.height)
    ) {
    override fun update() {
        // We don't need to update :^)
    }
}