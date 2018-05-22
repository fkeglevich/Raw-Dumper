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
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 12/11/17.
 */

public class StripImageWriter extends ADngImageWriter
{
    private byte[] buffer;

    @Override
    protected void init(TiffWriter tiffWriter, RawImageSize rawImageSize)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_COMPRESSION, TiffTag.COMPRESSION_NONE);
        tiffWriter.setField(TiffTag.TIFFTAG_ROWSPERSTRIP, 1L);
        buffer = rawImageSize.buildValidRowBuffer();
    }

    @Override
    protected void writeImageData(TiffWriter tiffWriter, RawImageData imageData, boolean invertRows) throws IOException
    {
        init(tiffWriter, imageData.getSize());
        int paddedHeight = imageData.getSize().getPaddedHeight();

        for (int row = 0; row < paddedHeight; row++)
        {
            imageData.copyValidRowToBuffer(invertRows ? (paddedHeight - 1 - row): row, buffer);
            tiffWriter.writeRawStrip(row, buffer, buffer.length);
        }

        tiffWriter.writeDirectory();
    }
}
