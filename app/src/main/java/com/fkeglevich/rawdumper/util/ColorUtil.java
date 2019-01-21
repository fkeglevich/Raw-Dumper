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

import com.fkeglevich.rawdumper.raw.color.ColorTemperature;
import com.fkeglevich.rawdumper.raw.color.XYCoords;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;

import static com.fkeglevich.rawdumper.util.MathUtil.scalarMultiply;
import static com.fkeglevich.rawdumper.util.MathUtil.sum;

/**
 * Created by Flávio Keglevich on 08/06/2017.
 * TODO: Add a class header comment!
 */

public class ColorUtil
{
    public static float[] getXYFromCCT(double temperature, ColorInfo colorInfo)
    {
        XYCoords xyCoords = Temperature.getXYFromColorTemperature(new ColorTemperature(temperature, getTintForTemperature(temperature, colorInfo)));

        return new float[] {(float) xyCoords.getX(), (float) xyCoords.getY()};
    }

    private static double getTintForTemperature(double temperature, ColorInfo colorInfo)
    {
        double a = colorInfo.getTintTemperatureFunction()[0];
        double b = colorInfo.getTintTemperatureFunction()[1];
        return temperature * a + b;
    }
    
    public static double[] interpolatedColorMatrix(double temperatureTarget,
                                                   double[] colorMatrix1, double[] colorMatrix2,
                                                   double temperature1, double temperature2)
    {
        if (temperature2 > temperature1)
        {
            double aux1 = temperature1;
            temperature1 = temperature2;
            temperature2 = aux1;

            double[] aux2 = colorMatrix1;
            colorMatrix1 = colorMatrix2;
            colorMatrix2 = aux2;
        }

        if (temperatureTarget <= temperature1)
            return colorMatrix1;
        if (temperatureTarget >= temperature2)
            return colorMatrix2;

        double invertedTarget = 1.0 / temperatureTarget;
        double inverted1      = 1.0 / temperature1;
        double inverted2      = 1.0 / temperature2;

        double linear = (invertedTarget - inverted2) / (inverted1 - inverted2);

        return sum(
                                scalarMultiply(colorMatrix1, linear),
                                scalarMultiply(colorMatrix2, 1.0 - linear));
    }
}
