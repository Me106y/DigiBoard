package dk.scuffed.whiteboardapp.pipeline.stages.points_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.utils.Vec2Float
import dk.scuffed.whiteboardapp.utils.Vec2Int
import kotlin.math.round

internal class WeightedMovingAveragePointsStage(
    private val inputPoints: PointsOutputStage,
    private val historySize: Int,
    pipeline: IPipeline
) : PointsOutputStage(pipeline, inputPoints.points[0], inputPoints.points[1], inputPoints.points[2], inputPoints.points[3]) {
    private var pointHistories =
        arrayOf(
            Array(historySize) { inputPoints.points[0] },
            Array(historySize) { inputPoints.points[1] },
            Array(historySize) { inputPoints.points[2] },
            Array(historySize) { inputPoints.points[3] }
        )
    private var pointIndex = 0


    override fun update() {
        pointHistories[0][pointIndex] = inputPoints.points[0]
        pointHistories[1][pointIndex] = inputPoints.points[1]
        pointHistories[2][pointIndex] = inputPoints.points[2]
        pointHistories[3][pointIndex] = inputPoints.points[3]

        pointIndex = (pointIndex + 1) % historySize

        points[0] = weightedAvgPoint(pointHistories[0])
        points[1] = weightedAvgPoint(pointHistories[1])
        points[2] = weightedAvgPoint(pointHistories[2])
        points[3] = weightedAvgPoint(pointHistories[3])
    }

    private fun weightedAvgPoint(pointHistory: Array<Vec2Int>): Vec2Int {
        val meanSum = pointHistory.reduce { sum, element -> sum + element }.toVec2Float()

        val mean = meanSum / pointHistory.size.toFloat()

        var weightSum = 0f
        var sum = Vec2Float(0f, 0f)

        // Loop goes through a list of points and calculates their distance from the mean position of the list.
        // The distance is compared to a threshold and used to calculate a weight, it its not within the threshold.
        // weighted-XY and total-weight values are summed to be used for calculating the new weighted average of all the points.
        for (point in pointHistory) {
            val distFromMean = point.toVec2Float().distance(mean)
            val weight = if (distFromMean <= 0.01) 100f else 1.0f / distFromMean
            sum += point.toVec2Float() * weight
            weightSum += weight
        }

        val result = sum / weightSum
        return Vec2Float(round(result.x), round(result.y)).toVec2Int()
    }
}