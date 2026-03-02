#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec4 color;

void main() {
    gl_FragColor = color;
}
