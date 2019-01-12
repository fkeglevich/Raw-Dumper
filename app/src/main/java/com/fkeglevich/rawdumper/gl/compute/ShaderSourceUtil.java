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

public class ShaderSourceUtil
{
    public static String replaceLocalSize(String source, int localSizeX, int localSizeY, int localSizeZ)
    {
        source = source.replaceAll("#define\\s+LOCAL_X\\s+\\d+", "#define LOCAL_X " + localSizeX);
        source = source.replaceAll("#define\\s+LOCAL_Y\\s+\\d+", "#define LOCAL_Y " + localSizeY);
        source = source.replaceAll("#define\\s+LOCAL_Z\\s+\\d+", "#define LOCAL_Z " + localSizeZ);
        return source;
    }
}
