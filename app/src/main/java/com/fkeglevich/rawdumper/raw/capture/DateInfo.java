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

import com.fkeglevich.rawdumper.tiff.ExifTagWriter;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.GregorianCalendar;

/**
 * Created by Flávio Keglevich on 09/06/2017.
 * TODO: Add a class header comment!
 */

public class DateInfo
{
    public GregorianCalendar captureDate = null;

    public void writeTiffTags(TiffWriter tiffWriter)
    {
        if (captureDate != null)
            tiffWriter.setField(TiffTag.TIFFTAG_DATETIME, ExifTagWriter.formatCalendarTag(captureDate));
    }
}
