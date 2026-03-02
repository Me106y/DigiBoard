#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform samplerExternalOES camera;
uniform vec2 camera_resolution;

void main() {
    vec2 uv = 1.0 - (gl_FragCoord.yx / camera_resolution.xy);

    vec4 color = texture2D(camera, uv);

    gl_FragColor = color;
}


/*
uniform float time;
uniform vec2 resolution;
uniform samplerExternalOES cam;
uniform vec2 camResolution;
uniform int gaussianKernelSize;
uniform float gaussianOffsets[3];
uniform float gaussianWeights[3];


vec4 blur(samplerExternalOES tex, vec2 coord) {
    float w = 1.0 / camResolution.x;
    float h = 1.0 / camResolution.y;


    vec4 blurred = texture2D(tex, coord) * gaussianWeights[0];

    for (int i = 1; i < gaussianKernelSize; i++) {
        blurred += texture2D(tex, coord + (vec2(gaussianOffsets[i], 0.0)) * w) * gaussianWeights[i];
        blurred += texture2D(tex, coord - (vec2(gaussianOffsets[i], 0.0)) * w) * gaussianWeights[i];

        blurred += texture2D(tex, coord + (vec2(0.0, gaussianOffsets[i])) * h) * gaussianWeights[i];
        blurred += texture2D(tex, coord - (vec2(0.0, gaussianOffsets[i])) * h) * gaussianWeights[i];
    }

    return blurred;
}

void main() {
    vec2 divThing = resolution.xy / camResolution.yx;
    vec2 fixedCamRes = min(divThing.x, divThing.y) * camResolution.yx;

    float halfThing = (fixedCamRes.y - resolution.y) / 2.0;

    vec2 uv = vec2(gl_FragCoord.x / fixedCamRes.x, gl_FragCoord.y / fixedCamRes.y + halfThing / resolution.y);
    vec2 camuv = 1.0 - uv.yx;


    float h = halfThing / resolution.y;

    if (uv.y < 0.0 || uv.y > 1.0) {
        gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);
    }
    else {
        if (uv.x < 0.5) {
            vec4 blurredCam = blur(cam, camuv);
            gl_FragColor = blurredCam;
        }
        else {
            vec4 img = texture2D(cam, camuv);
            gl_FragColor = img;
        }
    }
}
*/

