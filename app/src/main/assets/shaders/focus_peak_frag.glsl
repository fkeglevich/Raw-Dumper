#extension GL_OES_EGL_image_external : require
#extension GL_OES_standard_derivatives : enable

precision mediump float;

uniform mediump samplerExternalOES texture;

varying mediump vec2 textureCoord;

void main()
{
    vec4 color = texture2D(texture, textureCoord);
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    float focusPeak = step(0.04, length(vec2(dFdx(gray), dFdy(gray))));

    gl_FragColor = mix(color, vec4(1.0, 0.0, 0.0, 1.0), focusPeak);
}