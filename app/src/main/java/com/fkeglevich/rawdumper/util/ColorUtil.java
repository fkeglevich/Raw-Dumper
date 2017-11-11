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

package com.fkeglevich.rawdumper.util;

import com.fkeglevich.rawdumper.raw.info.ColorInfo;

/**
 * Created by Flávio Keglevich on 08/06/2017.
 * TODO: Add a class header comment!
 */

public class ColorUtil
{
    public static float[] getXYFromCCT(double temperature, ColorInfo colorInfo)
    {
        return Temperature.getXYFromCCTAndTint(temperature, getTintForTemperature(temperature, colorInfo));
    }

    private static double getTintForTemperature(double temperature, ColorInfo colorInfo)
    {
        double a = colorInfo.getTintTemperatureFunction()[0];
        double b = colorInfo.getTintTemperatureFunction()[1];
        return temperature * a + b;
    }
}
