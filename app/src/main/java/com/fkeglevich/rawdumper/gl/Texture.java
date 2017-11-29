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

/**
 * Created by flavio on 25/11/17.
 */

public class Texture
{
    private final int handle;
    private final int type;

    public static Texture create(int type)
    {
        int[] handles = new int[1];
        GLES20.glGenTextures(1, handles, 0);
        return new Texture(handles[0], type);
    }

    private Texture(int handle, int type)
    {
        this.handle = handle;
        this.type = type;
        init();
    }

    private void init()
    {
        GLES20.glBindTexture(type, handle);
    }

    public int getHandle()
    {
        return handle;
    }

    public int getType()
    {
        return type;
    }

    public void setHorizontalWrapMode(int mode)
    {
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_WRAP_S, mode);
    }

    public void setVerticalWrapMode(int mode)
    {
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_WRAP_T, mode);
    }

    public void setMinFilter(int filter)
    {
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_MIN_FILTER, filter);
    }

    public void setMagFilter(int filter)
    {
        GLES20.glTexParameteri(type, GLES20.GL_TEXTURE_MAG_FILTER, filter);
    }
}
