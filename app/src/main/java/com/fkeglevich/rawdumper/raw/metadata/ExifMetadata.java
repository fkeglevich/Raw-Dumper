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

package com.fkeglevich.rawdumper.raw.metadata;

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.exif.DngExifTagWriter;
import com.fkeglevich.rawdumper.exif.ExifTagWriter;
import com.fkeglevich.rawdumper.raw.data.ExifFlash;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.util.AppPackageUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ExifMetadata
{
    private static final byte[] defaultExifVersion = new byte[]{48, 50, 50, 48};

    @Nullable public Iso iso                              = null;
    @Nullable public ShutterSpeed exposureTime            = null;
    @Nullable public Double aperture                      = null;
    @Nullable public Ev exposureBias                      = null;
    @Nullable public ExifFlash flash                      = null;
    @Nullable public Float focalLength                    = null;

    @NonNull  public byte[] originalMakerNote             = new byte[0];

    private String make;
    private String model;

    public ExifMetadata(ExtraCameraInfo cameraInfo, DeviceInfo deviceInfo)
    {
        model = cameraInfo.getModel();
        make  = deviceInfo.getManufacturer();
    }

    public void writeInfoTo(DngNegative negative)
    {
        ExifTagWriter exifWriter = new DngExifTagWriter(negative);

        exifWriter.writeExifVersionTag(defaultExifVersion);
        exifWriter.writeDateTagsAsCurrentDate();
        exifWriter.writeMakeTag(make);
        exifWriter.writeModelTag(model);
        exifWriter.writeSoftwareTag(AppPackageUtil.getAppNameWithVersion());
        exifWriter.writeMakerNoteTag(originalMakerNote);

        if (iso != null)
            exifWriter.writeISOTag(iso);

        if (exposureTime != null)
            exifWriter.writeExposureTimeTags(exposureTime);

        if (aperture != null)
            exifWriter.writeApertureTags(aperture);

        if (exposureBias != null)
            exifWriter.writeExposureBiasTag(exposureBias);

        if (flash != null && flash != ExifFlash.UNKNOWN)
            exifWriter.writeFlashTag(flash);

        if (focalLength != null)
            exifWriter.writeFocalLengthTag(focalLength);
    }
}
