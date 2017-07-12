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

package com.fkeglevich.rawdumper.dng;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.Flash;
import com.fkeglevich.rawdumper.tiff.ExifTagWriter;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class ExifWriter
{
    private static final byte[] exifVersion = new byte[]{48, 50, 50, 48};

    ExifWriter()
    {   }

    void createEXIFDirectory(TiffWriter tiffWriter)
    {
        tiffWriter.createEXIFDirectory();
    }

    void writeTiffExifTags(TiffWriter tiffWriter, CaptureInfo captureInfo)
    {
        ExifTagWriter.writeExifVersionTag(tiffWriter, exifVersion);
        captureInfo.makerNoteInfo.writeTiffExifTags(tiffWriter);
        captureInfo.date.writeTiffExifTags(tiffWriter);
        captureInfo.camera.getLens().writeTiffExifTags(tiffWriter);

        Camera.Parameters parameters = captureInfo.captureParameters;

        if (parameters != null)
        {
            double exposureCompensation = parameters.getExposureCompensationStep() *
                    parameters.getExposureCompensation();
            ExifTagWriter.writeExposureBiasTag(tiffWriter, exposureCompensation);

            //TODO: Better handling of flash
            if (!Camera.Parameters.FLASH_MODE_AUTO.equals(parameters.getFlashMode()))
            {
                Flash flash = Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode()) ? Flash.DID_NOT_FIRE : Flash.FIRED;
                ExifTagWriter.writeFlashTag(tiffWriter, flash);
            }

            ExifTagWriter.writeFocalLengthTag(tiffWriter, parameters.getFocalLength());
        }
    }

    void closeEXIFDirectory(TiffWriter tiffWriter)
    {
        long[] dirOffset = {0};
        tiffWriter.writeCustomDirectory(dirOffset, 0);
        tiffWriter.setDirectory((short)0);
        tiffWriter.setField(TiffTag.TIFFTAG_EXIFIFD, dirOffset[0]);
    }
}
