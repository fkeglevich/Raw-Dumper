#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform mediump samplerExternalOES texture;
uniform mediump mat3 srgb2raw;
uniform mediump mat3 raw2srgb;
uniform mediump float factor;

uniform mediump float linAFactor;
uniform mediump float linBFactor;
uniform mediump float contrast;
uniform mediump float brightness;

varying mediump vec2 textureCoord;

const mat3 sRGB2XYZ = mat3(0.4360747,  0.3850649,  0.1430804,
                           0.2225045,  0.7168786,  0.0606169,
                           0.0139322,  0.0971045,  0.7141733);

const mat3 XYZ2sRGB = mat3( 3.1338561, -1.6168667, -0.4906146,
                           -0.9787684,  1.9161415,  0.0334540,
                            0.0719453, -0.2289914,  1.4052427);
const float alpha = 0.055;

vec3 sRGB2LinearsRGB(vec3 color)
{
    vec3 leqThan = color / 12.92;
    vec3 gThan   = pow((color + alpha) / (1.0 + alpha), vec3(2.4, 2.4, 2.4));
    return mix(gThan, leqThan, step(color, vec3(0.04045, 0.04045, 0.04045)));
}

vec3 linearsRGB2sRGB(vec3 color)
{
    vec3 leqThan = color * 12.92;
    vec3 gThan   = (1.0 + alpha) * pow(color, vec3(1.0 / 2.4, 1.0 / 2.4, 1.0 / 2.4)) - alpha;
    return mix(gThan, leqThan, step(color, vec3(0.0031308, 0.0031308, 0.0031308) ));
}

void main()
{
    vec3 sRGB = texture2D(texture, textureCoord).rgb;
    vec3 linearRGB = sRGB2LinearsRGB(sRGB);
    vec3 linearRAW = srgb2raw * linearRGB;
	vec3 RAW = linearRAW * linAFactor + linBFactor;

    linearRGB = raw2srgb * RAW;
    linearRGB = linearRGB * contrast + brightness;

    sRGB = linearsRGB2sRGB(linearRGB);

    gl_FragColor = vec4(sRGB.r, sRGB.g, sRGB.b, 1.0);
}