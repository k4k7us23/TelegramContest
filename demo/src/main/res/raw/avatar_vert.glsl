attribute vec4 aPosition;
attribute vec2 aTexCoord;

uniform float uImageAspect;
uniform float uViewAspect;

uniform float uZoom;

varying vec2 vTexCoord;

void main() {
    float scaleX = 1.0;
    float scaleY = 1.0;

    if (uViewAspect < uImageAspect) {
        scaleX = uViewAspect / uImageAspect;
    } else {
        scaleY = uImageAspect / uViewAspect;
    }

    gl_Position = vec4(aPosition.x / scaleX * uZoom, aPosition.y / scaleY * uZoom, 0.0, 1.0);
    vTexCoord = aTexCoord;
}