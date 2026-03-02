#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;
uniform vec2 framebuffer_resolution;
uniform sampler2D noise;
uniform float time;

void main() {
    // UVs for framebuffer and noise texture
    vec2 fbUV = gl_FragCoord.xy / framebuffer_resolution;
    vec2 noiseUV = gl_FragCoord.xy / 1024.0 / 10.0 + time / 6.0 ;

    // Sample noise texture
    vec4 noiseColor = texture2D(noise, noiseUV);

    // Distort UVs using the noise, and resample using distorted UVs
    vec2 uv1 = (noiseColor.xy -0.5);
    vec4 noiseColor2 = texture2D(noise, uv1);

    // Sample the framebuffer with the doubly distorted UVs
    vec4 inputColor = texture2D(framebuffer, fbUV + (noiseColor2.xy -0.5)*0.05);

    gl_FragColor = inputColor;
}