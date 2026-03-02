#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform vec2 resolution;
uniform sampler2D source_texture;


varying vec3 v_TexCoordinate;

void main() {
    // Divides by Z to revert the multiplication of z done per vertex
    vec4 color = texture2D(source_texture, vec2(v_TexCoordinate.x / v_TexCoordinate.z, v_TexCoordinate.y / v_TexCoordinate.z));

    gl_FragColor = color;
}