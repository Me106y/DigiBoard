#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;

uniform float gaussian_offsets[3];
uniform float gaussian_weights[3];
uniform vec2 direction;


void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    float w = 1.0 / resolution.x;
    float h = 1.0 / resolution.y;
    vec2 d = vec2(w, h);

    vec4 blurred = texture2D(framebuffer, uv) * gaussian_weights[0];

    for (int i = 1; i < 3; i++) {
        blurred += texture2D(framebuffer, uv + (vec2(gaussian_offsets[i]) * direction * d)) * gaussian_weights[i];
        blurred += texture2D(framebuffer, uv - (vec2(gaussian_offsets[i]) * direction * d)) * gaussian_weights[i];
    }

    gl_FragColor = blurred;
}