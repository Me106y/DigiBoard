package dk.scuffed.whiteboardapp.pipeline.stages.output_stages

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log
import android.util.Size
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.FramebufferInfo
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import dk.scuffed.whiteboardapp.pipeline.stages.Stage

/**
 * Draws any stage to tbe screen, allowing switching between stages.
 */
internal class DrawPipelineStage(
    context: Context,
    private val stages: MutableList<Stage>,
    private val pipeline: IPipeline
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.passthrough_shader, pipeline) {

    companion object {
        private var pipelinestage: DrawPipelineStage? = null

        private fun setStage(stage: DrawPipelineStage) {
            pipelinestage = stage
        }

        fun next() {
            pipelinestage?.nextStage()
        }

        fun prev() {
            pipelinestage?.prevStage()
        }
    }

    private var currentStageIndex: Int

    // Framebuffer used for drawing bitmaps into to throw into the GLOutput
    private var bitmapFramebuffer: FramebufferInfo

    init {
        setStage(this)
        setup()
        currentStageIndex = stages.lastIndex - 1
        bitmapFramebuffer = allocateBitmapTexture()
    }

    override fun setupFramebufferInfo() {
        // Set framebuffer to screen
        frameBufferInfo = FramebufferInfo(0, 0, TextureUnitPair(0, 0), 0, getResolution())
    }

    /// Go to the next stage in the pipeline, looping if reaching the end.
    fun nextStage() {
        currentStageIndex++
        if (currentStageIndex > stages.lastIndex) {
            currentStageIndex = 0
        }
        // Cant show itself!
        if (stages[currentStageIndex] == this) {
            nextStage()
        }
    }

    /// Go to the previous stage in the pipeline, looping if reaching the end.
    fun prevStage() {
        currentStageIndex--
        if (currentStageIndex < 0) {
            currentStageIndex = stages.lastIndex
        }
        // Cant show itself!
        if (stages[currentStageIndex] == this) {
            prevStage()
        }
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)


        val inputFrameBufferInfo: FramebufferInfo?
        val currentStage = stages[currentStageIndex]
        when (currentStage) {
            is GLOutputStage -> {
                inputFrameBufferInfo = currentStage.frameBufferInfo
            }
            is BitmapOutputStage -> {
                glActiveTexture(bitmapFramebuffer.textureUnitPair.textureUnit)
                glBindTexture(GLES20.GL_TEXTURE_2D, bitmapFramebuffer.textureHandle)
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, currentStage.outputBitmap, 0)
                inputFrameBufferInfo = bitmapFramebuffer
            }
            else -> {
                inputFrameBufferInfo = null
                Log.d("dsf", "This stage does not support debug viewing")
            }
        }

        if (inputFrameBufferInfo != null) {
            // Input framebuffer resolution
            val framebufferResolutionHandle =
                glGetUniformLocation(program, "framebuffer_resolution")
            glUniform2f(
                framebufferResolutionHandle,
                inputFrameBufferInfo.textureSize.width.toFloat(),
                inputFrameBufferInfo.textureSize.height.toFloat()
            )

            // Input framebuffer
            val framebufferTextureHandle = glGetUniformLocation(program, "framebuffer")
            glUniform1i(
                framebufferTextureHandle,
                inputFrameBufferInfo.textureUnitPair.textureUnitIndex
            )
            glActiveTexture(inputFrameBufferInfo.textureUnitPair.textureUnit)
            glBindTexture(GLES20.GL_TEXTURE_2D, inputFrameBufferInfo.textureHandle)
        }
    }

    private fun allocateBitmapTexture(): FramebufferInfo {
        val textureUnitPair = pipeline.allocateTextureUnit(this)
        glActiveTexture(textureUnitPair.textureUnit)

        val textureHandle = glGenTexture()
        glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return FramebufferInfo(
            999,
            textureHandle,
            textureUnitPair,
            GLES20.GL_RGBA,
            getResolution()
        )
    }
}