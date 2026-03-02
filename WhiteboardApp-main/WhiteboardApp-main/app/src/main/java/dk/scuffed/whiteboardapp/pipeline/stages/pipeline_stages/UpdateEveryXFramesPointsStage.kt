package dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int

internal class UpdateEveryXFramesPointsStage(
    inputStageConstructor: (pipeline: IPipeline) -> Unit,
    framesToSkip: Int,
    pipeline: IPipeline,
    vararg points: Vec2Int
) : UpdateEveryXFramesStageBase(inputStageConstructor, framesToSkip, pipeline) {

    private val pointsOutputStage = MyPointsOutputStage(pipeline, *points)

    private val inputPointsOutputStage: PointsOutputStage = stages.last() as PointsOutputStage

    fun getPointsOutputStage(): PointsOutputStage {
        return pointsOutputStage
    }

    override fun updateOutput() {
        pointsOutputStage.setPoints(inputPointsOutputStage.points)
    }

    private class MyPointsOutputStage(pipeline: IPipeline, vararg points: Vec2Int) :
        PointsOutputStage(pipeline, *points) {

        fun setPoints(array: Collection<Vec2Int>) {
            points.clear()
            points.addAll(array)
        }

        override fun update() {
            // Nothing
        }
    }
}