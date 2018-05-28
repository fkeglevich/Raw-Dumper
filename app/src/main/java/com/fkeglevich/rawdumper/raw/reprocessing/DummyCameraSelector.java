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

import com.fkeglevich.rawdumper.camera.async.CameraSelector;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 27/05/18.
 */
class DummyCameraSelector extends CameraSelector
{
    private final int numCameras;

    protected DummyCameraSelector(DeviceInfo deviceInfo, int cameraId)
    {
        super(cameraId);
        numCameras = deviceInfo.getCameras().length;
    }

    @Override
    protected int getNumOfCameras()
    {
        return numCameras;
    }
}
