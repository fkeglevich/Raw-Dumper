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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.RawCaptureInfo;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.raw.data.image.FileRawImage;
import com.fkeglevich.rawdumper.raw.metadata.ExifMetadata;

import androidx.annotation.NonNull;

public class FileCaptureInfo extends RawCaptureInfo
{
    private final ExifMetadata exifMetadata;
    private final MakerNoteInfo makerNoteInfo;
    private final String originalRawFileName;

    public FileCaptureInfo(CameraContext cameraContext, FileRawImage rawImage, Camera.Parameters parameters)
    {
        super(cameraContext, rawImage, parameters);
        makerNoteInfo = MakerNoteInfoExtractor.extract(rawImage.getMakerNotes(), getCameraInfo());
        exifMetadata  = buildMetadata(makerNoteInfo, parameters);
        originalRawFileName = rawImage.getFile().getName();
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
        negative.setOriginalRawFileName(originalRawFileName);
    }

    private ExifMetadata buildMetadata(MakerNoteInfo mknInfo, Camera.Parameters parameters)
    {
        ExifMetadata result = new ExifMetadata(getCameraInfo(), getDeviceInfo());

        if (getLensInfo() != null)
            result.aperture = getLensInfo().getNumericAperture();

        result.exposureBias         = Ev.create(parameters.getExposureCompensationStep() * parameters.getExposureCompensation());
        result.focalLength          = parameters.getFocalLength();
        result.flash                = getFlash(parameters);
        result.originalMakerNote    = mknInfo.originalMakerNote;
        result.iso                  = mknInfo.iso;
        result.exposureTime         = mknInfo.exposureTime;

        return result;
    }

    private ExifFlash getFlash(Camera.Parameters parameters)
    {
        if (!Camera.Parameters.FLASH_MODE_AUTO.equals(parameters.getFlashMode()))
            return Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode()) ? ExifFlash.DID_NOT_FIRE : ExifFlash.FIRED;

        return null;
    }
}
