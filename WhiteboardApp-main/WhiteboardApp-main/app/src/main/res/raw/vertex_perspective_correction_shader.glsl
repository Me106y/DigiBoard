attribute vec4 position;

attribute vec3 a_TexCoordinate;
varying vec3 v_TexCoordinate;
void main() {
    v_TexCoordinate = a_TexCoordinate;
    gl_Position = position;
}