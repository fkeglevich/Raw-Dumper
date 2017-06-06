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

package com.fkeglevich.rawdumper.dng2;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.raw.mkn.MakerNoteInfo;
import com.fkeglevich.rawdumper.tiff.ExifTag;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class contains all the info of the Exif IFD of the generated DNG files.
 *
 * Created by Flávio Keglevich on 27/05/2017.
 */

public class ExifInfo implements Serializable
{
    private static final byte[] exifVersion = new byte[]{48, 50, 50, 48};

    //Exposure Tags
    public double exposureTime; //in seconds
    public double fNumber;
    public int iso;
    public double exposureCompensation;
    public Flash flash;

    //Date/time Tags
    public GregorianCalendar dateTimeOriginal;

    //Extra Tags
    public double focalLength;
    public byte[] makerNote;

    public ExifInfo()
    {   }

    public void writeExifTags(TiffWriter writer)
    {
        writer.createEXIFDirectory();

        writer.setField(ExifTag.EXIFTAG_EXPOSURETIME, exposureTime);
        writer.setField(ExifTag.EXIFTAG_SHUTTERSPEEDVALUE, apexShutterSpeedValue(exposureTime));

        writer.setField(ExifTag.EXIFTAG_FNUMBER, fNumber);
        writer.setField(ExifTag.EXIFTAG_APERTUREVALUE, apexApertureValue(fNumber));

        writer.setField(ExifTag.EXIFTAG_ISOSPEEDRATINGS, new short[]{(short)iso}, true);

        writer.setField(ExifTag.EXIFTAG_EXPOSUREBIASVALUE, exposureCompensation);

        if (flash != Flash.UNKNOWN)
            writer.setField(ExifTag.EXIFTAG_FLASH, flash.getExifValue());

        writer.setField(ExifTag.EXIFTAG_DATETIMEORIGINAL, DataFormatter.formatCalendar(dateTimeOriginal));
        writer.setField(ExifTag.EXIFTAG_SUBSECTIMEORIGINAL, "" + dateTimeOriginal.get(Calendar.MILLISECOND));

        writer.setField(ExifTag.EXIFTAG_FOCALLENGTH, focalLength);

        writer.setField(ExifTag.EXIFTAG_MAKERNOTE, makerNote, true);

        writer.setField(ExifTag.EXIFTAG_EXIFVERSION, exifVersion, false);

        long[] dirOffset = {0};
        writer.writeCustomDirectory(dirOffset, 0);
        writer.setDirectory((short)0);
        writer.setField(TiffTag.TIFFTAG_EXIFIFD, dirOffset[0]);
    }

    public void getInfoFrom(MakerNoteInfo makerNoteInfo, Camera.Parameters parameters)
    {
        exposureTime = makerNoteInfo.exposureTime;
        //TODO: UNHARDCODE THIS!
        fNumber = 2.0;//parameters.getFNumber();
        iso = makerNoteInfo.iso;
        exposureCompensation = parameters.getExposureCompensationStep() * parameters.getExposureCompensation();

        //TODO: Better detecting of flash
        if (!Camera.Parameters.FLASH_MODE_AUTO.equals(parameters.getFlashMode()))
            flash = Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode()) ? Flash.DID_NOT_FIRE : Flash.FIRED;
        else
            flash = Flash.UNKNOWN;

        dateTimeOriginal = makerNoteInfo.dateTime;
        focalLength = parameters.getFocalLength();
        makerNote = makerNoteInfo.originalMakerNote;
    }

    private double log2(double x)
    {
        return Math.log(x) / Math.log(2.0);
    }

    private double apexShutterSpeedValue(double x)
    {
        return  -1.0 * log2(x);
    }

    private double apexApertureValue(double x)
    {
        return 2 * log2(x);
    }
}
