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

package com.fkeglevich.rawdumper.gl;

import android.opengl.GLES31;
import android.os.Build;

import com.fkeglevich.rawdumper.gl.compute.ShaderSourceUtil;
import com.fkeglevich.rawdumper.gl.exception.GLException;

import androidx.annotation.RequiresApi;

public class ComputeShader extends Shader
{
    public static ComputeShader create() throws GLException
    {
        Shader shader = Shader.create(ShaderType.COMPUTE);
        return new ComputeShader(shader.getHandle(), shader.getType());
    }

    private ComputeShader(int handle, ShaderType type)
    {
        super(handle, type);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dispatch(int numGroupsX, int numGroupsY, int numGroupsZ)
    {
        GLES31.glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void memoryBarrier(int barriers)
    {
        GLES31.glMemoryBarrier(barriers);
    }

    public void compile(String source, int localSizeX, int localSizeY, int localSizeZ) throws GLException
    {
        compile(ShaderSourceUtil.replaceLocalSize(source, localSizeX, localSizeY, localSizeZ));
    }
}
