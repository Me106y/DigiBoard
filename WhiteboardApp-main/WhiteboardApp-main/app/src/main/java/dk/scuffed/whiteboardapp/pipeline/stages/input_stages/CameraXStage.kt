package dk.scuffed.whiteboardapp.pipeline.stages.input_stages

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.util.Size
import android.view.Surface
import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.opengl.*
import dk.scuffed.whiteboardapp.pipeline.IPipeline
import dk.scuffed.whiteboardapp.pipeline.stages.GLOutputStage
import dk.scuffed.whiteboardapp.pipeline.Pipeline
import dk.scuffed.whiteboardapp.pipeline.TextureUnitPair
import java.util.concurrent.CompletableFuture

/**
 * Outputs the camera onto a OpenGL framebuffer
 */
internal class CameraXStage(
    context: Context,
    pipeline: IPipeline,
) : GLOutputStage(context, R.raw.vertex_shader, R.raw.camera_shader, pipeline) {

    private val cameraTextureUnitPair: TextureUnitPair
    private val cameraTextureHandle: Int
    private val cameraSurfaceTexture: SurfaceTexture
    private val cameraResolution: Size

    init {
        cameraTextureUnitPair = pipeline.allocateTextureUnit(this)
        cameraTextureHandle = createExternalOESTexture()
        cameraSurfaceTexture = SurfaceTexture(cameraTextureHandle)
        cameraSurfaceTexture.setDefaultBufferSize(getResolution().height, getResolution().width)

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(LENS_FACING_BACK)
            .build()

        val mainThreadHandler = Handler(context.mainLooper)

        val cameraResolutionFuture = CompletableFuture<Size>()

        mainThreadHandler.post {
            run {
                val preview = Preview.Builder()
                    .setTargetAspectRatio(RATIO_16_9)
                    .build()

                preview.setSurfaceProvider { request ->
                    val surface = Surface(cameraSurfaceTexture)
                    request.provideSurface(
                        surface,
                        ContextCompat.getMainExecutor(context)
                    ) { SurfaceRequest.Result.RESULT_SURFACE_USED_SUCCESSFULLY }
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.bindToLifecycle(context as LifecycleOwner, cameraSelector, preview)

                val resolution = preview.resolutionInfo!!.resolution
                cameraResolutionFuture.complete(resolution)
            }
        }

        cameraResolution = cameraResolutionFuture.get()
        setup()
    }

    override fun setupFramebufferInfo() {
        val resolution = Size(cameraResolution.height, cameraResolution.width)
        allocateFramebuffer(GLES20.GL_RGBA, resolution)
    }

    override fun setupUniforms(program: Int) {
        super.setupUniforms(program)

        cameraSurfaceTexture.updateTexImage()

        val cameraTextureHandle = glGetUniformLocation(program, "camera")
        glUniform1i(cameraTextureHandle, cameraTextureUnitPair.textureUnitIndex)
        glActiveTexture(cameraTextureUnitPair.textureUnit)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, this.cameraTextureHandle)

        val camResolutionHandle = glGetUniformLocation(program, "camera_resolution")
        glUniform2f(
            camResolutionHandle,
            cameraResolution.width.toFloat(),
            cameraResolution.height.toFloat()
        )
    }

    private fun createExternalOESTexture(): Int {
        val textureHandle = glGenTexture()

        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureHandle)
        glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)

        return textureHandle
    }
}