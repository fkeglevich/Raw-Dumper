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

import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.intel.camera.extensions.IntelCamera;

/**
 * Created by Flávio Keglevich on 29/06/2017.
 * TODO: Add a class header comment!
 */

class CameraLock
{
    private IntelCamera intelCamera = null;
    private Camera.CameraInfo cameraInfo = null;

    CameraLock()
    {   }

    IntelCamera getCamera()
    {
        ThreadUtil.checkIfSynchronized(this);
        return intelCamera;
    }

    void setCamera(IntelCamera intelCamera)
    {
        ThreadUtil.checkIfSynchronized(this);
        this.intelCamera = intelCamera;
    }

    Camera.CameraInfo getCameraInfo()
    {
        ThreadUtil.checkIfSynchronized(this);
        return cameraInfo;
    }

    void setCameraInfo(Camera.CameraInfo cameraInfo)
    {
        ThreadUtil.checkIfSynchronized(this);
        this.cameraInfo = cameraInfo;
    }
}
