#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform mediump samplerExternalOES texture;

varying mediump vec2 textureCoord;

void main()
{
    gl_FragColor = texture2D(texture, textureCoord);
}