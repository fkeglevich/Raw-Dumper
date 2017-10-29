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

package com.fkeglevich.rawdumper.camera.async.direct;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.action.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.data.FocusArea;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.helper.FocusHelper;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class LowLevelCameraActions implements CameraActions
{
    private final ICameraExtension cameraExtension;
    private final Object lock;

    LowLevelCameraActions(ICameraExtension cameraExtension, Object lock)
    {
        this.cameraExtension = cameraExtension;
        this.lock = lock;
    }

    @Override
    public void startAutoFocus(FocusArea focusArea, final AutoFocusResult callback)
    {
        List<Camera.Area> areas = FocusHelper.generateFocusAreas(focusArea);
        synchronized (lock)
        {
            Camera camera = cameraExtension.getCameraDevice();
            setAreasParameters(areas, camera);
            camera.autoFocus(new Camera.AutoFocusCallback()
            {
                @Override
                public void onAutoFocus(boolean success, Camera camera)
                {
                    callback.autoFocusDone(success);
                }
            });
        }
    }

    private void setAreasParameters(List<Camera.Area> areas, Camera camera)
    {
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getMaxNumFocusAreas() >= 1)
            parameters.setFocusAreas(areas);
        if (parameters.getMaxNumMeteringAreas() >= 1)
            parameters.setMeteringAreas(areas);
        camera.setParameters(parameters);
    }
}
