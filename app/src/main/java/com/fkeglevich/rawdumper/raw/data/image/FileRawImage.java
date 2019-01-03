/*
 * Copyright 2018, Flávio Keglevich
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

package com.fkeglevich.rawdumper.raw.data.image;

import com.fkeglevich.rawdumper.io.async.BufferManager;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuFileInputStream;

import java.io.File;
import java.io.IOException;

public class FileRawImage implements RawImage
{
    private final SuFile file;
    private final RawImageSize size;

    private byte[] data;
    private byte[] mkn;

    public FileRawImage(File file, RawImageSize size) throws IOException
    {
        this.file = new SuFile(file);
        this.size = size;
        this.data = initData(size.getBufferLength());
        this.mkn = new byte[0];
        initialize();
    }

    @Override
    public RawImageSize getSize()
    {
        return size;
    }

    @Override
    public byte[] getData()
    {
        return data;
    }

    @Override
    public void dispose()
    {
        BufferManager.getInstance().sendBuffer(data);
        data = null;
    }

    public byte[] getMakerNotes()
    {
        return mkn;
    }

    public SuFile getFile()
    {
        return file;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initialize() throws IOException
    {
        int fileSize = (int) file.length();
        mkn = new byte[fileSize - size.getBufferLength()];

        try(SuFileInputStream is = new SuFileInputStream(file))
        {
            is.read(mkn);
            is.read(data, 0, size.getBufferLength());
        }

        file.delete();
    }

    private byte[] initData(int bufferLength)
    {
        return BufferManager.getInstance().getBuffer(bufferLength);
    }
}
