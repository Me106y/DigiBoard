#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 color = texture2D(framebuffer, vec2(1.0 - uv.y, 1.0 - uv.x));

    gl_FragColor = color;
}
