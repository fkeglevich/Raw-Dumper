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

import com.fkeglevich.rawdumper.raw.BayerPattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Flávio Keglevich on 18/01/2017.
 * TODO: Add a class header comment!
 */

public class OV5670 extends SensorInfo
{
    private static final int HORIZONTAL_PADDING = 32;
    private static final int VERTICAL_PADDING = 24;

    private static final int[] RAW_WIDTHS   = {1280, 2560};
    private static final int[] RAW_HEIGHTS  = {960, 1920};

    private static final int[] RAW_BINNING_WIDTHS   = {1280};
    private static final int[] RAW_BINNING_HEIGHTS  = {960};

    private static final int ALIGN_WIDTH = 256;

    private static final int NUM_BITS_PER_PIXEL = 16;
    private static final int REAL_NUM_BITS_PER_PIXEL = 10;
    private static final int NUM_BYTES_PER_PIXEL = 2;

    private static final String SENSOR_NAME = "OV5670";
    private static final String SENSOR_MAKER = "OmniVision";

    private static final double PIXEL_WIDTH = 1.12;
    private static final double PIXEL_HEIGHT = 1.12;

    private static final double SENSOR_SIZE_NUMERATOR = 1.0;
    private static final double SENSOR_SIZE_DENOMINATOR = 5.0;

    private static final BayerPattern BAYER_PATTERN = BayerPattern.BGGR;

    private static final int    WHITE_LEVEL = 1023;
    private static final int[]  BLACK_LEVEL = new int[]{16, 16, 16, 16};

    private static final OV5670 INSTANCE = new OV5670();

    public static OV5670 getInstance()
    {
        return INSTANCE;
    }

    private List<RawImageSize> rawImageSizes;
    private List<RawImageSize> binningRawImageSizes;

    private OV5670()
    {
        initRawImageSizes();
        initBinningRawImageSizes();
    }

    private void initRawImageSizes()
    {
        ArrayList<RawImageSize> sizes = new ArrayList<RawImageSize>();
        for (int i = 0; i < RAW_WIDTHS.length; i++)
        {
            sizes.add(new RawImageSize(RAW_WIDTHS[i], RAW_HEIGHTS[i], getAlignWidth(), getHorizontalPadding(), getVerticalPadding(), getNumOfBytesPerPixel()));
        }
        rawImageSizes = Collections.unmodifiableList(sizes);
    }

    private void initBinningRawImageSizes()
    {
        ArrayList<RawImageSize> sizes = new ArrayList<RawImageSize>();
        for (int i = 0; i < RAW_BINNING_WIDTHS.length; i++)
        {
            sizes.add(new RawImageSize(RAW_BINNING_WIDTHS[i], RAW_BINNING_HEIGHTS[i], getAlignWidth(), getHorizontalPadding(), getVerticalPadding(), getNumOfBytesPerPixel()));
        }
        binningRawImageSizes = Collections.unmodifiableList(sizes);
    }

    @Override
    public int getHorizontalPadding()
    {
        return HORIZONTAL_PADDING;
    }

    @Override
    public int getVerticalPadding()
    {
        return VERTICAL_PADDING;
    }

    @Override
    public int getAlignWidth()
    {
        return ALIGN_WIDTH;
    }

    @Override
    public int getNumOfBitsPerPixel()
    {
        return NUM_BITS_PER_PIXEL;
    }

    @Override
    public int getRealNumOfBitsPerPixel()
    {
        return REAL_NUM_BITS_PER_PIXEL;
    }

    @Override
    public int getNumOfBytesPerPixel()
    {
        return NUM_BYTES_PER_PIXEL;
    }

    @Override
    public String getSensorName()
    {
        return SENSOR_NAME;
    }

    @Override
    public String getSensorMaker()
    {
        return SENSOR_MAKER;
    }

    @Override
    public double getPixelWidth()
    {
        return PIXEL_WIDTH;
    }

    @Override
    public double getPixelHeight()
    {
        return PIXEL_HEIGHT;
    }

    @Override
    public double getSensorSizeNumerator()
    {
        return SENSOR_SIZE_NUMERATOR;
    }

    @Override
    public double getSensorSizeDenominator()
    {
        return SENSOR_SIZE_DENOMINATOR;
    }

    @Override
    public boolean supportsPixelBinning()
    {
        return false;
    }

    @Override
    public List<RawImageSize> getRawImageSizes()
    {
        return rawImageSizes;
    }

    @Override
    public List<RawImageSize> getBinningRawImageSizes()
    {
        return binningRawImageSizes;
    }

    @Override
    public BayerPattern getBayerPattern()
    {
        return BAYER_PATTERN;
    }

    @Override
    public int getWhiteLevel()
    {
        return WHITE_LEVEL;
    }

    @Override
    public int[] getBlackLevel()
    {
        return BLACK_LEVEL;
    }

}
