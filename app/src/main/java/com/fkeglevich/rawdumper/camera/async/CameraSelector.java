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

import com.fkeglevich.rawdumper.camera.extension.CameraSelectorAccess;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 01/10/17.
 */

public abstract class CameraSelector
{
    private int currentCameraId;

    protected CameraSelector(int cameraId)
    {
        currentCameraId = cameraId;
    }

    public synchronized void selectNextCamera()
    {
        currentCameraId++;
        if (currentCameraId >= getNumOfCameras())
            currentCameraId = 0;
    }

    public synchronized boolean hasMultipleCameras()
    {
        return getNumOfCameras() > 1;
    }

    synchronized int getSelectedCameraId()
    {
        return currentCameraId;
    }

    public CameraSelectorAccess getAccess()
    {
        return new CameraSelectorAccess()
        {
            @Override
            protected int getSelectedCameraId()
            {
                return CameraSelector.this.getSelectedCameraId();
            }
        };
    }

    protected abstract int getNumOfCameras();
}
