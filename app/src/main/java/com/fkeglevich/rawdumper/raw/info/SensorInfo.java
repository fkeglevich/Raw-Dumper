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

import android.hardware.Camera;
import android.support.annotation.Keep;

import com.fkeglevich.rawdumper.dng.dngsdk.DngNegative;
import com.fkeglevich.rawdumper.raw.capture.ExifInfo;
import com.fkeglevich.rawdumper.raw.data.BayerPattern;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;

/**
 * Stores specific information and implementation details
 * about the camera sensor.
 *
 * Created by Flávio Keglevich on 11/06/2017.
 */

@Keep
@SuppressWarnings("unused")
public class SensorInfo
{
    private static final short[] DEFAULT_CFA_REPEAT_PATTERN_DIM = new short[] {2, 2};
    private static final short[] DEFAULT_BLACK_LEVEL_REPEAT_DIM = new short[] {2, 2};
    private static final byte[]  DEFAULT_CFA_PLANE_COLOR        = new byte[]  {0, 1, 2};
    private static final short   DEFAULT_CFA_LAYOUT             = 1;

    private int bitsPerPixel;
    private int storageBitsPerPixel;

    private String name;
    private String maker;

    private RawImageSize[] rawImageSizes;
    private RawImageSize[] binningRawImageSizes;

    private BayerPattern bayerPattern;

    private int whiteLevel;
    BlackLevelInfo blackLevelInfo;

    private int baseISO;
    private Integer integrationTimeScale;

    public RawImageSize[] getRawImageSizes()
    {
        return rawImageSizes;
    }

    public RawImageSize[] getBinningRawImageSizes()
    {
        return binningRawImageSizes;
    }

    public int getBaseISO()
    {
        return baseISO;
    }

    public Integer getIntegrationTimeScale()
    {
        return integrationTimeScale;
    }

    public void writeInfoTo(DngNegative negative, ExifInfo exifInfo, boolean invertRows)
    {
        negative.setSensorInfo(whiteLevel, blackLevelInfo.computeBlackLevel(exifInfo), invertRows ? bayerPattern.invertVertically().getPhase() : bayerPattern.getPhase());
    }

    public RawImageSize getRawImageSizeFromSize(Camera.Size size)
    {
        for (RawImageSize rawImageSize : getRawImageSizes())
            if (rawImageSize.getWidth() == size.width && rawImageSize.getHeight() == size.height)
                return rawImageSize;

        return null;
    }

    public void disableRaw()
    {
        rawImageSizes = new RawImageSize[0];
        binningRawImageSizes = new RawImageSize[0];
    }

}
