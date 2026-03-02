package dk.scuffed.whiteboardapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import dk.scuffed.whiteboardapp.opengl.OpenGLView
import org.opencv.android.OpenCVLoader


class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            Log.d("Whiteboard App", "Unable to load OpenCV")
        } else {
            Log.d("Whiteboard App", "OpenCV loaded")
        }

        // Check and request camera permissions
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
        else {
            createViews()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createViews()
            } else {
                Toast.makeText(this, "This app requires camera permission!", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }
    }

    private fun createViews() {
        setContentView(R.layout.activity_main)
        val buttonView = findViewById<ConstraintLayout>(R.id.button_view)
        val openGLView = OpenGLView(this)
        buttonView.addView(openGLView)
    }
}
