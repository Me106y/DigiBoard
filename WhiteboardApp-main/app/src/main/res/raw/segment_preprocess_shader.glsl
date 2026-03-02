#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    // manipulate UVs so it is correctly oriented in the segmentor
    // TODO: take phone orientation into account. Right now it is hardcoded for right handed landscape orientation.
    vec2 uvFlipped = vec2(1.0 - uv.y, uv.x);

    vec4 color = texture2D(framebuffer, uvFlipped);

    gl_FragColor = color;
}
