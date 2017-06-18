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

    public static SensorInfo createT4K37()
    {
        SensorInfo result = new SensorInfo();
        result.horizontalPadding = 16;
        result.verticalPadding = 16;
        result.alignWidth = 256;
        result.bitsPerPixel = 10;
        result.storageBitsPerPixel = 16;
        result.name = "T4K37";
        result.maker = "Toshiba";
        result.supportsPixelBinning = true;
        result.bayerPattern = BayerPattern.GRBG;
        result.whiteLevel = 1023;
        result.blackLevel = new float[]{64, 64, 64, 64};
        result.baseISO = 50;

        int[] widths  = {2048, 4096, 4096};
        int[] heights = {1536, 2304, 3072};

        result.rawImageSizes = getRawImageSizes(widths, heights, result);

        int[] bwidths  = {2048};
        int[] bheights = {1536};

        result.binningRawImageSizes = getRawImageSizes(bwidths, bheights, result);

        return result;
    }

    public static SensorInfo createOV5670()
    {
        SensorInfo result = new SensorInfo();
        result.horizontalPadding = 32;
        result.verticalPadding = 24;
        result.alignWidth = 256;
        result.bitsPerPixel = 10;
        result.storageBitsPerPixel = 16;
        result.name = "OV5670";
        result.maker = "OmniVision";
        result.supportsPixelBinning = false;
        result.bayerPattern = BayerPattern.BGGR;
        result.whiteLevel = 1023;
        result.blackLevel = new float[]{16, 16, 16, 16};
        result.baseISO = 50;

        int[] widths  = {2560};
        int[] heights = {1920};

        result.rawImageSizes = getRawImageSizes(widths, heights, result);
        result.binningRawImageSizes = new RawImageSize[]{};

        return result;
    }

    private static RawImageSize[] getRawImageSizes(int[] widths, int[] heights, SensorInfo info)
    {
        RawImageSize[] result = new RawImageSize[widths.length];

        for (int i = 0; i < result.length; i++)
            result[i] = new RawImageSize(widths[i], heights[i], info.alignWidth, info.horizontalPadding, info.verticalPadding, info.storageBitsPerPixel / 8);

        return result;
    }
}
