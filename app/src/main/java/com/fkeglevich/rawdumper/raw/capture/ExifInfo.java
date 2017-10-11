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

import android.hardware.Camera;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.raw.info.LensInfo;
import com.fkeglevich.rawdumper.tiff.ExifTagWriter;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;

/**
 * Created by Flávio Keglevich on 13/07/2017.
 * TODO: Add a class header comment!
 */

public class ExifInfo
{
    private GregorianCalendar dateTimeOriginal   = null;
    private Iso iso                              = null;
    private ShutterSpeed exposureTime            = null;
    private Double aperture                      = null;
    private byte[] originalMakerNote             = null;
    private Ev exposureBias                      = null;
    private ExifFlash flash                      = null;
    private Float focalLength                    = null;

    public void getExifDataFromCapture(CaptureInfo captureInfo)
    {
        if (captureInfo.extraJpegBytes != null)
        {
            if (captureInfo.makerNoteInfo == null || captureInfo.makerNoteInfo.originalMakerNote == null)
                getSomeDataFrom(captureInfo.extraJpegBytes, true);
            else
            {
                getSomeDataFrom(captureInfo.makerNoteInfo);
                getSomeDataFrom(captureInfo.extraJpegBytes, false);
            }
        }
        else
        {
            if (captureInfo.date != null)
                getSomeDataFrom(captureInfo.date);
            else
                getSomeDataFrom(DateInfo.createFromCurrentTime());

            if (captureInfo.camera != null && captureInfo.camera.getLens() != null)
                getSomeDataFrom(captureInfo.camera.getLens());

            if (captureInfo.captureParameters != null)
                getSomeDataFrom(captureInfo.captureParameters);

            if (captureInfo.makerNoteInfo != null)
                getSomeDataFrom(captureInfo.makerNoteInfo);
        }
    }

    private void getSomeDataFrom(DateInfo dateInfo)
    {
        dateTimeOriginal = dateInfo.captureDate;
    }

    private void getSomeDataFrom(MakerNoteInfo makerNoteInfo)
    {
        originalMakerNote = makerNoteInfo.originalMakerNote;
        iso = makerNoteInfo.iso;
        exposureTime = makerNoteInfo.exposureTime;
    }

    private void getSomeDataFrom(LensInfo lensInfo)
    {
        aperture = lensInfo.getAperture();
    }

    private void getSomeDataFrom(Camera.Parameters parameters)
    {
        exposureBias = Ev.create(parameters.getExposureCompensationStep() * parameters.getExposureCompensation());
        focalLength = parameters.getFocalLength();

        //TODO: Better handling of flash
        if (!Camera.Parameters.FLASH_MODE_AUTO.equals(parameters.getFlashMode()))
            flash = Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode()) ? ExifFlash.DID_NOT_FIRE : ExifFlash.FIRED;
    }

    private boolean getSomeDataFrom(byte[] extraJpegBytes, boolean extractMakerNotes)
    {
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(extraJpegBytes));
        boolean success = true;
        try
        {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream, extraJpegBytes.length);
            for(Directory directory : metadata.getDirectories())
            {
                if (directory.containsTag(ExifIFD0Directory.TAG_DATETIME_ORIGINAL))
                {
                    dateTimeOriginal = new GregorianCalendar();
                    dateTimeOriginal.setTime(directory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL));
                }

                if (directory.containsTag(ExifIFD0Directory.TAG_ISO_EQUIVALENT))
                    iso = Iso.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_EXPOSURE_TIME))
                    exposureTime = ShutterSpeed.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_FNUMBER))
                    aperture = directory.getDouble(ExifIFD0Directory.TAG_FNUMBER);

                if (extractMakerNotes && directory.containsTag(ExifIFD0Directory.TAG_MAKERNOTE))
                    originalMakerNote = directory.getByteArray(ExifIFD0Directory.TAG_MAKERNOTE);

                if (directory.containsTag(ExifIFD0Directory.TAG_EXPOSURE_BIAS))
                    exposureBias = Ev.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_FLASH))
                    flash = ExifFlash.getFlashFromValue((short)directory.getInt(ExifIFD0Directory.TAG_FLASH));

                if (directory.containsTag(ExifIFD0Directory.TAG_FOCAL_LENGTH))
                    focalLength = directory.getFloat(ExifIFD0Directory.TAG_FOCAL_LENGTH);
            }
        }
        catch (Exception e)
        {
            success = false;
        }

        try
        {   inputStream.close();    }
        catch (IOException ignored)
        {   }

        return success;
    }

    public void writeTiffExifTags(TiffWriter tiffWriter)
    {
        if (dateTimeOriginal != null)
        {
            ExifTagWriter.writeDateTimeOriginalTags(tiffWriter, dateTimeOriginal);
            ExifTagWriter.writeDateTimeDigitizedTags(tiffWriter, dateTimeOriginal);
        }

        if (iso != null)
            ExifTagWriter.writeISOTag(tiffWriter, iso);

        if (exposureTime != null)
            ExifTagWriter.writeExposureTimeTags(tiffWriter, exposureTime);

        if (aperture != null)
            ExifTagWriter.writeApertureTags(tiffWriter, aperture);

        if (originalMakerNote != null)
            ExifTagWriter.writeMakerNoteTag(tiffWriter, originalMakerNote);

        if (exposureBias != null)
            ExifTagWriter.writeExposureBiasTag(tiffWriter, exposureBias);

        if (flash != null && flash != ExifFlash.UNKNOWN)
            ExifTagWriter.writeFlashTag(tiffWriter, flash);

        if (focalLength != null)
            ExifTagWriter.writeFocalLengthTag(tiffWriter, focalLength);
    }
}
