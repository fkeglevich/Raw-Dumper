#extension GL_OES_EGL_image_external : require

precision lowp float;

uniform samplerExternalOES texture;

varying vec2 textureCoord;

void main()
{
    gl_FragColor = texture2D(texture, textureCoord);
}