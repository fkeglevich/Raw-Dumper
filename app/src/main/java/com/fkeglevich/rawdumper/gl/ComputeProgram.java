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
import android.util.Log;

import com.fkeglevich.rawdumper.gl.compute.ShaderSourceUtil;
import com.fkeglevich.rawdumper.gl.exception.GLException;

import androidx.annotation.RequiresApi;

public class ComputeProgram extends Program
{
    private final int localSizeX;
    private final int localSizeY;
    private final int localSizeZ;

    public static ComputeProgram create(String computeShaderCode, int localSizeX, int localSizeY, int localSizeZ) throws GLException
    {
        String source = ShaderSourceUtil.replaceLocalSize(computeShaderCode, localSizeX, localSizeY, localSizeZ);
        //Log.i("ASD", source);

        Shader computeShader = Shader.create(ShaderType.COMPUTE);
        computeShader.compile(source);

        Program program = Program.create();
        program.attachShader(computeShader);
        program.link();

        return new ComputeProgram(program.getHandle(), localSizeX, localSizeY, localSizeZ);
    }

    private ComputeProgram(int handle, int localSizeX, int localSizeY, int localSizeZ)
    {
        super(handle);
        this.localSizeX = localSizeX;
        this.localSizeY = localSizeY;
        this.localSizeZ = localSizeZ;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dispatch1D(int dataSizeX)
    {
        GLES31.glDispatchCompute(
                (int) Math.ceil(((double) dataSizeX) / ((double) localSizeX)), 1, 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dispatch2D(int dataSizeX, int dataSizeY)
    {
        GLES31.glDispatchCompute(
                (int) Math.ceil(((double) dataSizeX) / ((double) localSizeX)),
                (int) Math.ceil(((double) dataSizeY) / ((double) localSizeY)), 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void dispatch3D(int dataSizeX, int dataSizeY, int dataSizeZ)
    {
        GLES31.glDispatchCompute(
                (int) Math.ceil(((double) dataSizeX) / ((double) localSizeX)),
                (int) Math.ceil(((double) dataSizeY) / ((double) localSizeY)),
                (int) Math.ceil(((double) dataSizeZ) / ((double) localSizeZ)));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void memoryBarrier(int barriers)
    {
        GLES31.glMemoryBarrier(barriers);
    }

    public int getLocalSizeX()
    {
        return localSizeX;
    }

    public int getLocalSizeY()
    {
        return localSizeY;
    }

    public int getLocalSizeZ()
    {
        return localSizeZ;
    }
}
