package dk.scuffed.whiteboardapp

import android.util.Size
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.scuffed.whiteboardapp.pipeline.stages.opengl_process_stages.AspectCorrectionStage

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AspectRatioUnitTest {
    @Test
    fun aspect_isCorrect()
    {
        val epsilon = 0.01
        val dataPoints = arrayOf(
            Pair(Size(800, 600), 16.0/9.0),
            Pair(Size(2000, 600), 16.0/9.0)
        )

        for (pair in dataPoints) {
            val targetResolution = AspectCorrectionStage.convertToAspectRatio(pair.first, pair.second)

            assertEquals(pair.second, targetResolution.width.toDouble() / targetResolution.height.toDouble(), epsilon)
        }
    }
}