/*
 * Copyright 2019, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.gl.compute;

import org.junit.Test;

import static com.fkeglevich.rawdumper.gl.compute.ShaderSourceUtil.replaceLocalSize;
import static org.junit.Assert.*;

public class ShaderSourceUtilTest
{
    private static final String sampleSource = "#version 310 es\n" +
            "#extension GL_OES_EGL_image_external_essl3 : enable\n" +
            "#define LOCAL_X 1\n" +
            "#define LOCAL_Y 1\n" +
            "#define LOCAL_Z 1\n" +
            "\n" +
            "layout(std430, binding = 1) buffer outBuffer\n" +
            "{\n" +
            "     uint data[256];\n" +
            "};\n" +
            "\n" +
            "uniform samplerExternalOES input_image;\n" +
            "\n" +
            "layout (local_size_x = LOCAL_X, local_size_y = LOCAL_Y, local_size_z = LOCAL_Z) in;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\tivec2 p = ivec2(gl_GlobalInvocationID.xy);\n" +
            "\n" +
            "    vec4 color = texelFetch(input_image, p, 0);//imageLoad(input_image, p);\n" +
            "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114)) * 255.0;\n" +
            "\n" +
            "    atomicAdd(data[clamp(uint(128), 0u, 255u)], 1u);\n" +
            "}";

    @Test
    public void replaceLocalSizeTest()
    {
        System.out.println(replaceLocalSize(sampleSource, 8, 8, 8));
    }
}