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

package com.fkeglevich.rawdumper.dng.writer;

import com.fkeglevich.rawdumper.dng.ADngImageWriter;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Writes an uncompressed image to a DNG file in scanline mode.
 *
 * Created by Flávio Keglevich on 17/04/2017.
 */

public class ScanlineImageWriter extends ADngImageWriter
{
    private byte[] buffer;

    public ScanlineImageWriter()
    {   }

    @Override
    protected void init(TiffWriter tiffWriter, RawImageSize rawImageSize)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_COMPRESSION, TiffTag.COMPRESSION_NONE);
        buffer = new byte[rawImageSize.getPaddedWidthBytes()];
    }

    @Override
    protected void writeImageData(TiffWriter tiffWriter, RawImageSize rawImageSize, byte[] rawdata)
    {
        init(tiffWriter, rawImageSize);
        for (int row = 0; row < rawImageSize.getPaddedHeight(); row++)
        {
            System.arraycopy(rawdata, rawImageSize.getBytesPerLine() * row, buffer, 0, rawImageSize.getPaddedWidthBytes());
            tiffWriter.writeScanline(buffer, row);
        }
        tiffWriter.writeDirectory();
    }

    @Override
    protected void writeImageData(TiffWriter tiffWriter, RawImageSize rawImageSize, RandomAccessFile file) throws IOException
    {
        init(tiffWriter, rawImageSize);

        long current = file.getFilePointer();
        for (int row = 0; row < rawImageSize.getPaddedHeight(); row++)
        {
            file.seek(current + rawImageSize.getBytesPerLine() * row);
            file.read(buffer);
            tiffWriter.writeScanline(buffer, row);
        }
        tiffWriter.writeDirectory();
    }
}