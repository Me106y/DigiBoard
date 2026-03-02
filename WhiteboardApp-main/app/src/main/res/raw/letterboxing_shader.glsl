#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform vec2 offset;

uniform sampler2D framebuffer;


void main() {
    vec2 uv = (gl_FragCoord.xy - offset)/resolution;

    gl_FragColor = texture2D(framebuffer, uv);
}
