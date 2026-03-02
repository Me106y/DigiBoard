#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D rawInput;
uniform sampler2D backgroundColor;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 raw = texture2D(rawInput, uv);
    vec4 background = texture2D(backgroundColor, uv);

    gl_FragColor = vec4(vec3(1.0) - (background.xyz - raw.xyz), 1.0);
}
