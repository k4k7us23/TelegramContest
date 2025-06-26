precision highp float;

attribute vec4 aPosition;
attribute vec2 aTexCoord;

uniform float uImageAspect;
uniform float uViewAspect;

uniform float uZoom;

varying vec2 vTexCoord;
varying vec2 vVertexScale;

void main() {
    float scaleX = 1.0;
    float scaleY = 1.0;

    if (uViewAspect < uImageAspect) {
        scaleX = uImageAspect / uViewAspect;
    } else {
        scaleY = uViewAspect / uImageAspect;
    }

    vVertexScale = vec2(scaleX * uZoom, scaleY * uZoom);

    gl_Position = vec4(aPosition.x * vVertexScale.x, aPosition.y * vVertexScale.y, 0.0, 1.0);
    vTexCoord = aTexCoord;
}