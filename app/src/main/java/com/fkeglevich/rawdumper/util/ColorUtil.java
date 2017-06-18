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

/**
 * Created by Flávio Keglevich on 08/06/2017.
 * TODO: Add a class header comment!
 */

public class ColorUtil
{
    /**
     * Gets the CIE chromaticity of a CIE-D illuminant from the correlated color temperature of that CIE-D illuminant.
     * Java code based on the formulas of Bruce Justin Lindbloom at http://www.brucelindbloom.com/index.html?Eqn_T_to_xy.html
     * and the code of UFRaw by Udi Fuchs
     *
     * @param temperature   The correlated color temperature (in Kelvin)
     * @return  A float array containing the x and y coordinates, respectively
     */
    public static float[] getXYFromCCT(double temperature)
    {
        double x, y;
        double cct_squared = temperature*temperature;
        double cct_cubic = cct_squared*temperature;

        if (temperature <= 4000)
            x =   (274750000.0/cct_cubic) -  (985980.0/cct_squared) + (1174.44/temperature) + 0.145986;
        else if (temperature <= 7000)
            x = (-4607000000.0/cct_cubic) + (2967800.0/cct_squared) + (99.11/temperature)   + 0.244063;
        else
            x = (-2006400000.0/cct_cubic) + (1901800.0/cct_squared) + (247.48/temperature)  + 0.237040;

        y = (-3.0*(x*x)) + (2.870*x) - 0.275;
        return new float[]{(float)x, (float)y};
    }
}
