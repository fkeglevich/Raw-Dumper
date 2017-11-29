precision mediump float;

uniform mat4 surfaceMatrix;

uniform vec2 surfaceSize;
uniform vec2 previewScale;

attribute vec2 vertexPosition;

varying vec2 textureCoord;

void main()
{
    gl_Position = vec4(vertexPosition, 0.0, 1.0);
    textureCoord = (surfaceMatrix * ((gl_Position + 1.0) * 0.5)).xy;
    gl_Position.xy *= previewScale;
    gl_Position.y += 1.0 - previewScale.y;
}