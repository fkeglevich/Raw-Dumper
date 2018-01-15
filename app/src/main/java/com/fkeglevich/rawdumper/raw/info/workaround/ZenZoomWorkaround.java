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

package com.fkeglevich.rawdumper.raw.info.workaround;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

/*
 * Created by flavio on 15/01/18.
 */

public class ZenZoomWorkaround implements DeviceWorkaround
{
    @Override
    public void applyWorkaroundIfNeeded(DeviceInfo deviceInfo)
    {
        apply(deviceInfo);
    }

    private void apply(DeviceInfo deviceInfo)
    {
        ExtraCameraInfo[] extraCameraInfos = deviceInfo.getCameras();
        for (ExtraCameraInfo cameraInfo : extraCameraInfos)
        {
            if (!DebugFlag.isForceRawZenfoneZoom() && isRearCamera(cameraInfo))
                cameraInfo.getSensor().disableRaw();

            if (isFrontCamera(cameraInfo))
                cameraInfo.getExposure().getShutterSpeedValues().clear();
        }
    }

    private boolean isRearCamera(ExtraCameraInfo cameraInfo)
    {
        return cameraInfo.getFacing() == Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    private boolean isFrontCamera(ExtraCameraInfo cameraInfo)
    {
        return cameraInfo.getFacing() == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }
}
