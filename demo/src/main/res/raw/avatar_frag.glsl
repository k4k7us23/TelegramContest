precision highp float;

uniform sampler2D uTexture;

uniform vec2 uViewSize;
uniform float uCornerRadius;

varying vec2 vTexCoord;
varying vec2 vVertexScale;

vec2 calculateNormalizedCoord(vec2 texCoord, vec2 vertexScale) {
    vec2 shiftV = vec2(((-1.0 / vertexScale.x) + 1.0) / 2.0, ((-1.0 / vertexScale.y) + 1.0) / 2.0);
    vec2 divV = vec2(1.0 / vertexScale.x, 1.0 / vertexScale.y);
    return clamp((texCoord - shiftV) / divV, 0.0, 1.0);
}

float calculateAlpha(vec2 pixelCoord, vec2 viewSize, float cornerRadius) {
    float left = pixelCoord.x;
    float right = viewSize.x - pixelCoord.x;
    float top = pixelCoord.y;
    float bottom = viewSize.y - pixelCoord.y;

    vec2 corner = vec2(
    min(left, right),
    min(top, bottom)
    );

    float dist = length(max(vec2(cornerRadius) - corner, 0.0));

    float antialiasingWidth = 2.0;
    return 1.0 - smoothstep(cornerRadius - antialiasingWidth, cornerRadius, dist);
}

void main() {
    vec4 color = texture2D(uTexture, vTexCoord);

    vec2 normalizedCoord = calculateNormalizedCoord(vTexCoord, vVertexScale);
    vec2 pixelCoord = (normalizedCoord * uViewSize);

    float alpha = calculateAlpha(pixelCoord, uViewSize, uCornerRadius);

    gl_FragColor = vec4(color.rgb, alpha);
}