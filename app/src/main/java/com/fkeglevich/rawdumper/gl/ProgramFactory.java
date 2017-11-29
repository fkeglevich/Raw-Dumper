/*
 * Copyright 2017, Flávio Keglevich
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

package com.fkeglevich.rawdumper.gl;

import com.fkeglevich.rawdumper.gl.exception.GLException;
import com.fkeglevich.rawdumper.util.AssetUtil;

import java.io.IOException;

/**
 * A factory class for creating OpenGL ES programs
 *
 * Created by Flávio Keglevich on 25/11/17.
 */

public class ProgramFactory
{
    public static Program createFromAssets(String vertexShaderAsset, String fragmentShaderAsset) throws GLException, IOException
    {
        return create(AssetUtil.getAssetAsString(vertexShaderAsset), AssetUtil.getAssetAsString(fragmentShaderAsset));
    }

    public static Program create(String vertexShaderCode, String fragmentShaderCode) throws GLException
    {
        Shader vertexShader = Shader.create(ShaderType.VERTEX);
        vertexShader.compile(vertexShaderCode);

        Shader fragmentShader = Shader.create(ShaderType.FRAGMENT);
        fragmentShader.compile(fragmentShaderCode);

        return create(vertexShader, fragmentShader);
    }

    public static Program create(Shader vertexShader, Shader fragmentShader) throws GLException
    {
        Program program = Program.create();
        program.attachShader(vertexShader);
        program.attachShader(fragmentShader);
        program.link();

        return program;
    }
}
