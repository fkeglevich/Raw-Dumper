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

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.io.IOException;

/**
 * Created by Flávio Keglevich on 15/08/2017.
 * TODO: Add a class header comment!
 */

public class PreviewHelper
{
    /*It is always zero because the app is always in portrait mode
      This could change in future versions */
    private static final int APP_ORIENTATION_DEGREES = 0;

    public static int setupPreviewTexture(CameraContext cameraContext, Camera camera) throws IOException
    {
        int rotation = setCameraDisplayOrientation(cameraContext.getCameraInfo(), camera, APP_ORIENTATION_DEGREES);
        camera.setPreviewTexture(cameraContext.getSurfaceTexture());
        return rotation;
    }

    private static int setCameraDisplayOrientation(ExtraCameraInfo cameraInfo, Camera camera, int degrees)
    {
        int facing = cameraInfo.getFacing();
        int orientation = cameraInfo.getOrientation();

        int result;
        if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else // back-facing
        {
            result = (orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        return result;
    }
}
