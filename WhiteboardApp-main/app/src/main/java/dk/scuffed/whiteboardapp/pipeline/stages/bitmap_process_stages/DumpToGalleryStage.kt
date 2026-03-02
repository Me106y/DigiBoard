package dk.scuffed.whiteboardapp.pipeline.stages.bitmap_process_stages

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import android.widget.Button
import dk.scuffed.whiteboardapp.MainActivity
import dk.scuffed.whiteboardapp.R
import dk.scuffed.whiteboardapp.pipeline.*
import dk.scuffed.whiteboardapp.pipeline.stages.BitmapOutputStage
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * Allows saving a bitmap to the phone gallery.
 * Is controlled by a button.
 * When the button is pressed, all of these stages will dump from current frame.
 */
internal class DumpToGalleryStage(
    private val context: Context,
    private val bitmapStage: BitmapOutputStage,
    pipeline: IPipeline
) : BitmapOutputStage(
    pipeline,
    Size(bitmapStage.outputBitmap.width, bitmapStage.outputBitmap.height),
    bitmapStage.outputBitmap.config
) {
    init {
        // Take control of the listening. Note it overrides, so only one can control it.
        val button = (context as MainActivity).findViewById<Button>(R.id.capture)

        button.setOnClickListener {
            DumpAll()
        }

        addDumpGalleryStage(this)
    }

    override fun update() {
        if (checkShouldDumpThisFrame(this)){
            dump()
        }
    }

    /// Dump content of framebuffer to gallery
    fun dump(){
        saveImage(bitmapStage.outputBitmap, context, "WhiteboardApp")
    }

    companion object {
        private var dumpThisFrame: Boolean = false
        private var shouldDump: Boolean = false
        /// Makes all DumpToGalleryStages dump their content to the gallery
        fun DumpAll(){
            shouldDump = true
        }
        /// Check and update if this stage should dump.
        fun checkShouldDumpThisFrame(stage: DumpToGalleryStage): Boolean {
            if (dumpThisFrame) {
                if (stage == stages.last()){
                    dumpThisFrame = false
                    shouldDump = false
                }
                return true
            }

            if (shouldDump && stage == stages.last()){
                dumpThisFrame = true
            }
            return false
        }
        fun addDumpGalleryStage(s: DumpToGalleryStage){
            stages.add(s)
        }

        private val stages = arrayListOf<DumpToGalleryStage>()
    }



    //Image saving to gallery functionality is from https://stackoverflow.com/a/57265702
    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        val values = contentValues()
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        // RELATIVE_PATH and IS_PENDING are introduced in API 29.

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}