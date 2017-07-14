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

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.ExifInfo;
import com.fkeglevich.rawdumper.tiff.ExifTagWriter;
import com.fkeglevich.rawdumper.tiff.TiffTag;
import com.fkeglevich.rawdumper.tiff.TiffWriter;

/**
 * This class is used for writing Exif data on the DNG files.
 *
 * Created by Flávio Keglevich on 14/06/2017.
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
        ExifInfo exifInfo = new ExifInfo();

        if (captureInfo.extraJpegBytes != null)
            exifInfo.getSomeDataFrom(captureInfo.extraJpegBytes, captureInfo.makerNoteInfo == null);
        else
        {
            exifInfo.getSomeDataFrom(captureInfo.date);
            if (captureInfo.camera.getLens() != null)
                exifInfo.getSomeDataFrom(captureInfo.camera.getLens());

            if (captureInfo.captureParameters != null)
                exifInfo.getSomeDataFrom(captureInfo.captureParameters);
        }

        if (captureInfo.makerNoteInfo != null)
            exifInfo.getSomeDataFrom(captureInfo.makerNoteInfo);

        exifInfo.writeTiffExifTags(tiffWriter);
    }

    void closeEXIFDirectory(TiffWriter tiffWriter)
    {
        long[] dirOffset = {0};
        tiffWriter.writeCustomDirectory(dirOffset, 0);
        tiffWriter.setDirectory((short)0);
        tiffWriter.setField(TiffTag.TIFFTAG_EXIFIFD, dirOffset[0]);
    }
}
