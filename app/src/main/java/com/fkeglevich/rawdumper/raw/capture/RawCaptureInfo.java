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

package com.fkeglevich.rawdumper.raw.capture;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.pipeline.filename.FilenameBuilder;
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.io.Directories;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.image.RawImage;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.raw.info.LensInfo;
import com.fkeglevich.rawdumper.raw.metadata.ExifMetadata;

import java.io.File;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class RawCaptureInfo
{
    private final CameraContext cameraContext;
    private final RawImage rawImage;
    private final RawSettings rawSettings         = new RawSettings();
    private final File destinationFile            = initDestinationFile();

    @Nullable private final Camera.Parameters parameters;

    private WhiteBalanceInfo whiteBalanceInfoCache = null;

    public RawCaptureInfo(CameraContext cameraContext, RawImage rawImage, @Nullable Camera.Parameters parameters)
    {
        this.cameraContext  = cameraContext;
        this.rawImage       = rawImage;
        this.parameters     = parameters;
        this.rawSettings.getDataFrom(cameraContext.getRawSettings());
    }

    public DeviceInfo getDeviceInfo()
    {
        return cameraContext.getDeviceInfo();
    }

    public ExtraCameraInfo getCameraInfo()
    {
        return cameraContext.getCameraInfo();
    }

    public LensInfo getLensInfo()
    {
        return getCameraInfo().getLens();
    }

    public byte[] getImageData()
    {
        return rawImage.getData();
    }

    public RawImageSize getImageSize()
    {
        return rawImage.getSize();
    }

    public RawSettings getRawSettings()
    {
        return rawSettings;
    }

    public File getDestinationFile()
    {
        return destinationFile;
    }

    public boolean shouldInvertRows()
    {
        return rawSettings.shouldInvertRows(this.getCameraInfo());
    }

    @NonNull public abstract MakerNoteInfo getMakerNoteInfo();

    @NonNull public abstract ExifMetadata getExifMetadata();

    public abstract void writeInfoTo(DngNegative negative);

    public WhiteBalanceInfo getWhiteBalanceInfo()
    {
        if (whiteBalanceInfoCache == null)
            whiteBalanceInfoCache = WhiteBalanceInfo.create(getCameraInfo(), getMakerNoteInfo(), parameters);

        return whiteBalanceInfoCache;
    }

    private File initDestinationFile()
    {
        String fileName = new FilenameBuilder()
                .useCalendar(GregorianCalendar.getInstance())
                .useFileFormat(FileFormat.DNG)
                .isPicture()
                .build();

        return new File(Directories.getPicturesDirectory(), fileName);
    }
}
