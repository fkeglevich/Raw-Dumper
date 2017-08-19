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

package com.fkeglevich.rawdumper.camera.async;

import android.hardware.Camera;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.AvailableCaptureSizes;
import com.fkeglevich.rawdumper.camera.CaptureSize;
import com.fkeglevich.rawdumper.camera.shared.SharedCamera;

/**
 * Created by Flávio Keglevich on 15/08/2017.
 * TODO: Add a class header comment!
 */

public class CameraSetup
{
    private final CameraAccess cameraAccess;

    CameraSetup(CameraAccess cameraAccess)
    {
        this.cameraAccess = cameraAccess;
    }

    void setupCameraParameters(SharedCameraGetter cameraGetter)
    {
        synchronized (cameraGetter.getSharedCamera())
        {
            SharedCamera sharedCamera = cameraGetter.getSharedCamera().get();
            Camera camera = sharedCamera.getCamera();

            //Setting camera parameters block
            Camera.Parameters parameters = camera.getParameters();
            {
                //Raw picture format setup
                parameters.set("raw-data-format", "bayer");
                //parameters.set("mode", "LL");
                //parameters.set("ultra_pixels_mode", "on");

                Log.i("SET RAW", "ASD");

                //Simple fix for the green postview (when Intel features are available)
                if (sharedCamera.getCameraExtension().hasIntelFeatures())
                {
                    parameters.set("burst-start-index", "-1");
                    parameters.set("burst-length", "1");
                }

                //Helper AvailableCaptureSizes object
                AvailableCaptureSizes availableCaptureSizes = new AvailableCaptureSizes(parameters);

                //Setting picture size
                //CaptureSize largestPicSize = new CaptureSize(1600, 1200);//availableCaptureSizes.getLargestPictureSize();
                CaptureSize largestPicSize = availableCaptureSizes.getLargestPictureSize();
                parameters.setPictureSize(largestPicSize.getWidth(), largestPicSize.getHeight());

                //Setting preview size
                CaptureSize largestPrevSize = availableCaptureSizes.getLargestCompatiblePreviewSize(largestPicSize);
                parameters.setPreviewSize(largestPrevSize.getWidth(), largestPrevSize.getHeight());
            }
            camera.setParameters(parameters);
        }
    }
}
