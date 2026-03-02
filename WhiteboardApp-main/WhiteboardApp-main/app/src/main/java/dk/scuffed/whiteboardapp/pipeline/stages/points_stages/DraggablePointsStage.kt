package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Int
import kotlin.math.pow

/**
 * Creates and outputs 4 points that you can drag using dragPoint.
 */
internal class DraggablePointsStage(
    pipeline: IPipeline
) : PointsOutputStage(
    pipeline,
    Vec2Int(100, pipeline.getInitialResolution().height - 100),
    Vec2Int(100, 100),
    Vec2Int(pipeline.getInitialResolution().width - 100, 100),
    Vec2Int(pipeline.getInitialResolution().width - 100, pipeline.getInitialResolution().height - 100),
) {
    companion object {
        private var instance: DraggablePointsStage? = null

        fun dragPoint(screenPosition: Vec2Int) {
            instance?.dragPoint(screenPosition)
        }
    }

    fun dragPoint(screenPosition: Vec2Int) {
        val closestIndex = getClosest(screenPosition)

        points[closestIndex] = screenPosition
    }

    private fun getClosest(screenPosition: Vec2Int): Int {
        var closestSquareDist = Float.POSITIVE_INFINITY
        var closestIndex = -1
        for (p in points.indices) {
            val dist = points[p].distance(screenPosition)
            if (dist < closestSquareDist) {
                closestSquareDist = dist
                closestIndex = p
            }
        }
        return closestIndex
    }


    init {
        instance = this
    }

    override fun update() {
    }
}
