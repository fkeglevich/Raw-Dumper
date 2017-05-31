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

import java.util.List;

/**
 * Created by Flávio Keglevich on 26/12/2016.
 * TODO: Add a class header comment!
 */

public abstract class SensorInfo
{
    abstract public int getHorizontalPadding();
    abstract public int getVerticalPadding();

    abstract public int getAlignWidth();

    abstract public int getNumOfBitsPerPixel();
    abstract public int getRealNumOfBitsPerPixel();
    public int getNumOfBytesPerPixel()
    {
        return getNumOfBitsPerPixel() / 8;
    }

    abstract public String getSensorName();
    abstract public String getSensorMaker();
    public String getFullName()
    {
        return getSensorMaker() + " " + getSensorName();
    }

    abstract public double getPixelWidth();
    abstract public double getPixelHeight();

    abstract public double getSensorSizeNumerator();
    abstract public double getSensorSizeDenominator();

    abstract public boolean supportsPixelBinning();

    abstract public List<RawImageSize> getRawImageSizes();
    abstract public List<RawImageSize> getBinningRawImageSizes();

    public boolean containsRawImageSize(RawImageSize size)
    {
        List<RawImageSize> availableSizes = getRawImageSizes();

        for (RawImageSize currSize : availableSizes)
            if (currSize.compareTo(size)) return true;

        return false;
    }

    abstract public BayerPattern getBayerPattern();

    abstract public int getWhiteLevel();

    abstract public int[] getBlackLevel();
}
