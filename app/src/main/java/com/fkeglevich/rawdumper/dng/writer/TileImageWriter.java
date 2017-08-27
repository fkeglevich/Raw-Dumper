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

import android.util.Log;

import com.fkeglevich.rawdumper.dng.ADngImageWriter;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Flávio Keglevich on 27/08/2017.
 * TODO: Add a class header comment!
 */

public class TileImageWriter extends ADngImageWriter
{
    private static final double DIMENSION_MULTIPLE = 16.0;
    private static final int FIRST_TILE_ID = 0;

    private int tileWidth;
    private int tileHeight;

    public TileImageWriter()
    {   }

    @Override
    protected void init(TiffWriter tiffWriter, RawImageSize rawImageSize)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_COMPRESSION, TiffTag.COMPRESSION_NONE);

        tileWidth   = calculateTileDimension(rawImageSize.getPaddedWidth());
        tileHeight  = calculateTileDimension(rawImageSize.getPaddedHeight());

        tiffWriter.setField(TiffTag.TIFFTAG_TILEWIDTH, (long)tileWidth);
        tiffWriter.setField(TiffTag.TIFFTAG_TILELENGTH, (long)tileHeight);
    }

    @Override
    protected void writeImageData(TiffWriter tiffWriter, RawImageData imageData) throws IOException
    {
        init(tiffWriter, imageData.getSize());

        int bytesPerPixel = imageData.getSize().getBytesPerPixel();
        byte[] buffer = new byte[tileWidth * tileHeight * bytesPerPixel];

        int paddedHeight = imageData.getSize().getPaddedHeight();
        int tileBytesPerLine = tileWidth * bytesPerPixel;

        for (int row = 0; row < paddedHeight; row++)
            imageData.copyValidRowToBuffer(row, buffer, tileBytesPerLine * row);

        tiffWriter.writeRawTile(FIRST_TILE_ID, buffer, buffer.length);
        tiffWriter.writeDirectory();
    }

    private int calculateTileDimension(int dimension)
    {
        return (int) (Math.ceil(dimension / DIMENSION_MULTIPLE) * DIMENSION_MULTIPLE);
    }
}
