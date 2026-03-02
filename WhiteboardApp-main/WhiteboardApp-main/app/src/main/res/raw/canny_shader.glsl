/*
Copyright © 2018 Dylan James Cutler (DCtheTall)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the “Software”), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


precision mediump float;

uniform vec2 resolution;
uniform sampler2D input_framebuffer;

uniform float weak_edge_threshold;
uniform float hard_edge_threshold;

const float PI = 3.14159265359;

float map(float value, float min1, float max1, float min2, float max2){
    return min2+(value-min1)*(max2-min2)/(max1-min1);
}

vec2 rotate2D(vec2 v, float rad) {
    float s = sin(rad);
    float c = cos(rad);
    return mat2(c, s, -s, c) * v;
}

/**
 * Return a vector with the same length as v
 * but its direction is rounded to the nearest
 * of 8 cardinal directions
 */
vec2 round2DVectorAngle(vec2 v) {
    float len = length(v);
    vec2 n = normalize(v);
    float maximum = -1.;
    float bestAngle;
    for (int i = 0; i < 8; i++) {
        float theta = (float(i) * 2. * PI) / 8.;
        vec2 u = rotate2D(vec2(1., 0.), theta);
        float scalarProduct = dot(u, n);
        if (scalarProduct > maximum) {
            bestAngle = theta;
            maximum = scalarProduct;
        }
    }
    return len * rotate2D(vec2(1., 0.), bestAngle);
}


/**
 * Get the gradient of the textures intensity
 * as a function of the texture coordinate
 */
vec2 getTextureIntensityGradient(
    sampler2D textureSampler,
    vec2 textureCoord,
    vec2 resolution
) {
    vec2 color = texture2D(textureSampler, textureCoord).rg;

    // Map the texture color space (0.0 to 1.0) to the sobel space (-1.0 to 1.0)
    float x = map(color.x, 0.0, 1.0, -1.0, 1.0);
    float y = map(color.y, 0.0, 1.0, -1.0, 1.0);
    return vec2(x, y);
}

vec2 getSuppressedTextureIntensityGradient(
    sampler2D textureSampler,
    vec2 textureCoord,
    vec2 resolution
) {
    vec2 gradient = getTextureIntensityGradient(textureSampler, textureCoord, resolution);
    gradient = round2DVectorAngle(gradient);
    vec2 gradientStep = normalize(gradient) / resolution;
    float gradientLength = length(gradient);
    vec2 gradientPlusStep = getTextureIntensityGradient(
        textureSampler, textureCoord + gradientStep, resolution);
    if (length(gradientPlusStep) >= gradientLength) return vec2(0.);
    vec2 gradientMinusStep = getTextureIntensityGradient(
        textureSampler, textureCoord - gradientStep, resolution);
    if (length(gradientMinusStep) >= gradientLength) return vec2(0.);
    return gradient;
}

float applyDoubleThreshold(
    vec2 gradient,
    float weakThreshold,
    float strongThreshold
) {
    float gradientLength = length(gradient);
    if (gradientLength < weakThreshold) return 0.;
    if (gradientLength < strongThreshold) return .5;
    return 1.;
}

float applyHysteresis(
    sampler2D textureSampler,
    vec2 textureCoord,
    vec2 resolution,
    float weakThreshold,
    float strongThreshold
) {
    float dx = 1. / resolution.x;
    float dy = 1. / resolution.y;
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            vec2 ds = vec2(
                -dx + (float(i) * dx),
                -dy + (float(j) * dy));
            vec2 gradient = getSuppressedTextureIntensityGradient(
                textureSampler, clamp(textureCoord + ds, vec2(0.), vec2(1.)), resolution);
            float edge = applyDoubleThreshold(gradient, weakThreshold, strongThreshold);
            if (edge == 1.) return 1.;
        }
    }
    return 0.;
}

float cannyEdgeDetection(
    sampler2D textureSampler,
    vec2 textureCoord,
    vec2 resolution,
    float weakThreshold,
    float strongThreshold
) {
    vec2 gradient = getSuppressedTextureIntensityGradient(textureSampler, textureCoord, resolution);
    float edge = applyDoubleThreshold(gradient, weakThreshold, strongThreshold);
    if (edge == .5) {
        edge = applyHysteresis(
            textureSampler, textureCoord, resolution, weakThreshold, strongThreshold);
    }
    return edge;
}

void main() {
    vec3 color = vec3(cannyEdgeDetection(input_framebuffer, gl_FragCoord.xy / resolution, resolution, weak_edge_threshold, hard_edge_threshold));

    gl_FragColor = vec4(color, 1.0);
}
