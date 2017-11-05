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

package com.fkeglevich.rawdumper.raw.data.buffer;

import com.fkeglevich.rawdumper.raw.data.RawImageSize;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents the data of a raw picture, abstracting the origin of the actual data
 *
 * Created by Flávio Keglevich on 26/08/2017.
 */

public abstract class RawImageData implements Closeable
{
    private final RawImageSize size;
    private int offset = 0;

    RawImageData(RawImageSize size)
    {
        this.size = size;
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int value)
    {
        this.offset = value;
    }

    public RawImageSize getSize()
    {
        return size;
    }

    public abstract void copyValidRowToBuffer(int row, byte[] buffer, int start) throws IOException;

    public void copyValidRowToBuffer(int row, byte[] buffer) throws IOException
    {
        copyValidRowToBuffer(row, buffer, 0);
    }

    public abstract void copyAllImageDataToBuffer(byte[] buffer, int start) throws IOException;

    public void copyAllImageDataToBuffer(byte[] buffer) throws IOException
    {
        copyAllImageDataToBuffer(buffer, 0);
    }

    int calculatePositionFromRow(int row)
    {
        return getOffset() + getSize().getBytesPerLine() * row;
    }
}
