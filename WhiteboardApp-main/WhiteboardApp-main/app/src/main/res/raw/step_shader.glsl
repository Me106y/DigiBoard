#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform float edge;
uniform vec2 resolution;
uniform sampler2D framebuffer;

// Outputs color where the alpha is above the threshold, otherwise outputs white
void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 color = texture2D(framebuffer, uv);

    float stepValue = step(color.a, edge);

    gl_FragColor = vec4(color.xyz * stepValue + vec3(1.0 - stepValue), 1.0);
}
