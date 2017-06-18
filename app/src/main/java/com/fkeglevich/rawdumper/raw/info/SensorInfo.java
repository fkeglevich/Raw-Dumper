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

package com.fkeglevich.rawdumper.raw.info;

import com.fkeglevich.rawdumper.raw.data.BayerPattern;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Created by Flávio Keglevich on 11/06/2017.
 * TODO: Add a class header comment!
 */

public class SensorInfo
{
    private static final short[] DEFAULT_CFA_REPEAT_PATTERN_DIM = new short[] {2, 2};
    private static final short[] DEFAULT_BLACK_LEVEL_REPEAT_DIM = new short[] {2, 2};

    private int horizontalPadding;
    private int verticalPadding;

    private int alignWidth;

    private int bitsPerPixel;
    private int storageBitsPerPixel;

    private String name;
    private String maker;

    private boolean supportsPixelBinning;

    private RawImageSize[] rawImageSizes;
    private RawImageSize[] binningRawImageSizes;

    private BayerPattern bayerPattern;

    private int whiteLevel;
    private float[] blackLevel;

    private int baseISO;

    private SensorInfo()
    {   }

    public RawImageSize[] getRawImageSizes()
    {
        return rawImageSizes;
    }

    public RawImageSize[] getBinningRawImageSizes()
    {
        return binningRawImageSizes;
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_BITSPERSAMPLE,          storageBitsPerPixel);
        tiffWriter.setField(TiffTag.TIFFTAG_CFAREPEATPATTERNDIM,    DEFAULT_CFA_REPEAT_PATTERN_DIM, false);
        tiffWriter.setField(TiffTag.TIFFTAG_CFAPATTERN,             bayerPattern.getBytePattern(), false);
        tiffWriter.setField(TiffTag.TIFFTAG_WHITELEVEL,             new long[] { whiteLevel }, true);
        tiffWriter.setField(TiffTag.TIFFTAG_BLACKLEVELREPEATDIM,    DEFAULT_BLACK_LEVEL_REPEAT_DIM, false);
        tiffWriter.setField(TiffTag.TIFFTAG_BLACKLEVEL,             blackLevel, true);
    }
}
