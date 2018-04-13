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

import com.fkeglevich.rawdumper.tiff.TagFormatter;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.GregorianCalendar;

/**
 * Stores date/time information about a raw capture
 *
 * Created by Flávio Keglevich on 09/06/2017.
 */

public class DateInfo
{
    /**
     * Creates a DateInfo object from a hal-generated i3av4 filename.
     * It's useful when the i3av4 file is the only source of information.
     *
     * @param filename  A String containing only the name of the file
     * @return  A DateInfo object
     */
    public static DateInfo createFromFilename(String filename)
    {
        //If it doesn't have enough chars, just return a valid value
        if (filename.length() < 19)
            return createFromCurrentTime();

        int year = Integer.parseInt(filename.substring(4, 8));
        int month = Integer.parseInt(filename.substring(8, 10));
        int day = Integer.parseInt(filename.substring(10, 12));
        int hour = Integer.parseInt(filename.substring(13, 15));
        int minute = Integer.parseInt(filename.substring(15, 17));
        int second = Integer.parseInt(filename.substring(17, 19));

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(year, month, day, hour, minute, second);

        return new DateInfo(calendar);
    }

    /**
     * Creates a DateInfo object from the current time when this method is called.
     *
     * @return  A DateInfo object
     */
    public static DateInfo createFromCurrentTime()
    {
        return new DateInfo((GregorianCalendar)GregorianCalendar.getInstance());
    }

    private GregorianCalendar captureDate = null;

    /**
     * Private constructor
     *
     * @param captureDate   The main date/time of the capture
     */
    private DateInfo(GregorianCalendar captureDate)
    {
        this.captureDate = captureDate;
    }

    /**
     * Write all information contained by this object as Tiff tags.
     *
     * @param tiffWriter    The TiffWriter used to actually write the tags
     */
    public void writeTiffTags(TiffWriter tiffWriter)
    {
        if (getCaptureDate() != null)
            tiffWriter.setField(TiffTag.TIFFTAG_DATETIME, TagFormatter.formatCalendarTag(getCaptureDate()));
    }

    /**
     * Gets the picture capture date
     *
     * @return  A GregorianCalendar
     */
    public GregorianCalendar getCaptureDate()
    {
        return captureDate;
    }
}
