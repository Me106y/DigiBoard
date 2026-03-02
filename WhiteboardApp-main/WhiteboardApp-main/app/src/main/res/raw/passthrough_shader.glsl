#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;
uniform vec2 framebuffer_resolution;

void main() {
    vec2 uv = gl_FragCoord.xy / framebuffer_resolution;

    vec4 color = texture2D(framebuffer, uv);

    gl_FragColor = color;
}