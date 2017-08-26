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
import java.io.RandomAccessFile;

/**
 * Created by Flávio Keglevich on 26/08/2017.
 * TODO: Add a class header comment!
 */

public class FileRawImageData extends RawImageData
{
    private final RandomAccessFile file;

    public FileRawImageData(RawImageSize size, RandomAccessFile file) throws IOException
    {
        super(size);
        this.file = file;
        setOffset((int)file.getFilePointer());
    }

    @Override
    public void copyValidRowToBuffer(int row, byte[] buffer, int start) throws IOException
    {
        file.seek(calculatePositionFromRow(row));
        file.read(buffer);
    }

    @Override
    public void copyAllImageDataToBuffer(byte[] buffer, int start) throws IOException
    {
        file.seek(getOffset());
        file.read(buffer, start, getSize().getBufferLength());
    }
}
