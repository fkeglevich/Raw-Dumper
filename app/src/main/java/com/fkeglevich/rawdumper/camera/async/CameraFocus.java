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

import android.graphics.Rect;
import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.async.callbacks.IAutoFocusCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flávio Keglevich on 08/07/2017.
 * TODO: Add a class header comment!
 */

public class CameraFocus
{
    private final CameraAccess cameraAccess;
    CameraFocus(CameraAccess cameraAccess)
    {
        this.cameraAccess = cameraAccess;
    }

    public void touchFocusAsync(int x, int y, int viewWidth, int viewHeight, int touchSize, IAutoFocusCallback autoFocusCallback)
    {
        Rect focusRect = calculateFocusRect(x, y, viewWidth, viewHeight, touchSize);

        List<Camera.Area> focusList = new ArrayList<>();
        focusList.add(new Camera.Area(focusRect, 1000));

        synchronized (cameraAccess.cameraLock)
        {
            if (cameraAccess.cameraLock.getCamera() != null)
            {
                Camera camera = cameraAccess.cameraLock.getCamera().getCameraDevice();
                Camera.Parameters parameters = camera.getParameters();
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();

                if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    parameters.setFocusAreas(focusList);
                    parameters.setMeteringAreas(focusList);
                    camera.setParameters(parameters);

                    cameraAccess.autoFocusAsync(autoFocusCallback);
                }
            }
        }
    }

    public void touchFocusAsync(int x, int y, int viewWidth, int viewHeight, IAutoFocusCallback autoFocusCallback)
    {
        touchFocusAsync(x, y, viewWidth, viewHeight, 100, autoFocusCallback);
    }

    private Rect calculateFocusRect(int x, int y, int viewWidth, int viewHeight, int touchSize)
    {
        Rect touchRect = new Rect(x - touchSize, y - touchSize, x + touchSize, y + touchSize);
        Rect focusRect = new Rect(
                touchRect.left * 2000/viewWidth - 1000,
                touchRect.top * 2000/viewHeight - 1000,
                touchRect.right * 2000/viewWidth - 1000,
                touchRect.bottom * 2000/viewHeight - 1000);
        correctFocusRect(focusRect);
        return focusRect;
    }

    private void correctFocusRect(Rect focusRect)
    {
        int width = focusRect.width(), height = focusRect.height();

        if (focusRect.left < -1000)
        {
            focusRect.left = -1000;
            focusRect.right = focusRect.left + width;
        }
        if (focusRect.right > 1000)
        {
            focusRect.right = 1000;
            focusRect.left = focusRect.right - width;
        }
        if (focusRect.top < -1000)
        {
            focusRect.top = -1000;
            focusRect.bottom = focusRect.top + height;
        }
        if (focusRect.bottom > 1000)
        {
            focusRect.bottom = 1000;
            focusRect.top = focusRect.bottom - height;
        }
    }
}
