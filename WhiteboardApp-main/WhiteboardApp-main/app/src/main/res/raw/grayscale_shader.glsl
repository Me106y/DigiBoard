#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D framebuffer;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 color = texture2D(framebuffer, uv);

    // https://tannerhelland.com/2011/10/01/grayscale-image-algorithm-vb6.html
    float value = color.r * 0.3 + color.g * 0.59 + color.b * 0.11;

    gl_FragColor = vec4(vec3(value), color.a);
}
