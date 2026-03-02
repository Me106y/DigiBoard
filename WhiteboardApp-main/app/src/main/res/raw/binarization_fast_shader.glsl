#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;
uniform sampler2D downscaledFramebuffer;
uniform float threshold;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 greyscaled = texture2D(framebuffer, uv);
    vec4 avg = texture2D(downscaledFramebuffer, uv);

    float thresholdedAvg = avg.x * (100.0-threshold)/100.0;

    gl_FragColor = vec4(vec3(step(thresholdedAvg, greyscaled.x)), 1.0);
}
