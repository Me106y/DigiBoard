package dk.scuffed.whiteboardapp.pipeline.stages.lines_stages

import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.stages.LinesOutputStage
import kotlin.math.atan2

internal class LinesAngleDiscriminatorStage(
    private val linesOutputStage: LinesOutputStage,
    private val minAngle: Float,
    private val maxAngle: Float,
    pipeline: IPipeline
) : LinesOutputStage(pipeline) {
    override fun update() {
        lines.clear()
        for (line in linesOutputStage.lines) {
            val theta =
                atan2(line.endPoint.y - line.startPoint.y, line.endPoint.x - line.startPoint.x)
            if (theta in minAngle..maxAngle || theta in (minAngle + Math.PI)..(maxAngle + Math.PI) || theta in (minAngle - Math.PI)..(maxAngle - Math.PI)) {
                lines.add(line)
            }
        }
    }

}