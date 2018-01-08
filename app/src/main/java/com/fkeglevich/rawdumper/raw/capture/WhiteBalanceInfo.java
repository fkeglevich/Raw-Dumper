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

package com.fkeglevich.rawdumper.raw.capture;

import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;
import com.fkeglevich.rawdumper.util.ColorUtil;
import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class WhiteBalanceInfo
{
    private static final int DAYLIGHT_TEMPERATURE = 5503;

    private static WhiteBalanceInfo createFromXYCoords(double x, double y, ColorInfo colorInfo)
    {
        float[] neutralValues = MathUtil.doubleArrayToFloat(colorInfo.calculateSimpleAsShotNeutral(x, y));
        return new WhiteBalanceInfo(neutralValues);
    }

    public static WhiteBalanceInfo createFromMakerNote(MakerNoteInfo makerNoteInfo, ColorInfo colorInfo)
    {
        if (makerNoteInfo.wbCoordinatesXY != null)
            return createFromXYCoords(makerNoteInfo.wbCoordinatesXY[0], makerNoteInfo.wbCoordinatesXY[1], colorInfo);
        else
            return createFromDaylightTemperature(colorInfo);
    }

    public static WhiteBalanceInfo createFromDaylightTemperature(ColorInfo colorInfo)
    {
        float[] xy = ColorUtil.getXYFromCCT(DAYLIGHT_TEMPERATURE, colorInfo);
        return createFromXYCoords(xy[0], xy[1], colorInfo);
    }

    private final float[] asShotNeutral;

    private WhiteBalanceInfo(float[] asShotNeutral)
    {
        this.asShotNeutral = asShotNeutral;
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        tiffWriter.setField(TiffTag.TIFFTAG_ASSHOTNEUTRAL, asShotNeutral, true);
    }
}
