#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D background_framebuffer;
uniform sampler2D foreground_framebuffer;

float lerp(float v0, float v1, float t) {
    return (1.0 - t) * v0 + t * v1;
}

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 background_color = texture2D(background_framebuffer, uv);
    vec4 foreground_color = texture2D(foreground_framebuffer, uv);

    // LIMITATION: We remove all transparency from the final image
    gl_FragColor = vec4(
        lerp(background_color.r, foreground_color.r, foreground_color.a),
        lerp(background_color.g, foreground_color.g, foreground_color.a),
        lerp(background_color.b, foreground_color.b, foreground_color.a),
        1.0
    );
}
