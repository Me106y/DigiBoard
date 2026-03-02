#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D source_texture;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 color = texture2D(source_texture, uv);

    gl_FragColor = color;
}