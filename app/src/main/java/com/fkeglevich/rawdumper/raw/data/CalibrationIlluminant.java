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

package com.fkeglevich.rawdumper.raw.data;

/**
 * A simple enum for listing all supported calibration illuminants.
 * They're used in the CalibrationIlluminant1 and CalibrationIlluminant2 DNG tags and for the
 * LightSource Exif tag.
 *
 * Created by Flávio Keglevich on 16/04/2017.
 */

public enum CalibrationIlluminant
{
    UNKNOWN(0,                  "Unknown"),
    DAYLIGHT(1,                 "Daylight"),
    FLUORESCENT(2,              "Fluorescent"),
    TUNGSTEN(3,                 "Tungsten"),
    FLASH(4,                    "Flash"),
    FINE_WEATHER(9,             "Fine Weather"),
    CLOUDY_WEATHER(10,          "Cloudy Weather"),
    SHADE(11,                   "Shade"),
    DAYLIGHT_FLUORESCENT(12,    "Daylight Fluorescent"),
    DAY_WHITE_FLUORESCENT(13,   "Day White Fluorescent"),
    COOL_WHITE_FLUORESCENT(14,  "Cool White Fluorescent"),
    WHITE_FLUORESCENT(15,       "White Fluorescent"),
    STANDARD_LIGHT_A(17,        "Standard light A"),
    STANDARD_LIGHT_B(18,        "Standard light B"),
    STANDARD_LIGHT_C(19,        "Standard light C"),
    D55(20,                     "D55"),
    D65(21,                     "D65"),
    D75(22,                     "D75"),
    D50(23,                     "D50"),
    ISO_STUDIO_TUNGSTEN(24,     "Iso Studio Tungsten"),
    OTHER_LIGHT_SOURCE(255,     "Other Light Source");

    private final int exifCode;
    private final String name;

    CalibrationIlluminant(int exifCode, String name)
    {
        this.exifCode = exifCode;
        this.name = name;
    }

    public int getExifCode()
    {
        return exifCode;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return "[CalibrationIlluminant" + getName() + " (" + getExifCode() + ")]";
    }
}
