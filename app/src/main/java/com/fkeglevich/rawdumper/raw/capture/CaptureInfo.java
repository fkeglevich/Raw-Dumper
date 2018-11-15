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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.dng.writer.DngNegative;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.io.File;

/**
 * Created by Flávio Keglevich on 09/06/2017.
 * TODO: Add a class header comment!
 */

public class CaptureInfo
{
    //Required fields
    public CameraContext cameraContext          = null;
    public DeviceInfo device                    = null;
    public ExtraCameraInfo camera               = null;
    public DateInfo date                        = null;
    public WhiteBalanceInfo whiteBalanceInfo    = null;
    public RawImageSize imageSize               = null;
    public String originalRawFilename           = null;
    public String destinationRawFilename        = null;
    public RawSettings rawSettings              = new RawSettings();

    //Optional fields
    public MakerNoteInfo makerNoteInfo          = null;
    public Camera.Parameters captureParameters  = null;
    public byte[] extraJpegBytes                = null;
    public byte[] rawDataBytes                  = null;
    public byte[] extraBuffer                   = null;
    public File relatedI3av4File                = null;

    public boolean isValid()
    {
        return  device                  != null &&
                camera                  != null &&
                date                    != null &&
                whiteBalanceInfo        != null &&
                imageSize               != null &&
                originalRawFilename     != null &&
                destinationRawFilename  != null;
    }

    public void writeInfoTo(DngNegative negative)
    {
        negative.setOriginalRawFileName(originalRawFilename);
        //negative.setSoftware(AppPackageUtil.getAppNameWithVersion());
    }

    public boolean shouldInvertRows()
    {
        return rawSettings.shouldInvertRows(this);
    }
}
