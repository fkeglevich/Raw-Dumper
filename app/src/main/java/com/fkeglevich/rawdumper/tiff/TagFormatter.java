/*
 * Copyright 2018, Flávio Keglevich
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

package com.fkeglevich.rawdumper.tiff;

import com.fkeglevich.rawdumper.util.MathUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Deals with some TIFF tags's specific formatting
 *
 * Created by Flávio Keglevich on 13/04/2018.
 */
public class TagFormatter
{
    // Calendar tag formatting
    private static final String DATE_PATTERN = "yyyy:MM:dd HH:mm:ss";

    /**
     * Converts exposure time (in seconds) to APEX shutter speed value
     *
     * @param exposureTime  Exposure time (in seconds)
     * @return  The APEX shutter speed value
     */
    public static double formatApexShutterSpeedTag(double exposureTime)
    {
        return -1.0 * MathUtil.log2(exposureTime);
    }

    /**
     * Converts aperture value (f-number) to APEX aperture value
     *
     * @param aperture  Aperture (f-number)
     * @return          The APEX aperture value
     */
    public static double formatApexApertureTag(double aperture)
    {
        return 2 * MathUtil.log2(aperture);
    }

    /**
     * Converts a Calendar to TIFF date format
     *
     * @param calendar  A Calendar
     * @return          A String for TIFF's date tags
     */
    public static String formatCalendarTag(Calendar calendar)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }
}
