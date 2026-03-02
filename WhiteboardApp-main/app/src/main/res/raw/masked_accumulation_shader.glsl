#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;

uniform float accumulation_factor;

uniform sampler2D framebuffer;
uniform sampler2D old_accumulator_framebuffer;
uniform sampler2D mask;

void main() {
    vec2 samplersUV = gl_FragCoord.xy / resolution;

    vec4 inputColour = texture2D(framebuffer, samplersUV);
    vec4 oldAccumulator = texture2D(old_accumulator_framebuffer, samplersUV);
    vec4 mask = texture2D(mask, samplersUV);

    // Binarize by removing all greyscale values and only keeping full whites
    float binaryInput = step(1.0, inputColour.x);
    float maskValue = step(1.0, mask.x);

    float accumulated = (oldAccumulator.a + (maskValue) * (accumulation_factor * binaryInput - accumulation_factor * (1.0-binaryInput)));

    gl_FragColor = vec4(inputColour.xyz * maskValue + oldAccumulator.xyz * (1.0-maskValue), accumulated);
}