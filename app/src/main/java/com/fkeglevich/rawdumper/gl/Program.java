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

import android.opengl.GLES20;

import com.fkeglevich.rawdumper.BuildConfig;
import com.fkeglevich.rawdumper.gl.exception.GLException;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an OpenGL ES program
 *
 * Created by Flávio Keglevich on 24/11/17.
 */

public class Program
{
    private final int handle;

    private final Map<String, Integer> uniformCache = new HashMap<>();
    private final Map<String, Integer> attribCache = new HashMap<>();

    public static Program create() throws GLException
    {
        int handle = GLES20.glCreateProgram();
        if (handle != 0)
            return new Program(handle);

        throw new GLException("Error creating program!");
    }

    protected Program(int handle)
    {
        this.handle = handle;
    }

    public void attachShader(Shader shader)
    {
        GLES20.glAttachShader(getHandle(), shader.getHandle());
    }

    public void detachShader(Shader shader)
    {
        GLES20.glDetachShader(getHandle(), shader.getHandle());
    }

    public void link() throws GLException
    {
        GLES20.glLinkProgram(getHandle());

        if (BuildConfig.DEBUG)
        {
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(getHandle(), GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == GLES20.GL_FALSE)
            {
                String message = GLES20.glGetProgramInfoLog(getHandle());
                delete();
                throw new GLException(message);
            }
        }
    }

    public void use()
    {
        GLES20.glUseProgram(getHandle());
    }

    public void delete()
    {
        GLES20.glDeleteProgram(getHandle());
    }

    public int getHandle()
    {
        return handle;
    }

    public void setUniformFloat(String name, float data)
    {
        GLES20.glUniform1f(getUniformHandle(name), data);
    }

    public void setUniformVec2(String name, float x, float y)
    {
        GLES20.glUniform2f(getUniformHandle(name), x, y);
    }

    public void setUniformVec2(String name, float[] data)
    {
        GLES20.glUniform2fv(getUniformHandle(name), 1, data, 0);
    }

    public void setUniformMat4(String name, float[] data)
    {
        setUniformMat4(name, data, false);
    }

    public void setUniformMat4(String name, float[] data, boolean transpose)
    {
        GLES20.glUniformMatrix4fv(getUniformHandle(name), 1, transpose, data, 0);
    }

    @SuppressWarnings("ConstantConditions")
    protected int getUniformHandle(String name)
    {
        if (uniformCache.containsKey(name))
            return uniformCache.get(name);

        int result = GLES20.glGetUniformLocation(handle, name);

        uniformCache.put(name, result);
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    protected int getAttribHandle(String name)
    {
        if (attribCache.containsKey(name))
            return attribCache.get(name);

        int result = GLES20.glGetAttribLocation(handle, name);

        attribCache.put(name, result);
        return result;
    }
}
