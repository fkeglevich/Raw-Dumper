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

/**
 * Code and table for transforming CCT and tint to CIE xy coordinates taken from Adobe's DNG SDK.
 *
 * Modified and ported to java by Flávio Keglevich on 11/11/17.
 */

public class Temperature
{
    /*****************************************************************************/
    // Copyright 2006 Adobe Systems Incorporated
    // All Rights Reserved.
    //
    // NOTICE:  Adobe permits you to use, modify, and distribute this file in
    // accordance with the terms of the Adobe license agreement accompanying it.
    /*****************************************************************************/

    /* $Id: //mondo/dng_sdk_1_4/dng_sdk/source/dng_temperature.cpp#1 $ */
    /* $DateTime: 2012/05/30 13:28:51 $ */
    /* $Change: 832332 $ */
    /* $Author: tknoll $ */

    // Scale factor between distances in uv space to a more user friendly "tint" parameter.
    private static final double TINT_SCALE = -3000.0;

    // Table from Wyszecki & Stiles, "Color Science", second edition, page 228.
    private static final double[][] TEMP_TABLE =
            {
                    {   0, 0.18006, 0.26352, -0.24341 },
                    {  10, 0.18066, 0.26589, -0.25479 },
                    {  20, 0.18133, 0.26846, -0.26876 },
                    {  30, 0.18208, 0.27119, -0.28539 },
                    {  40, 0.18293, 0.27407, -0.30470 },
                    {  50, 0.18388, 0.27709, -0.32675 },
                    {  60, 0.18494, 0.28021, -0.35156 },
                    {  70, 0.18611, 0.28342, -0.37915 },
                    {  80, 0.18740, 0.28668, -0.40955 },
                    {  90, 0.18880, 0.28997, -0.44278 },
                    { 100, 0.19032, 0.29326, -0.47888 },
                    { 125, 0.19462, 0.30141, -0.58204 },
                    { 150, 0.19962, 0.30921, -0.70471 },
                    { 175, 0.20525, 0.31647, -0.84901 },
                    { 200, 0.21142, 0.32312, -1.0182 },
                    { 225, 0.21807, 0.32909, -1.2168 },
                    { 250, 0.22511, 0.33439, -1.4512 },
                    { 275, 0.23247, 0.33904, -1.7298 },
                    { 300, 0.24010, 0.34308, -2.0637 },
                    { 325, 0.24702, 0.34655, -2.4681 },
                    { 350, 0.25591, 0.34951, -2.9641 },
                    { 375, 0.26400, 0.35200, -3.5814 },
                    { 400, 0.27218, 0.35407, -4.3633 },
                    { 425, 0.28039, 0.35577, -5.3762 },
                    { 450, 0.28863, 0.35714, -6.7262 },
                    { 475, 0.29685, 0.35823, -8.5955 },
                    { 500, 0.30505, 0.35907, -11.324 },
                    { 525, 0.31320, 0.35968, -15.628 },
                    { 550, 0.32129, 0.36011, -23.325 },
                    { 575, 0.32931, 0.36038, -40.770 },
                    { 600, 0.33724, 0.36051, -116.45 }
            };

    public static XYCoords getXYFromColorTemperature(ColorTemperature colorTemperature)
    {
        // Result
        double x = 0, y = 0;

        // Find inverse temperature to use as index.
        double r = 1.0E6 / colorTemperature.getTemperature();

        // Convert tint to offset is uv space.
        double offset = colorTemperature.getTint() * (1.0 / TINT_SCALE);

        // Search for line pair containing coordinate.
        for (int index = 0; index <= 29; index++)
        {
            if (r < TEMP_TABLE[index + 1][0] || index == 29)
            {
                // Find relative weight of first line.
                double f = (TEMP_TABLE [index + 1][0] - r) /
                        (TEMP_TABLE [index + 1][0] - TEMP_TABLE [index][0]);

                // Interpolate the black body coordinates.
                double u = TEMP_TABLE [index    ][1] * f +
                        TEMP_TABLE [index + 1][1] * (1.0 - f);

                double v = TEMP_TABLE [index    ][2] * f +
                        TEMP_TABLE [index + 1][2] * (1.0 - f);

                // Find vectors along slope for each line.
                double uu1 = 1.0;
                double vv1 = TEMP_TABLE [index][3];

                double uu2 = 1.0;
                double vv2 = TEMP_TABLE [index + 1][3];

                double len1 = Math.sqrt (1.0 + vv1 * vv1);
                double len2 = Math.sqrt (1.0 + vv2 * vv2);

                uu1 /= len1;
                vv1 /= len1;

                uu2 /= len2;
                vv2 /= len2;

                // Find vector from black body point.
                double uu3 = uu1 * f + uu2 * (1.0 - f);
                double vv3 = vv1 * f + vv2 * (1.0 - f);

                double len3 = Math.sqrt (uu3 * uu3 + vv3 * vv3);

                uu3 /= len3;
                vv3 /= len3;

                // Adjust coordinate along this vector.
                u += uu3 * offset;
                v += vv3 * offset;

                // Convert to xy coordinates.
                x = 1.5 * u / (u - 4.0 * v + 2.0);
                y = v / (u - 4.0 * v + 2.0);
                break;
            }
        }
        return new XYCoords(x, y);
    }

    public static ColorTemperature getColorTemperatureFromXYCoords(XYCoords xy)
    {
        double temperature = 0, tint = 0;

        // Convert to uv space.
        double u = 2.0 * xy.getX() / (1.5 - xy.getX() + 6.0 * xy.getY());
        double v = 3.0 * xy.getY() / (1.5 - xy.getX() + 6.0 * xy.getY());

        // Search for line pair coordinate is between.
        double last_dt = 0.0;

        double last_dv = 0.0;
        double last_du = 0.0;

        for (int index = 1; index <= 30; index++)
        {
            // Convert slope to delta-u and delta-v, with length 1.
            double du = 1.0;
            double dv = TEMP_TABLE [index][3];

            double len = Math.sqrt(1.0 + dv * dv);

            du /= len;
            dv /= len;

            // Find delta from black body point to test coordinate.
            double uu = u - TEMP_TABLE [index][1];
            double vv = v - TEMP_TABLE [index][2];

            // Find distance above or below line.
            double dt = - uu * dv + vv * du;

            // If below line, we have found line pair.
            if (dt <= 0.0 || index == 30)
            {

                // Find fractional weight of two lines.
                if (dt > 0.0) dt = 0.0;

                dt = -dt;

                double f;

                if (index == 1)
                    f = 0.0;
                else
                    f = dt / (last_dt + dt);

                // Interpolate the temperature.

                temperature = 1.0E6 / (TEMP_TABLE [index - 1][0] * f +
                        TEMP_TABLE [index    ][0] * (1.0 - f));

                // Find delta from black body point to test coordinate.
                uu = u - (TEMP_TABLE [index - 1][1] * f +
                        TEMP_TABLE [index    ][1] * (1.0 - f));

                vv = v - (TEMP_TABLE [index - 1][2] * f +
                        TEMP_TABLE [index    ][2] * (1.0 - f));

                // Interpolate vectors along slope.
                du = du * (1.0 - f) + last_du * f;
                dv = dv * (1.0 - f) + last_dv * f;

                len = Math.sqrt (du * du + dv * dv);

                du /= len;
                dv /= len;

                // Find distance along slope.
                tint = (uu * du + vv * dv) * TINT_SCALE;
                break;
            }

            // Try next line pair.
            last_dt = dt;
            last_du = du;
            last_dv = dv;
        }
        return new ColorTemperature(temperature, tint);
    }
}
