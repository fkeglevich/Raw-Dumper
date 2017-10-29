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

package com.fkeglevich.rawdumper.camera.helper;

import android.graphics.Rect;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flávio Keglevich on 15/08/2017.
 * TODO: Add a class header comment!
 */

public class FocusHelper
{
    private static final int MIN_FOCUS_RECT_POSITION = -1000;
    private static final int MAX_FOCUS_RECT_POSITION =  1000;
    private static final int MAX_FOCUS_RECT_SIZE =      2000;

    private static final int DEFAULT_AREA_WEIGHT = 1000;

    public static List<Camera.Area> generateFocusAreas(int x, int y, int viewWidth, int viewHeight, int touchSize)
    {
        List<Camera.Area> areaList = new ArrayList<>();
        Rect focusRect = calculateFocusRect(x, y, viewWidth, viewHeight, touchSize);
        areaList.add(new Camera.Area(focusRect, DEFAULT_AREA_WEIGHT));
        return areaList;
    }

    private static Rect calculateFocusRect(int x, int y, int viewWidth, int viewHeight, int touchSize)
    {
        Rect touchRect = new Rect(x - touchSize, y - touchSize, x + touchSize, y + touchSize);
        Rect focusRect = new Rect(
                touchRect.left * MAX_FOCUS_RECT_SIZE/viewWidth - MAX_FOCUS_RECT_POSITION,
                touchRect.top * MAX_FOCUS_RECT_SIZE/viewHeight - MAX_FOCUS_RECT_POSITION,
                touchRect.right * MAX_FOCUS_RECT_SIZE/viewWidth - MAX_FOCUS_RECT_POSITION,
                touchRect.bottom * MAX_FOCUS_RECT_SIZE/viewHeight - MAX_FOCUS_RECT_POSITION);
        correctFocusRect(focusRect);
        return focusRect;
    }

    private static void correctFocusRect(Rect focusRect)
    {
        int width = focusRect.width(), height = focusRect.height();
        if (focusRect.left < MIN_FOCUS_RECT_POSITION)
        {
            focusRect.left = MIN_FOCUS_RECT_POSITION;
            focusRect.right = focusRect.left + width;
        }
        if (focusRect.right > MAX_FOCUS_RECT_POSITION)
        {
            focusRect.right = MAX_FOCUS_RECT_POSITION;
            focusRect.left = focusRect.right - width;
        }
        if (focusRect.top < MIN_FOCUS_RECT_POSITION)
        {
            focusRect.top = MIN_FOCUS_RECT_POSITION;
            focusRect.bottom = focusRect.top + height;
        }
        if (focusRect.bottom > MAX_FOCUS_RECT_POSITION)
        {
            focusRect.bottom = MAX_FOCUS_RECT_POSITION;
            focusRect.top = focusRect.bottom - height;
        }
    }
}

/*
Rect focusRect = calculateFocusRect(x, y, viewWidth, viewHeight, touchSize);

        List<Camera.Area> focusList = new ArrayList<>();
        focusList.add(new Camera.Area(focusRect, 1000));

        synchronized (cameraAccess.cameraLock)
        {
            if (cameraAccess.cameraLock.getCameraExtension() != null)
            {
                Camera camera = cameraAccess.cameraLock.getCameraExtension().getCameraDevice();
                Camera.Parameters parameters = camera.getParameters();
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();

                if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    parameters.setFocusAreas(focusList);
                    parameters.setMeteringAreas(focusList);
                    camera.setParameters(parameters);

                    //cameraAccess.autoFocusAsync(autoFocusCallback);
                }
            }
        }
 */