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

/**
 * Created by Flávio Keglevich on 08/07/2017.
 * TODO: Add a class header comment!
 */

public class CameraFlash
{
    private final CameraAccess cameraAccess;
    CameraFlash(CameraAccess cameraAccess)
    {
        this.cameraAccess = cameraAccess;
    }

    public boolean getFlashValue()
    {
        synchronized (cameraAccess.cameraLock)
        {
            return !cameraAccess.cameraLock.getCamera().getCameraDevice().getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF);
        }
    }

    public void setFlashValue(boolean value)
    {
        synchronized (cameraAccess.cameraLock)
        {
            Camera.Parameters parameters = cameraAccess.cameraLock.getCamera().getCameraDevice().getParameters();
            parameters.setFlashMode(value ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
            cameraAccess.cameraLock.getCamera().getCameraDevice().setParameters(parameters);
        }
    }

    public boolean hasFlash()
    {
        synchronized (cameraAccess.cameraLock)
        {
            return cameraAccess.cameraLock.getCamera().getCameraDevice().getParameters().getSupportedFocusModes().size() > 1;
        }
    }
}
