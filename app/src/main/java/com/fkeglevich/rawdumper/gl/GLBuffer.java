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

import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import static android.opengl.GLES30.GL_MAP_WRITE_BIT;

public class GLBuffer
{
    private final int handle;
    private final BufferType type;

    public static GLBuffer create(BufferType bufferType)
    {
        int[] handles = new int[1];
        GLES20.glGenBuffers(1, handles, 0);
        return new GLBuffer(handles[0], bufferType);
    }

    private GLBuffer(int handle, BufferType type)
    {
        this.handle = handle;
        this.type = type;
    }

    public void bind()
    {
        GLES20.glBindBuffer(type.getBufferType(), handle);
    }

    public void setData(byte[] data, BufferUsage usage)
    {
        setData(data, 0, data.length, usage);
    }

    public void setData(byte[] data, int offset, int length, BufferUsage usage)
    {
        setData(ByteBuffer.wrap(data, offset, length), usage);
    }

    public void setData(ByteBuffer data, BufferUsage usage)
    {
        setData(data, data.remaining(), usage);
    }

    public void setData(Buffer data, int size, BufferUsage usage)
    {
        bind();
        GLES20.glBufferData(type.getBufferType(), size, data, usage.getBufferUsage());
    }

    public void setDataAsMapping(byte[] data, int accessFlags)
    {
        bind();
        ByteBuffer buffer = (ByteBuffer) GLES30.glMapBufferRange(type.getBufferType(),
                0, data.length, GL_MAP_WRITE_BIT | accessFlags);
        buffer.put(data);
        GLES30.glUnmapBuffer(type.getBufferType());
    }

    public void setDataAsMapping(ByteBuffer data, int accessFlags)
    {
        bind();
        ByteBuffer buffer = (ByteBuffer) GLES30.glMapBufferRange(type.getBufferType(),
                0, data.remaining(), GL_MAP_WRITE_BIT | accessFlags);

        buffer.put(data);
        GLES30.glUnmapBuffer(type.getBufferType());
    }

    public void bindBase(int index)
    {
        GLES30.glBindBufferBase(type.getBufferType(), index, handle);
    }

    private void delete()
    {
        GLES20.glDeleteBuffers(1, new int[] { handle }, 0);
    }
}
