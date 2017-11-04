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

package com.fkeglevich.rawdumper.controller.orientation;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

/**
 * Created by Flávio Keglevich on 27/08/2017.
 * TODO: Add a class header comment!
 */

public class OrientationManager
{
    private static final OrientationManager instance = new OrientationManager();

    public static OrientationManager getInstance()
    {
        return instance;
    }

    private OrientationEventListener orientationListener = null;
    private int lastDegrees = OrientationEventListener.ORIENTATION_UNKNOWN;

    void setup(Context context)
    {
        disable();
        orientationListener = new OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI)
        {
            @Override
            public void onOrientationChanged(int orientation)
            {
                lastDegrees = orientation;
            }
        };
        enable();
    }

    void disable()
    {
        if (orientationListener != null)
            orientationListener.disable();
    }

    void enable()
    {
        if (orientationListener != null)
            orientationListener.enable();
    }

    public ImageOrientation getImageOrientation(CameraContext cameraContext)
    {
        int cameraOrientation = cameraContext.getCameraInfo().getOrientation();
        int degrees;
        if (lastDegrees != OrientationEventListener.ORIENTATION_UNKNOWN)
            degrees = (lastDegrees + cameraOrientation) % 360;
        else
            degrees = cameraOrientation % 360;

        if (degrees >= 45 && degrees < 135)
            return ImageOrientation.RIGHTTOP;
        if (degrees >= 135 && degrees < 225)
            return ImageOrientation.BOTRIGHT;
        if (degrees >= 225 && degrees < 315)
            return ImageOrientation.LEFTBOT;
        else
            return ImageOrientation.TOPLEFT;
    }

    public int getCameraRotation(CameraContext cameraContext)
    {
        if (lastDegrees == OrientationEventListener.ORIENTATION_UNKNOWN)
            return 0;

        int orientation = (lastDegrees + 45) / 90 * 90;

        int facing = cameraContext.getCameraInfo().getFacing();
        int infoOrientation = cameraContext.getCameraInfo().getOrientation();

        if (facing == CAMERA_FACING_FRONT)
            return (infoOrientation - orientation + 360) % 360;
        else
            return (infoOrientation + orientation) % 360;
    }
}
