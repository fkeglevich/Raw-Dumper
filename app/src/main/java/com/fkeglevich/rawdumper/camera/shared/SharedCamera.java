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

package com.fkeglevich.rawdumper.camera.shared;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.RawImageCallbackAccess;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

public class SharedCamera
{
    private final ICameraExtension          cameraExtension;
    private final Camera.CameraInfo         cameraInfo;
    private final int                       cameraId;
    private final ExtraCameraInfo           extraCameraInfo;
    private final RawImageCallbackAccess    rawImageCallbackAccess;
    private final SharedParameters          parameters;

    SharedCamera(ICameraExtension cameraExtension, Camera.CameraInfo cameraInfo, int cameraId, ExtraCameraInfo extraCameraInfo)
    {
        this.cameraExtension = cameraExtension;
        this.cameraInfo = cameraInfo;
        this.cameraId = cameraId;
        this.extraCameraInfo = extraCameraInfo;
        this.rawImageCallbackAccess = new RawImageCallbackAccess(getCamera());
        this.parameters = new SharedParameters(getCameraExtension().getCameraDevice());
    }

    public ICameraExtension getCameraExtension()
    {
        return cameraExtension;
    }

    public Camera getCamera()
    {
        return cameraExtension.getCameraDevice();
    }

    public Camera.CameraInfo getCameraInfo()
    {
        return cameraInfo;
    }

    public int getCameraId()
    {
        return cameraId;
    }

    public ExtraCameraInfo getExtraCameraInfo()
    {
        return extraCameraInfo;
    }

    public RawImageCallbackAccess getRawImageCallbackAccess()
    {
        return rawImageCallbackAccess;
    }

    public SharedParameters getParameters()
    {
        return parameters;
    }
}
