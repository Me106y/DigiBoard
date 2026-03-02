#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform float accumulation_factor;

uniform sampler2D framebuffer;
uniform sampler2D oldAccumulator;

void main() {
    vec2 samplersUV = gl_FragCoord.xy / resolution;

    vec4 input = texture2D(framebuffer, samplersUV);
    vec4 oldAccumulator = texture2D(oldAccumulator, samplersUV);
    
    float add = oldAccumulator.x + accumulation_factor * input.x - accumulation_factor * (1.0 - input.x);


    gl_FragColor = vec4(add, add, add, 1.0);
}