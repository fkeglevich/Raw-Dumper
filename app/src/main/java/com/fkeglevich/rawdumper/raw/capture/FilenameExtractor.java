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

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Flávio Keglevich on 27/07/2017.
 * TODO: Add a class header comment!
 */

public class FilenameExtractor
{
    private static final String NAME_SUFIX      = "IMG";
    private static final String NAME_SEPARATOR  = "_";

    private static final String DATE_PATTERN = "yyyyMMdd" + NAME_SEPARATOR + "HHmmss";

    private SimpleDateFormat dateFormat;

    public FilenameExtractor()
    {
        dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
    }

    /*public String extractFromDate(DateInfo dateInfo)
    {
        GregorianCalendar date = dateInfo.captureDate;
        dateFormat.format()
        return NAME_SUFIX + NAME_SEPARATOR + date.
    }*/

    /*
    @SuppressLint("SimpleDateFormat")
    public static String formatCalendarTag(Calendar calendar)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }
     */
}
