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

import androidx.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.controller.orientation.OrientationManager;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

public class RawSettings
{
    public volatile boolean shouldInvertFrontCameraRows = true;
    public volatile boolean keepLensVignetting          = false;
    public volatile boolean compressRawFiles            = true;
    public volatile boolean useOrientationFromPhone     = true;

    public int getOrientationCode(CaptureInfo captureInfo)
    {
        if (useOrientationFromPhone)
        {
            CameraContext cameraContext = captureInfo.cameraContext;
            return OrientationManager.getInstance().getImageOrientation(cameraContext, shouldInvertRows(captureInfo)).getExifCode();
        }
        else
            return ImageOrientation.TOPLEFT.getExifCode();
    }

    boolean shouldInvertRows(CaptureInfo captureInfo)
    {
        boolean isFrontCamera = captureInfo.cameraContext.getCameraInfo().getFacing() == CAMERA_FACING_FRONT;
        return isFrontCamera && shouldInvertFrontCameraRows;
    }

    public void getDataFrom(RawSettings rawSettings)
    {
        shouldInvertFrontCameraRows = rawSettings.shouldInvertFrontCameraRows;
        keepLensVignetting          = rawSettings.keepLensVignetting;
        compressRawFiles            = rawSettings.compressRawFiles;
        useOrientationFromPhone     = rawSettings.useOrientationFromPhone;
    }

    @NonNull
    @Override
    public String toString()
    {
        return "[RawSettings shouldInvertFrontCameraRows=" + shouldInvertFrontCameraRows + ", " +
                "keepLensVignetting=" + keepLensVignetting + ", " +
                "compressRawFiles=" + compressRawFiles + ", " +
                "useOrientationFromPhone=" + useOrientationFromPhone + "]";
    }
}