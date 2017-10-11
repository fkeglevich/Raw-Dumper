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

package com.fkeglevich.rawdumper.tiff;

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.util.MathUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Flávio Keglevich on 28/06/2017.
 * TODO: Add a class header comment!
 */

public class ExifTagWriter
{
    // Calendar tag formatting
    private static final String DATE_PATTERN = "yyyy:MM:dd HH:mm:ss";

    public static int writeExposureTimeTags(TiffWriter tiffWriter, ShutterSpeed shutterSpeed)
    {
        double exposureTime = shutterSpeed.getExposureInSeconds();
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_EXPOSURETIME, exposureTime);
        if (result == 0) return result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_SHUTTERSPEEDVALUE, formatApexShutterSpeedTag(exposureTime));
        return result;
    }

    public static int writeISOTag(TiffWriter tiffWriter, Iso iso)
    {
        return tiffWriter.setField(ExifTag.EXIFTAG_ISOSPEEDRATINGS, new short[]{(short)iso.getNumericValue()}, true);
    }

    public static int writeMakerNoteTag(TiffWriter tiffWriter, byte[] makerNote)
    {
        return tiffWriter.setField(ExifTag.EXIFTAG_MAKERNOTE, makerNote, true);
    }

    public static int writeDateTimeOriginalTags(TiffWriter tiffWriter, Calendar dateTimeOriginal)
    {
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_DATETIMEORIGINAL, formatCalendarTag(dateTimeOriginal));
        if (result == 0) return result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_SUBSECTIMEORIGINAL, "" + dateTimeOriginal.get(Calendar.MILLISECOND));
        return result;
    }

    public static int writeDateTimeDigitizedTags(TiffWriter tiffWriter, Calendar dateTimeOriginal)
    {
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_DATETIMEDIGITIZED, formatCalendarTag(dateTimeOriginal));
        if (result == 0) return result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_SUBSECTIMEDIGITIZED, "" + dateTimeOriginal.get(Calendar.MILLISECOND));
        return result;
    }

    public static int writeApertureTags(TiffWriter tiffWriter, double aperture)
    {
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_FNUMBER, aperture);
        if (result == 0) return result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_APERTUREVALUE, formatApexApertureTag(aperture));
        return result;
    }

    public static int writeExifVersionTag(TiffWriter tiffWriter, byte[] exifVersion)
    {
        return tiffWriter.setField(ExifTag.EXIFTAG_EXIFVERSION, exifVersion, false);
    }

    public static int writeExposureBiasTag(TiffWriter tiffWriter, Ev exposureBias)
    {
        return tiffWriter.setField(ExifTag.EXIFTAG_EXPOSUREBIASVALUE, (double) exposureBias.getValue());
    }

    public static int writeFlashTag(TiffWriter tiffWriter, ExifFlash flash)
    {
        return tiffWriter.setField(ExifTag.EXIFTAG_FLASH, flash.getExifValue());
    }

    public static int writeFocalLengthTag(TiffWriter tiffWriter, float focalLength)
    {
        return tiffWriter.setField(ExifTag.EXIFTAG_FOCALLENGTH, focalLength);
    }

    public static double formatApexShutterSpeedTag(double exposureTime)
    {
        return -1.0 * MathUtil.log2(exposureTime);
    }

    public static double formatApexApertureTag(double aperture)
    {
        return 2 * MathUtil.log2(aperture);
    }

    public static String formatCalendarTag(Calendar calendar)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);
        dateFormat.setCalendar(calendar);
        return dateFormat.format(calendar.getTime());
    }

}
