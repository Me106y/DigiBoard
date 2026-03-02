package dk.scuffed.whiteboardapp.helper;

import android.util.Log;

public class GlesHelper
{
    static {
        Log.d("asd", "Loading the GLESHelper!");
        System.loadLibrary("native-lib");
    }
    public static native void glReadPixels(int x, int y, int width, int height, int format, int type);
}