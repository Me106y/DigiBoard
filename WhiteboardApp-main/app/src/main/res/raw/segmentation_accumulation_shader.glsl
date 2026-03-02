#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;

uniform float accumulation_factor;

uniform sampler2D segmentation;
uniform sampler2D oldAccumulator;

void main() {
    vec2 samplersUV = gl_FragCoord.xy / resolution;

    vec4 segInput = texture2D(segmentation, samplersUV);
    vec4 oldAccumulator = texture2D(oldAccumulator, samplersUV);


    // get the segment map as a 0 1 mask.
    float inverseSegmentMap = 1.0 - step(0.05, segInput.z);

    // Set pixels to zero where it is segmented. Otherwise, increment pixels back towards to 1.
    float add = (oldAccumulator.x + accumulation_factor) * inverseSegmentMap;
    
    gl_FragColor = vec4(add, 0.0, 0.0, add);
}