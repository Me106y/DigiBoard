package dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages

import android.content.Context
import android.util.Size
import android.widget.Button
import dk.scuffed.whiteboardapp.MainActivity
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import dk.scuffed.whiteboardapp.utils.Vec2Int

internal class SwitchablePointPipeline(
    context: Context,
    firstStageConstructor: (pipeline: IPipeline) -> Unit,
    secondStageConstructor: (pipeline: IPipeline) -> Unit,
    private val pipeline: IPipeline,
) : Stage(pipeline), IPipeline {
    private val firstStages = ArrayList<Stage>()
    private val secondStages = ArrayList<Stage>()

    //Used for switching between the two stage arrays.
    private var switch = true

    //Used for adding stages to the specific array based on the boolean value.
    private var loadingSwitch = false


    val pointsOutputStage: PointsOutputStage

    init {
        val button = (context as MainActivity).findViewById<Button>(R.id.round_button)
        button.setOnClickListener {
            setSwitch(!switch)
        }

        firstStageConstructor(this)
        loadingSwitch = true
        secondStageConstructor(this)
        val pts = getLastStagePoints()
        pointsOutputStage = MyPointsOutputStage(pipeline, pts[0], pts[1], pts[2], pts[3])
    }


    override fun draw() {
        for (stage in if (switch) {
            firstStages
        } else {
            secondStages
        }) {
            stage.performUpdate()
        }

        val points = getLastStagePoints()
        (pointsOutputStage as MyPointsOutputStage).setPoints(points)
    }

    override fun onResolutionChanged(resolution: Size) {
        for (stage in firstStages) {
            stage.performOnResolutionChanged(resolution)
        }
        for (stage in secondStages) {
            stage.performOnResolutionChanged(resolution)
        }
    }

    override fun getInitialResolution(): Size {
        return pipeline.getInitialResolution()
    }

    override fun addStage(stage: Stage) {
        if (loadingSwitch) {
            firstStages.add(stage)
        } else {
            secondStages.add(stage)
        }
    }

    override fun allocateFramebuffer(
        stage: Stage,
        textureFormat: Int,
        size: Size
    ): FramebufferInfo {
        return pipeline.allocateFramebuffer(stage, textureFormat, size)
    }

    override fun allocateTextureUnit(stage: Stage): TextureUnitPair {
        return pipeline.allocateTextureUnit(stage)
    }

    override fun update() {
        draw()
    }

    override fun whenResolutionChanged(resolution: Size) {
        onResolutionChanged(resolution)
    }

    /**
     * Sets the switch variable in the class.
     * @param bool is used for setting the switch variable.
     */
    fun setSwitch(bool: Boolean) {
        switch = bool
    }

    private fun getLastStage(): Stage {
        return if (switch) {
            firstStages.last()
        } else {
            secondStages.last()
        }
    }

    private fun getLastStagePoints(): ArrayList<Vec2Int> {
        return when (val lastStage = getLastStage()) {
            is PointsOutputStage -> lastStage.points
            is ThreadedBitmapInputPointOutputStage -> lastStage.myOutputPointsStage.points
            else -> throw Exception("Tried to switch to a unknown point stage.")
        }
    }

    private class MyPointsOutputStage(pipeline: IPipeline, vararg initialPoints: Vec2Int) :
        PointsOutputStage(pipeline, *initialPoints) {
        override fun update() {
            //Do nothing
        }

        /**
         * Sets the new points the the corner points
         * @param newPoints is array of Vec2Int as the corner coordinates.
         */
        fun setPoints(newPoints: ArrayList<Vec2Int>) {
            points.clear()
            points.addAll(newPoints)
        }

    }
}