package dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages

import android.graphics.Bitmap
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.PointsOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import dk.scuffed.whiteboardapp.utils.Vec2Int

/**
 * Takes a BitmapOutputStage as input and runs its stages in a separate thread and outputs it as a point output stage
 */
internal class ThreadedBitmapInputPointOutputStage(
    preliminaryStageConstructor: (pipeline: IPipeline) -> Unit,
    inputStageConstructor: (inputBitmapStage: BitmapOutputStage, pipeline: IPipeline) -> Unit,
    pipeline: IPipeline,
    vararg initialPoints: Vec2Int
) : ThreadedStageBase(pipeline) {
    private val myInputBitmapStage: MyInputBitmapStage

    private val outputPointsStage: PointsOutputStage
    val myOutputPointsStage: PointsOutputStage

    private val myPreliminaryStagesPipeline = MyPreliminaryStagesPipeline(pipeline)

    init {
        preliminaryStageConstructor(myPreliminaryStagesPipeline)

        val inputBitmap =
            (myPreliminaryStagesPipeline.stages.last() as BitmapOutputStage).outputBitmap
        val copiedInputBitmap = inputBitmap.copy(inputBitmap.config, false)
        myInputBitmapStage = MyInputBitmapStage(
            manualPipeline,
            Size(copiedInputBitmap.width, copiedInputBitmap.height),
            copiedInputBitmap.config
        )
        inputStageConstructor(myInputBitmapStage, this)
        outputPointsStage = stages.last() as PointsOutputStage
        myOutputPointsStage = MyOutputPointsStage(this, *initialPoints)
    }


    override fun updateInput() {
        myPreliminaryStagesPipeline.draw()

        val inputBitmap =
            (myPreliminaryStagesPipeline.stages.last() as BitmapOutputStage).outputBitmap
        val copiedInputBitmap = inputBitmap.copy(inputBitmap.config, false)
        myInputBitmapStage.updateBitmap(copiedInputBitmap)
    }

    override fun updateOutput() {
        (myOutputPointsStage as MyOutputPointsStage).updatePoints(outputPointsStage.points)
    }

    override fun whenResolutionChanged(resolution: Size) {
        super.whenResolutionChanged(resolution)

        myPreliminaryStagesPipeline.onResolutionChanged(resolution)

    }

    private class MyPreliminaryStagesPipeline(private val pipeline: IPipeline) : IPipeline {
        val stages = ArrayList<Stage>()

        override fun draw() {
            for (stage in stages) {
                stage.performUpdate()
            }
        }

        override fun onResolutionChanged(resolution: Size) {
            for (stage in stages) {
                stage.performOnResolutionChanged(resolution)
            }
        }

        override fun getInitialResolution(): Size {
            return pipeline.getInitialResolution()
        }

        override fun addStage(stage: Stage) {
            stages.add(stage)
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
    }

    private class MyInputBitmapStage(pipeline: IPipeline, resolution: Size, config: Bitmap.Config) :
        BitmapOutputStage(pipeline, resolution, config) {
        override fun update() {
            // Never call this
            assert(false)
        }

        fun updateBitmap(bitmap: Bitmap) {
            outputBitmap = bitmap
        }
    }

    private class MyOutputPointsStage(pipeline: IPipeline, vararg initialPoints: Vec2Int) :
        PointsOutputStage(pipeline, *initialPoints) {
        override fun update() {
            // Don't do anything
        }

        fun updatePoints(newPoints: ArrayList<Vec2Int>) {
            points.clear()
            points.addAll(newPoints)
        }
    }
}