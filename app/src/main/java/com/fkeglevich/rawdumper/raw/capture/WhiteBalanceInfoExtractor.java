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
import com.fkeglevich.rawdumper.util.ColorUtil;
import com.fkeglevich.rawdumper.util.MathUtil;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class WhiteBalanceInfoExtractor
{
    private static final int DAYLIGHT_TEMPERATURE = 5503;

    public WhiteBalanceInfoExtractor()
    {   }

    public WhiteBalanceInfo extractFrom(double x, double y, ColorInfo colorInfo)
    {
        WhiteBalanceInfo result = new WhiteBalanceInfo();
        result.asShotNeutral = MathUtil.doubleArrayToFloat(colorInfo.calculateSimpleAsShotNeutral(x, y));
        return result;
    }

    public WhiteBalanceInfo extractFrom(MakerNoteInfo makerNoteInfo, ColorInfo colorInfo)
    {
        return extractFrom(makerNoteInfo.wbCoordinatesXY[0], makerNoteInfo.wbCoordinatesXY[1], colorInfo);
    }

    public WhiteBalanceInfo extractFrom(ColorInfo colorInfo)
    {
        float[] xy = ColorUtil.getXYFromCCT(DAYLIGHT_TEMPERATURE);
        return extractFrom(xy[0], xy[1], colorInfo);
    }
}
