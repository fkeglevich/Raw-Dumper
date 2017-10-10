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

package com.fkeglevich.rawdumper.camera.setup;

import android.graphics.SurfaceTexture;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.CameraSelector;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/10/17.
 */

class SetupStageCameraContext extends CameraContext
{
    private final DeviceInfo deviceInfo;
    private final SurfaceTexture surfaceTexture;
    private final CameraSelector cameraSelector;

    SetupStageCameraContext(DeviceInfo deviceInfo, SurfaceTexture surfaceTexture, CameraSelector cameraSelector)
    {
        this.deviceInfo = deviceInfo;
        this.surfaceTexture = surfaceTexture;
        this.cameraSelector = cameraSelector;
    }

    @Override
    public DeviceInfo getDeviceInfo()
    {
        return deviceInfo;
    }

    @Override
    public SurfaceTexture getSurfaceTexture()
    {
        return surfaceTexture;
    }

    @Override
    public CameraSelector getCameraSelector()
    {
        return cameraSelector;
    }
}
