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

package com.fkeglevich.rawdumper.raw.reprocessing;

import android.graphics.SurfaceTexture;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.CameraSelector;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 27/05/18.
 */
public class DummyCameraContext extends CameraContext
{
    private final DeviceInfo deviceInfo;
    private final CameraSelector cameraSelector;

    public DummyCameraContext(DeviceInfo deviceInfo, int cameraId)
    {
        this.deviceInfo = deviceInfo;
        this.cameraSelector = new DummyCameraSelector(deviceInfo, cameraId);
        this.getRawSettings().useOrientationFromPhone = false;
    }

    @Override
    public DeviceInfo getDeviceInfo()
    {
        return deviceInfo;
    }

    @Override
    public SurfaceTexture getSurfaceTexture()
    {
        return null;
    }

    @Override
    public CameraSelector getCameraSelector()
    {
        return cameraSelector;
    }
}
