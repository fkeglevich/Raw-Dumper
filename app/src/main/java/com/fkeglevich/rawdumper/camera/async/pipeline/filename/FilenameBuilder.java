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

package com.fkeglevich.rawdumper.camera.async.pipeline.filename;

import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.raw.capture.DateInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A builder for building the name of all pictures/videos taken
 *
 * Created by Flávio Keglevich on 02/11/17.
 */

public class FilenameBuilder
{
    private enum NameSuffix
    {
        PICTURE("IMG"),
        VIDEO("VID");

        private final String suffix;

        NameSuffix (String suffix)
        {
            this.suffix = suffix;
        }

        public String getSuffix()
        {
            return suffix;
        }
    }

    private static final String NAME_SEPARATOR      = "_";
    private static final String DATE_PATTERN        = "yyyyMMdd" + NAME_SEPARATOR + "HHmmss";

    private final SimpleDateFormat format   = new SimpleDateFormat(DATE_PATTERN, Locale.US);

    private Calendar   calendar             = null;
    private FileFormat fileFormat           = null;
    private NameSuffix nameSuffix           = NameSuffix.PICTURE;

    /**
     * Sets the date used by the builder to create the file name
     *
     * @param calendar      The date as a Calendar object
     * @return              The FilenameBuilder itself
     */
    public FilenameBuilder useCalendar(Calendar calendar)
    {
        this.calendar = calendar;
        return this;
    }

    /**
     * Sets the date used by the builder to create the file name
     *
     * @param dateInfo      The date as a DateInfo object
     * @return              The FilenameBuilder itself
     */
    public FilenameBuilder useDateInfo(DateInfo dateInfo)
    {
        return useCalendar(dateInfo.getCaptureDate());
    }

    public FilenameBuilder useFileFormat(FileFormat fileFormat)
    {
        this.fileFormat = fileFormat;
        return this;
    }

    /**
     * Tells the builder that it's building a picture filename
     *
     * @return  The FilenameBuilder itself
     */
    public FilenameBuilder isPicture()
    {
        nameSuffix = NameSuffix.PICTURE;
        return this;
    }

    /**
     * Tells the builder that it's building a video filename
     *
     * @return  The FilenameBuilder itself
     */
    public FilenameBuilder isVideo()
    {
        nameSuffix = NameSuffix.VIDEO;
        return this;
    }

    /**
     * Build the requested picture/video name
     *
     * @return  The name as a String
     */
    public String build()
    {
        if (fileFormat == null) throw new RuntimeException("A file format wasn't choosed!");
        if (format.getCalendar() == null) throw new RuntimeException("A calendar wasn't choosed!");

        format.setCalendar(calendar);

        return  nameSuffix.getSuffix() +
                NAME_SEPARATOR +
                format.format(calendar.getTime()) +
                fileFormat.getExtension();
    }
}
