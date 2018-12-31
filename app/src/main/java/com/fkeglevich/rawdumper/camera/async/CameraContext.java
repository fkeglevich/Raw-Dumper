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

import android.graphics.SurfaceTexture;

import com.fkeglevich.rawdumper.gl.GLService;
import com.fkeglevich.rawdumper.raw.capture.RawSettings;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;
import com.fkeglevich.rawdumper.raw.info.LensInfo;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/10/17.
 */

public abstract class CameraContext
{
    abstract public DeviceInfo getDeviceInfo();
    abstract public SurfaceTexture getSurfaceTexture();
    abstract public CameraSelector getCameraSelector();
    abstract public GLService getGlService();

    private final RawSettings rawSettings = new RawSettings();

    public ExtraCameraInfo getCameraInfo()
    {
        DeviceInfo deviceInfo = getDeviceInfo();
        CameraSelector cameraSelector = getCameraSelector();
        return deviceInfo.getCameras()[cameraSelector.getSelectedCameraId()];
    }

    public SensorInfo getSensorInfo()
    {
        return getCameraInfo().getSensor();
    }

    public ColorInfo getColorInfo()
    {
        return getCameraInfo().getColor();
    }

    public ExposureInfo getExposureInfo()
    {
        return getCameraInfo().getExposure();
    }

    public LensInfo getLensInfo()
    {
        return getCameraInfo().getLens();
    }

    public RawSettings getRawSettings()
    {
        return rawSettings;
    }
}
