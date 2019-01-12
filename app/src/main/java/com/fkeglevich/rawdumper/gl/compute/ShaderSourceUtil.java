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
        String[] split = source.split("[\\r\\n]+");
        StringBuilder buffer = new StringBuilder(source.length());

        boolean firstNonMacroLine = true;
        for (String line : split)
        {
            if (!line.startsWith("#") && firstNonMacroLine)
            {
                firstNonMacroLine = false;
                buffer.append("#define LOCAL_X ").append(localSizeX).append("\n")
                        .append("#define LOCAL_Y ").append(localSizeY).append("\n")
                        .append("#define LOCAL_Z ").append(localSizeZ).append("\n");
            }

            if (!line.startsWith("#define LOCAL_"))
                buffer.append(line).append("\n");
        }

        return buffer.toString();
    }
}
