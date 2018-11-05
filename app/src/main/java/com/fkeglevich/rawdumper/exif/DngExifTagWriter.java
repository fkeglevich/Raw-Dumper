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
import com.fkeglevich.rawdumper.util.DateUtil;

import java.util.Calendar;

public class DngExifTagWriter implements ExifTagWriter
{
    static
    {
        System.loadLibrary("dng-writer");
    }

    private final long nativeHandle;

    private native void writeExposureTimeTagsNative(long pointer, double exposureTime);
    private native void writeISOTagNative(long pointer, int iso);
    private native void writeDateTimeOriginalTagsNative(long pointer, String dateIso8601);
    private native void writeDateTimeDigitizedTagsNative(long pointer, String dateIso8601);
    private native void writeApertureTagsNative(long pointer, double aperture);
    private native void writeExifVersionTagNative(long pointer, byte[] exifVersion);
    private native void writeExposureBiasTagNative(long pointer, float bias);
    private native void writeFlashTagNative(long pointer, short exifValue);
    private native void writeFocalLengthTagNative(long pointer, float focalLength);

    DngExifTagWriter(long nativeHandle)
    {
        this.nativeHandle = nativeHandle;
    }

    @Override
    public void writeExposureTimeTags(ShutterSpeed shutterSpeed)
    {
        writeExposureTimeTagsNative(nativeHandle, shutterSpeed.getExposureInSeconds());
    }

    @Override
    public void writeISOTag(Iso iso)
    {
        writeISOTagNative(nativeHandle, iso.getNumericValue());
    }

    @Override
    public void writeMakerNoteTag(byte[] makerNote)
    {
        //not implemented yet (probably unsupported by DNG SDK)
    }

    @Override
    public void writeDateTimeOriginalTags(Calendar dateTimeOriginal)
    {
        writeDateTimeOriginalTagsNative(nativeHandle, DateUtil.calendarToIso8601(dateTimeOriginal));
    }

    @Override
    public void writeDateTimeDigitizedTags(Calendar dateTimeDigitized)
    {
        writeDateTimeDigitizedTagsNative(nativeHandle, DateUtil.calendarToIso8601(dateTimeDigitized));
    }

    @Override
    public void writeApertureTags(double aperture)
    {
        writeApertureTagsNative(nativeHandle, aperture);
    }

    @Override
    public void writeExifVersionTag(byte[] exifVersion)
    {
        writeExifVersionTagNative(nativeHandle, exifVersion);
    }

    @Override
    public void writeExposureBiasTag(Ev exposureBias)
    {
        writeExposureBiasTagNative(nativeHandle, exposureBias.getValue());
    }

    @Override
    public void writeFlashTag(ExifFlash flash)
    {
        writeFlashTagNative(nativeHandle, flash.getExifValue());
    }

    @Override
    public void writeFocalLengthTag(float focalLength)
    {
        writeFocalLengthTagNative(nativeHandle, focalLength);
    }
}
