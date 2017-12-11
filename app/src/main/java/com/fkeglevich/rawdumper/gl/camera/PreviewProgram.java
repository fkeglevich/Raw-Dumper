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

package com.fkeglevich.rawdumper.gl.camera;

import android.opengl.GLES20;

import com.fkeglevich.rawdumper.gl.Program;
import com.fkeglevich.rawdumper.gl.exception.GLException;
import com.fkeglevich.rawdumper.gl.exception.GLUncheckedException;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by flavio on 26/11/17.
 */

public class PreviewProgram extends Program
{
    private float[] surfaceMatrixCache = new float[16];
    private float[] surfaceSizeCache   = new float[2];
    private float[] previewScaleCache  = new float[2];

    public static PreviewProgram create() throws GLException
    {
        return new PreviewProgram(Program.create().getHandle());
    }

    public static PreviewProgram create(Program program)
    {
        return new PreviewProgram(program.getHandle());
    }

    PreviewProgram(int handle)
    {
        super(handle);
    }

    void setSurfaceMatrix(float[] surfaceMatrix)
    {
        if (Arrays.equals(surfaceMatrix, surfaceMatrixCache)) return;

        int handle = getUniformHandle("surfaceMatrix");
        if (handle != -1)
        {
            GLES20.glUniformMatrix4fv(handle, 1, false, surfaceMatrix, 0);
            System.arraycopy(surfaceMatrix, 0, surfaceMatrixCache, 0, surfaceMatrix.length);
        }
    }

    void setSurfaceSize(float[] surfaceSize)
    {
        if (Arrays.equals(surfaceSize, surfaceSizeCache)) return;

        int handle = getUniformHandle("surfaceSize");
        if (handle != -1)
        {
            GLES20.glUniform2fv(handle, 1, surfaceSize, 0);
            System.arraycopy(surfaceSize, 0, surfaceSizeCache, 0, surfaceSize.length);
        }
    }

    void setPreviewScale(float[] previewScale)
    {
        if (Arrays.equals(previewScale, previewScaleCache)) return;

        int handle = getUniformHandle("previewScale");
        if (handle != -1)
        {
            GLES20.glUniform2fv(handle, 1, previewScale, 0);
            System.arraycopy(previewScale, 0, previewScaleCache, 0, previewScale.length);
        }
    }

    void setupVertices(ByteBuffer vertexBuffer)
    {
        int vertexPositionHandle = getAttribHandle("vertexPosition");
        if (vertexPositionHandle == -1)
            throw new GLUncheckedException("The attrib 'vertexPosition' wasn't found!");

        GLES20.glVertexAttribPointer(vertexPositionHandle, 2, GLES20.GL_BYTE, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(vertexPositionHandle);
    }
}
