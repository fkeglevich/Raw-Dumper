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

package com.fkeglevich.rawdumper.exif;

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.tiff.TagFormatter;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.util.Calendar;

/**
 * Writes EXIF tags to a TIFF file
 *
 * Created by Flávio Keglevich on 28/06/2017.
 */

public class TiffExifTagWriter implements ExifTagWriter
{
    private final TiffWriter tiffWriter;

    public TiffExifTagWriter(TiffWriter tiffWriter)
    {
        this.tiffWriter = tiffWriter;
    }

    @Override
    public void writeExposureTimeTags(ShutterSpeed shutterSpeed)
    {
        double exposureTime = shutterSpeed.getExposureInSeconds();
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_EXPOSURETIME, exposureTime);
        if (result == 0) return;
            tiffWriter.setField(ExifTag.EXIFTAG_SHUTTERSPEEDVALUE, TagFormatter.formatApexShutterSpeedTag(exposureTime));
    }

    @Override
    public void writeISOTag(Iso iso)
    {
        tiffWriter.setField(ExifTag.EXIFTAG_ISOSPEEDRATINGS, new short[]{(short) iso.getNumericValue()}, true);
    }

    @Override
    public void writeMakerNoteTag(byte[] makerNote)
    {
        tiffWriter.setField(ExifTag.EXIFTAG_MAKERNOTE, makerNote, true);
    }

    @Override
    public void writeDateTimeOriginalTags(Calendar dateTimeOriginal)
    {
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_DATETIMEORIGINAL, TagFormatter.formatCalendarTag(dateTimeOriginal));
        if (result == 0) return;
            tiffWriter.setField(ExifTag.EXIFTAG_SUBSECTIMEORIGINAL, "" + dateTimeOriginal.get(Calendar.MILLISECOND));
    }

    @Override
    public void writeDateTimeDigitizedTags(Calendar dateTimeOriginal)
    {
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_DATETIMEDIGITIZED, TagFormatter.formatCalendarTag(dateTimeOriginal));
        if (result == 0) return;
            tiffWriter.setField(ExifTag.EXIFTAG_SUBSECTIMEDIGITIZED, "" + dateTimeOriginal.get(Calendar.MILLISECOND));
    }

    @Override
    public void writeApertureTags(double aperture)
    {
        int result;
        result = tiffWriter.setField(ExifTag.EXIFTAG_FNUMBER, aperture);
        if (result == 0) return;
            tiffWriter.setField(ExifTag.EXIFTAG_APERTUREVALUE, TagFormatter.formatApexApertureTag(aperture));
    }

    @Override
    public void writeExifVersionTag(byte[] exifVersion)
    {
        tiffWriter.setField(ExifTag.EXIFTAG_EXIFVERSION, exifVersion, false);
    }

    @Override
    public void writeExposureBiasTag(Ev exposureBias)
    {
        tiffWriter.setField(ExifTag.EXIFTAG_EXPOSUREBIASVALUE, (double) exposureBias.getValue());
    }

    @Override
    public void writeFlashTag(ExifFlash flash)
    {
        tiffWriter.setField(ExifTag.EXIFTAG_FLASH, flash.getExifValue());
    }

    @Override
    public void writeFocalLengthTag(float focalLength)
    {
        tiffWriter.setField(ExifTag.EXIFTAG_FOCALLENGTH, focalLength);
    }
}
