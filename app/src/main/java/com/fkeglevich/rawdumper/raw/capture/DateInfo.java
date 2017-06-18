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

import android.annotation.SuppressLint;

import com.fkeglevich.rawdumper.tiff.ExifTag;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Flávio Keglevich on 09/06/2017.
 * TODO: Add a class header comment!
 */

public class DateInfo
{
    // DateTime formatting
    private static final String DATE_PATTERN = "yyyy:MM:dd HH:mm:ss";

    @SuppressLint("SimpleDateFormat")
    static String formatCalendar(Calendar calendar)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

    public GregorianCalendar captureDate = null;

    public DateInfo()
    {   }

    public void writeTiffExifTags(TiffWriter tiffWriter)
    {
        if (captureDate != null)
        {
            tiffWriter.setField(ExifTag.EXIFTAG_DATETIMEORIGINAL, formatCalendar(captureDate));
            tiffWriter.setField(ExifTag.EXIFTAG_SUBSECTIMEORIGINAL, "" + captureDate.get(Calendar.MILLISECOND));
        }
    }

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        if (captureDate != null)
            tiffWriter.setField(TiffTag.TIFFTAG_DATETIME, formatCalendar(captureDate));
    }
}
