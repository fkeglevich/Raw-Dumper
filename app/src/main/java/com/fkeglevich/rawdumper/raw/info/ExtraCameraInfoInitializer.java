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

package com.fkeglevich.rawdumper.raw.info;

import android.hardware.Camera;
import android.util.Log;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 13/11/17.
 */

class ExtraCameraInfoInitializer
{
    ExtraCameraInfo[] initializeCameras(ExtraCameraInfo[] cameras)
    {
        int numCameras = cameras.length;
        int realNumCameras = Camera.getNumberOfCameras();

        if (realNumCameras == numCameras)
            return initCamerasNormally(cameras);
        else if (realNumCameras == 1 && numCameras == 2)
            return initFirstCameraOnly(cameras);
        else
            throw new RuntimeException("Mismatched number of cameras, expected: " + numCameras + " found: " + realNumCameras);
    }

    private ExtraCameraInfo[] initFirstCameraOnly(ExtraCameraInfo[] cameras)
    {
        Log.w("DeviceInfo", "Found only one camera");

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(0, cameraInfo);

        ExtraCameraInfo workingCam = cameraInfo.facing == CAMERA_FACING_BACK ? cameras[0] : cameras[1];

        workingCam.fixId(0);
        workingCam.runtimeInit();
        return new ExtraCameraInfo[] {workingCam};
    }

    private ExtraCameraInfo[] initCamerasNormally(ExtraCameraInfo[] cameras)
    {
        for (ExtraCameraInfo cameraInfo : cameras)
            cameraInfo.runtimeInit();

        return cameras;
    }
}
