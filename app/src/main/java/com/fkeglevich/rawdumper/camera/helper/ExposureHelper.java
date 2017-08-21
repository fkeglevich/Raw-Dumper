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

package com.fkeglevich.rawdumper.camera.helper;

/**
 * Simple helper class for decoding specific but recurring exposure data.
 *
 * Created by Flávio Keglevich on 21/08/2017.
 */

public class ExposureHelper
{
    /**
     * Decodes integer encoded exposure time.
     *
     * @param value The exposure time
     * @return      A double representing the same exposure time in seconds
     */
    public static double decodeIntegerExposureTime(int value)
    {
        return value / 1000000.0;
    }

    /**
     * Decodes a simple float-encoded ISO value
     *
     * @param value     The encoded ISO
     * @param baseIso   The sensor's base ISO value
     * @return          The integer ISO value
     */
    public static int decodeFloatIso(float value, int baseIso)
    {
        return Math.round(baseIso * value);
    }
}
