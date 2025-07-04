precision mediump float;

attribute vec4 aPosition;   // clip-space vertex position (x,y from -1 to 1)
attribute vec2 aTexCoord;   // input texture coordinate (0 to 1 range)

varying vec2 vTexCoord;     // pass to fragment shader

void main() {
    vTexCoord = aTexCoord;
    gl_Position = aPosition;
}