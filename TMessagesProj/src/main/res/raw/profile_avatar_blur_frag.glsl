precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uTexture;
uniform vec2 uTexSize;   // texture resolution (width, height)
uniform float uSigma;     // blur radius/amount (e.g. 0.0 = no blur, up to ~1.0 for strong blur)
uniform int uRadius;
uniform vec2 uDir;       // blur direction: (1,0) for horizontal, (0,1) for vertical

// Gaussian weight computation (1D Gaussian kernel)
float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma));
}

void main() {
    if (uRadius == 1) {
        gl_FragColor = texture2D(uTexture, vTexCoord);
    } else {
        vec2 texelSize = uDir / uTexSize;  // step direction in UV
        vec4 accum = vec4(0.0);
        float weightSum = 0.0;

        // Sample center pixel
        float w = gaussian(0.0, uSigma);
        accum += texture2D(uTexture, vTexCoord) * w;
        weightSum += w;

        for (int i = 1; i <= 1000; ++i) {
            if (i > uRadius) break;

            float offset = float(i);
            float weight = gaussian(offset, uSigma);
            if (weight < 1.0 / 255.0) {
                break;
            }

            vec2 offsetUV = texelSize * offset;

            vec4 sample1 = texture2D(uTexture, vTexCoord + offsetUV);
            vec4 sample2 = texture2D(uTexture, vTexCoord - offsetUV);

            accum += (sample1 + sample2) * weight;
            weightSum += 2.0 * weight;
        }

        gl_FragColor = accum / weightSum;
    }
}