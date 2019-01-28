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

import static com.fkeglevich.rawdumper.util.MathUtil.invert3x3Matrix;
import static com.fkeglevich.rawdumper.util.MathUtil.multiply3x3MatrixToVector3;

/**
 * Created by Flávio Keglevich on 08/06/2017.
 * TODO: Add a class header comment!
 */

public class ColorUtil
{
    private static final int MAX_PASSES = 30;

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

    public static XYCoords neutralToXY(double[] neutral, ColorInfo colorInfo)
    {
        XYCoords last = XYCoords.D50;

        for (int pass = 0; pass < MAX_PASSES; pass++)
        {
            double[] xyzToCamera = colorInfo.interpolatedColorMatrix(last.toColorTemperature().getTemperature());
            double[] CC = colorInfo.interpolatedCalibrationMatrix(last.toColorTemperature().getTemperature());

            xyzToCamera = MathUtil.multiply3x3Matrices(CC, xyzToCamera);

            XYCoords next = XYCoords.fromXYZ(multiply3x3MatrixToVector3(invert3x3Matrix(xyzToCamera), neutral));

            if ((Math.abs(next.getX() - last.getX()) + Math.abs(next.getY() - last.getY())) < 0.0000001)
                return next;

            if (pass == (MAX_PASSES - 1))
                return new XYCoords((last.getX() + next.getX()) * 0.5,
                                    (last.getY() + next.getY()) * 0.5);
        }
        return last;
    }

    public static ColorTemperature neutralToColorTemperature(double[] neutral, ColorInfo colorInfo)
    {
        return neutralToXY(neutral, colorInfo).toColorTemperature();
    }
}
