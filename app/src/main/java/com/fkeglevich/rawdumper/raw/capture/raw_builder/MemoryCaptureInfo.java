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

package com.fkeglevich.rawdumper.raw.capture.raw_builder;

import android.hardware.Camera;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.RawCaptureInfo;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.raw.data.image.MemoryRawImage;
import com.fkeglevich.rawdumper.raw.metadata.ExifMetadata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import androidx.annotation.NonNull;

public class MemoryCaptureInfo extends RawCaptureInfo
{
    private final ExifMetadata exifMetadata;
    private final MakerNoteInfo makerNoteInfo;

    public MemoryCaptureInfo(CameraContext cameraContext, MemoryRawImage rawImage, Camera.Parameters parameters)
    {
        super(cameraContext, rawImage, parameters);
        exifMetadata  = buildMetadata(rawImage.getJpegData());
        makerNoteInfo = MakerNoteInfoExtractor.extract(exifMetadata.originalMakerNote, getCameraInfo());
    }

    @Override
    @NonNull
    public MakerNoteInfo getMakerNoteInfo()
    {
        return makerNoteInfo;
    }

    @Override
    @NonNull
    public ExifMetadata getExifMetadata()
    {
        return exifMetadata;
    }

    @Override
    public void writeInfoTo(DngNegative negative)
    {
        //No op
    }

    private ExifMetadata buildMetadata(byte[] jpegData)
    {
        ExifMetadata result = new ExifMetadata(getCameraInfo(), getDeviceInfo());

        try (BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(jpegData)))
        {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream, jpegData.length);
            for(Directory directory : metadata.getDirectories())
            {
                if (directory.containsTag(ExifIFD0Directory.TAG_ISO_EQUIVALENT))
                    result.iso = Iso.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_EXPOSURE_TIME))
                    result.exposureTime = ShutterSpeed.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_FNUMBER))
                    result.aperture = directory.getDouble(ExifIFD0Directory.TAG_FNUMBER);

                if (directory.containsTag(ExifIFD0Directory.TAG_MAKERNOTE))
                    result.originalMakerNote = directory.getByteArray(ExifIFD0Directory.TAG_MAKERNOTE);

                if (directory.containsTag(ExifIFD0Directory.TAG_EXPOSURE_BIAS))
                    result.exposureBias = Ev.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_FLASH))
                    result.flash = ExifFlash.getFromExifDirectory(directory);

                if (directory.containsTag(ExifIFD0Directory.TAG_FOCAL_LENGTH))
                    result.focalLength = directory.getFloat(ExifIFD0Directory.TAG_FOCAL_LENGTH);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
}
