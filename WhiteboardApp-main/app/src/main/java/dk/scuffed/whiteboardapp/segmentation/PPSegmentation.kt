package dk.scuffed.whiteboardapp.segmentation

import android.content.Context
import android.graphics.Bitmap
import com.baidu.paddle.fastdeploy.LitePowerMode
import com.baidu.paddle.fastdeploy.RuntimeOption
import com.baidu.paddle.fastdeploy.vision.SegmentationResult
import com.baidu.paddle.fastdeploy.vision.Visualize
import com.baidu.paddle.fastdeploy.vision.segmentation.PaddleSegModel

/**
 * The Paddle Paddle segmentation
 */
class PPSegmentation(context: Context, private val model: Model) {
    enum class Model(val modelName: String, val width: Int, val height: Int) {
        PORTRAIT("humansegv2portrait", 256, 144),
        GENERALLITE("humansegv2generallite", 192, 192),
        GENERALMOBILE("humansegv2generalmobile", 192, 192)
    }

    private val segmentationModel: PaddleSegModel = PaddleSegModel()
    private val segmentationResult: SegmentationResult = SegmentationResult()

    fun segment(input: Bitmap): Bitmap {
        if (input.width != model.width || input.height != model.height) {
            throw Exception("Resolution of input bitmap to segmentor (" + input.width + "," + input.height + ") doesn't match required resolution of(" + model.width + "," + model.height + ")")
        }

        segmentationModel.predict(input, segmentationResult)

        // create and return a bitmap with result from prediction
        val bitmapResult: Bitmap =
            Bitmap.createBitmap(model.width, model.height, Bitmap.Config.ARGB_8888)

        Visualize.visSegmentation(bitmapResult, segmentationResult, 0.5f)
        return bitmapResult
    }

    init {
        // get all path names
        val modelDir = "models/${model.modelName}"
        val modelCacheDir = context.cacheDir.toString() + "/" + modelDir
        val modelFile = "$modelCacheDir/model.pdmodel"
        val paramsFile = "$modelCacheDir/model.pdiparams"
        val configFile = "$modelCacheDir/deploy.yaml"

        // copy the model from assets into the cache
        Utils.copyDirectoryFromAssets(context, modelDir, modelCacheDir)

        // configure how the model is run
        val option = RuntimeOption()
        option.setCpuThreadNum(4)
        option.setLitePowerMode(LitePowerMode.LITE_POWER_HIGH)
        option.enableLiteFp16()
        option.enableLiteInt8()

        // configure and initialize the model
        segmentationModel.setVerticalScreenFlag(false)
        segmentationModel.init(modelFile, paramsFile, configFile, option)

        // set up segmentation result to use as buffer
        segmentationResult.setCxxBufferFlag(true)
    }
}
