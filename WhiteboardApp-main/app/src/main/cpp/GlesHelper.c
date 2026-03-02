#include <jni.h>
#include <GLES2/gl2.h>
JNIEXPORT void JNICALL


// This is required to allow calling glReadPixels with these parameters, because the Java bindings doesnt have this function signature for some reason
Java_dk_scuffed_whiteboardapp_helper_GlesHelper_glReadPixels(JNIEnv *env, jobject instance, jint x,
                                                        jint y, jint width, jint height,
                                                        jint format, jint type) {
    glReadPixels(x, y, width, height, format, type, 0);
}