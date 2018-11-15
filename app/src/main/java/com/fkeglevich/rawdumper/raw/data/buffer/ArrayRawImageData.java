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

import java.io.IOException;

/**
 * Represents the data of a raw picture that originated from a byte array.
 *
 * Created by Flávio Keglevich on 26/08/2017.
 */

public class ArrayRawImageData extends RawImageData
{
    private final byte[] array;

    public ArrayRawImageData(RawImageSize size, byte[] array)
    {
        super(size);
        this.array = array;
    }

    public ArrayRawImageData(RawImageSize size)
    {
        this(size, size.buildValidRowBuffer());
    }

    @Override
    public void copyValidRowToBuffer(int row, byte[] buffer, int start)
    {
        System.arraycopy(array, calculatePositionFromRow(row), buffer, start, getSize().getPaddedWidthBytes());
    }

    @Override
    public void copyAllImageDataToBuffer(byte[] buffer, int start)
    {
        System.arraycopy(array, getOffset(), buffer, start, getSize().getBufferLength());
    }

    @Override
    public byte[] getLowLevelBuffer()
    {
        return array;
    }

    /**
     * Closes the object and release any system resources it holds.
     * <p>
     * <p>Although only the first call has any effect, it is safe to call close
     * multiple times on the same object. This is more lenient than the
     * overridden {@code AutoCloseable.close()}, which may be called at most
     * once.
     */
    @Override
    public void close() throws IOException
    {
        //no op
    }
}
