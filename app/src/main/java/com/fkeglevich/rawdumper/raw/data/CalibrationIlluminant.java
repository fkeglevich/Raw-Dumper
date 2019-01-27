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

import androidx.annotation.Keep;

/**
 * A simple enum for listing all supported calibration illuminants.
 * They're used in the CalibrationIlluminant1 and CalibrationIlluminant2 DNG tags and for the
 * LightSource Exif tag.
 *
 * Created by Flávio Keglevich on 16/04/2017.
 */

@Keep
public enum CalibrationIlluminant
{
    UNKNOWN(0,                  "Unknown",                  0),
    DAYLIGHT(1,                 "Daylight",                 5500),
    FLUORESCENT(2,              "Fluorescent",              4150),
    TUNGSTEN(3,                 "Tungsten",                 2850),
    FLASH(4,                    "Flash",                    5500),
    FINE_WEATHER(9,             "Fine Weather",             5500),
    CLOUDY_WEATHER(10,          "Cloudy Weather",           6500),
    SHADE(11,                   "Shade",                    7500),
    DAYLIGHT_FLUORESCENT(12,    "Daylight Fluorescent",     6400),
    DAY_WHITE_FLUORESCENT(13,   "Day White Fluorescent",    5050),
    COOL_WHITE_FLUORESCENT(14,  "Cool White Fluorescent",   4150),
    WHITE_FLUORESCENT(15,       "White Fluorescent",        3525),
    STANDARD_LIGHT_A(17,        "Standard light A",         2850),
    STANDARD_LIGHT_B(18,        "Standard light B",         5500),
    STANDARD_LIGHT_C(19,        "Standard light C",         6500),
    D55(20,                     "D55",                      5500),
    D65(21,                     "D65",                      6500),
    D75(22,                     "D75",                      7500),
    D50(23,                     "D50",                      5000),
    ISO_STUDIO_TUNGSTEN(24,     "Iso Studio Tungsten",      3200),
    OTHER_LIGHT_SOURCE(255,     "Other Light Source",       0);

    private final int exifCode;
    private final String name;
    private final double temperature;

    CalibrationIlluminant(int exifCode, String name, double temperature)
    {
        this.exifCode = exifCode;
        this.name = name;
        this.temperature = temperature;
    }

    public int getExifCode()
    {
        return exifCode;
    }

    private String getName()
    {
        return name;
    }

    public double getTemperature()
    {
        return temperature;
    }

    public String toString()
    {
        return "[CalibrationIlluminant" + getName() + " (" + getExifCode() + ")]";
    }
}
