#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;
uniform int windowSize;
uniform float threshold;

void main() {
    float w = 1.0 / resolution.x;
    float h = 1.0 / resolution.y;

    vec2 uv = gl_FragCoord.xy / resolution;


    vec4 binarized = texture2D(framebuffer, uv);
    float subImageSum = 0.0;

    for (int i = -windowSize; i <= windowSize; i++)
    {
        for (int j = -windowSize; j <= windowSize; j++)
        {
            subImageSum += texture2D(framebuffer, uv + vec2(w*float(i), h*float(j))).x;
        }
    }

    subImageSum = subImageSum * (100.0-threshold)/100.0;

    gl_FragColor = vec4(step(subImageSum, float(4*windowSize*windowSize)*binarized.xxx), 1.0);
}
