#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform mediump vec2 surfaceSize;
uniform mediump vec2 previewScale;
uniform mediump float revealRadius;
uniform mediump samplerExternalOES texture;

varying mediump vec2 textureCoord;

void main()
{
    vec2 position = -1.0 + 2.0 * (gl_FragCoord.xy / surfaceSize);
    position.x *= surfaceSize.x / surfaceSize.y;

    float outerRadius = revealRadius/surfaceSize.y;
    float dist = distance(position, vec2(0.0, 1.0 - previewScale.y));

    gl_FragColor = mix( texture2D(texture, textureCoord),
                        vec4(0.20, 0.20, 0.20, 1.0),
                        step(outerRadius, dist) * 0.9);
}