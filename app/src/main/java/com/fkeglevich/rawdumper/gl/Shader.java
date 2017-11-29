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

/**
 * Represents an OpenGL ES shader
 *
 * Created by Flávio Keglevich on 24/11/17.
 */

public class Shader
{
    private final int handle;
    private final ShaderType type;

    public static Shader create(ShaderType type) throws GLException
    {
        int handle = GLES20.glCreateShader(type.getGlShaderType());
        if (handle != 0)
            return new Shader(handle, type);

        throw new GLException("Error creating shader!");
    }

    protected Shader(int handle, ShaderType type)
    {
        this.handle = handle;
        this.type = type;
    }

    public ShaderType getType()
    {
        return type;
    }

    public void compile(String code) throws GLException
    {
        GLES20.glShaderSource(getHandle(), code);
        GLES20.glCompileShader(getHandle());

        if (BuildConfig.DEBUG)
        {
            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(getHandle(), GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == GLES20.GL_FALSE)
            {
                String message = GLES20.glGetShaderInfoLog(getHandle());
                delete();
                throw new GLException(message);
            }
        }
    }

    public void delete()
    {
        GLES20.glDeleteShader(getHandle());
    }

    public int getHandle()
    {
        return handle;
    }
}
