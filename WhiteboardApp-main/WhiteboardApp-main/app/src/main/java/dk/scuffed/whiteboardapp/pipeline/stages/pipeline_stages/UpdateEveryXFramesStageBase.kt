package dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair
import dk.scuffed.whiteboardapp.pipeline.stages.Stage

internal abstract class UpdateEveryXFramesStageBase(
    inputStageConstructor: (pipeline: IPipeline) -> Unit,
    private val framesToSkip: Int,
    private val pipeline: IPipeline
) :
    Stage(pipeline),
    IPipeline {

    protected val stages: ArrayList<Stage> = ArrayList()

    init {
        inputStageConstructor(this)
    }


    private var frames = 0

    protected abstract fun updateOutput()

    override fun update() {
        if (frames == 0) {
            draw()
            updateOutput()
        }

        frames++

        if (frames == framesToSkip) {
            frames = 0
        }
    }

    final override fun draw() {
        for (stage in stages) {
            stage.performUpdate()
        }
    }

    final override fun whenResolutionChanged(resolution: Size) {
        onResolutionChanged(resolution)
    }

    final override fun onResolutionChanged(resolution: Size) {
        for (stage in stages) {
            stage.performOnResolutionChanged(resolution)
        }
    }

    final override fun getInitialResolution(): Size {
        return pipeline.getInitialResolution()
    }

    final override fun addStage(stage: Stage) {
        stages.add(stage)
    }

    final override fun allocateFramebuffer(
        stage: Stage,
        textureFormat: Int,
        size: Size
    ): FramebufferInfo {
        return pipeline.allocateFramebuffer(stage, textureFormat, size)
    }

    final override fun allocateTextureUnit(stage: Stage): TextureUnitPair {
        return pipeline.allocateTextureUnit(stage)
    }
}