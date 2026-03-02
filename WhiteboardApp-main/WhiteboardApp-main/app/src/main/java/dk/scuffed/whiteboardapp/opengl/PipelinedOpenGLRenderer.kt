package dk.scuffed.whiteboardapp.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Size
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PipelinedOpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var pipelineInitialized = false
    private lateinit var pipeline: Pipeline

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        if (!pipelineInitialized) {
            pipeline = Pipeline(context, Size(width, height))
            pipelineInitialized = true
        } else {
            pipeline.onResolutionChanged(Size(width, height))
        }
    }

    override fun onDrawFrame(p0: GL10?) {
        pipeline.draw()
    }
}