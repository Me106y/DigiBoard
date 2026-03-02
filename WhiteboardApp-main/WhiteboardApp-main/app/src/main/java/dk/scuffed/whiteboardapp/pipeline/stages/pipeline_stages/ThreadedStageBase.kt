package dk.scuffed.whiteboardapp.pipeline.stages.pipeline_stages

import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.Stage
import java.util.concurrent.Semaphore

/**
 * A base class for a pipeline stage that runs its stages in a separate thread
 */
internal abstract class ThreadedStageBase(private val pipeline: IPipeline) : Stage(pipeline),
    IPipeline {
    protected val stages = ArrayList<Stage>()
    private val stageThread = Thread({ threadLoop() }, "ThreadedStage Thread")
    private var stageThreadStarted = false
    private var hasOutput = false

    private val inputReadySemaphore = Semaphore(0)
    private val outputReadySemaphore = Semaphore(1)

    protected val manualPipeline: IPipeline = MyManualPipeline(this)

    abstract fun updateInput()
    abstract fun updateOutput()

    private fun threadLoop() {
        assert(Thread.currentThread() == stageThread)
        while (true) {
            inputReadySemaphore.acquire()
            draw()
            outputReadySemaphore.release()
        }
    }

    final override fun draw() {
        assert(Thread.currentThread() == stageThread)

        for (stage in stages) {
            stage.performUpdate()
        }
    }

    final override fun onResolutionChanged(resolution: Size) {
        TODO("Not yet implemented")
    }

    final override fun getInitialResolution(): Size {
        assert(Thread.currentThread() != stageThread)
        return pipeline.getInitialResolution()
    }

    final override fun addStage(stage: Stage) {
        assert(stage !is GLOutputStage)
        stages.add(stage)
    }

    final override fun allocateFramebuffer(
        stage: Stage,
        textureFormat: Int,
        size: Size
    ): FramebufferInfo {
        assert(Thread.currentThread() != stageThread)
        return pipeline.allocateFramebuffer(stage, textureFormat, size)
    }

    final override fun allocateTextureUnit(stage: Stage): TextureUnitPair {
        assert(Thread.currentThread() != stageThread)
        return pipeline.allocateTextureUnit(stage)
    }

    final override fun update() {
        assert(Thread.currentThread() != stageThread)

        if (!stageThreadStarted) {
            stageThread.start()
            stageThreadStarted = true
        }

        if (outputReadySemaphore.tryAcquire()) {
            updateInput()
            if (hasOutput) {
                updateOutput()
            }
            inputReadySemaphore.release()
            hasOutput = true
        }
    }

    override fun whenResolutionChanged(resolution: Size) {
        super.whenResolutionChanged(resolution)

        manualPipeline.onResolutionChanged(resolution)
    }

    private class MyManualPipeline(private val pipeline: IPipeline) : IPipeline {
        private val stages = ArrayList<Stage>()

        override fun draw() {
            // Don't do anything
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
}