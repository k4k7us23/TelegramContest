precision mediump float;

varying vec2 vTexCoord;

uniform sampler2D uTexture;
uniform vec2 uTexSize;   // texture resolution (width, height)
uniform float uSigma;     // blur radius/amount (e.g. 0.0 = no blur, up to ~1.0 for strong blur)
uniform vec2 uDir;       // blur direction: (1,0) for horizontal, (0,1) for vertical

// Gaussian weight computation (1D Gaussian kernel)
float CalcGauss(float x, float sigma) {
    if (sigma <= 0.0) return 0.0;
    // Gaussian formula (normalized): exp(-x^2/(2*sigma)) / (2*pi*sigma)
    return exp(-(x * x) / (2.0 * sigma)) / (6.28318 * sigma);
}

void main() {
    // Start with center pixel
    vec4 centerColor = texture2D(uTexture, vTexCoord);
    vec4 accum = vec4(centerColor.rgb, 1.0);  // accumulate color in RGB, weight in alpha
    // Calculate one-pixel step vector for given direction
    vec2 texOffset = uDir / uTexSize;  // e.g. (1/width, 0) for horizontal

    // Sample on both sides of the center pixel
    for (int i = 1; i <= 32; ++i) {
        float weight = CalcGauss(float(i) / 32.0, uSigma * 0.5);
        if (weight < 1.0 / 255.0) break;  // stop when contributions are very small:contentReference[oaicite:7]{index=7}

        // Sample pixel i steps forward and i steps backward from center
        vec4 sample1 = texture2D(uTexture, vTexCoord + texOffset * float(i));
        vec4 sample2 = texture2D(uTexture, vTexCoord - texOffset * float(i));
        accum += vec4(sample1.rgb * weight, weight);
        accum += vec4(sample2.rgb * weight, weight);
    }

    // Normalize the accumulated color by total weight (stored in accum.a)
    vec3 blurredColor = accum.rgb / accum.a;
    gl_FragColor = vec4(blurredColor, 1.0);
}