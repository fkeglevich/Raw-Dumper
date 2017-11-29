#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform vec2 surfaceSize;
uniform vec2 previewScale;

uniform samplerExternalOES texture;

varying vec2 textureCoord;

void main()
{
    vec4 texel = texture2D(texture, textureCoord);

    vec2 position = -1.0 + 2.0 * (gl_FragCoord.xy / surfaceSize);
    position.x *= surfaceSize.x / surfaceSize.y;

    float dist = distance(position, vec2(0.0, 1.0 - previewScale.y));
    float outerRadius = (1.0/surfaceSize.y) * (1080.0);
    float innerRadius = (1.0/surfaceSize.y) * (1080.0 - 5.0);

    gl_FragColor = mix(texel, vec4(0.10, 0.10, 0.10, 1.0), smoothstep(innerRadius, outerRadius, dist) * 0.75);
}