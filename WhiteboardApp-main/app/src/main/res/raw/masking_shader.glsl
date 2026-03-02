#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 samplerResolution;

uniform sampler2D sampler1;
uniform sampler2D sampler2;
uniform sampler2D mask;

void main() {
    vec2 samplersUV = gl_FragCoord.xy / samplerResolution;
    
    vec4 col1 = texture2D(sampler1, samplersUV);
    vec4 col2 = texture2D(sampler2, samplersUV);
    vec4 mask = texture2D(mask, samplersUV);

    // add all channels together so it works with any colour (see segmentor, which is annoying and only uses one channel).
    float actualmask = step(1.0, mask.x + mask.y + mask.z);

    gl_FragColor = col2 * actualmask + (1.0-actualmask) * col1;
}