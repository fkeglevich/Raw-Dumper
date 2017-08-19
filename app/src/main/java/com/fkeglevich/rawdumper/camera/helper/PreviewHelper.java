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

import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.TextureView;

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.camera.shared.SharedCamera;

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

    public static void setupPreviewTexture(TextureView textureView, Locked<SharedCamera> lockedCamera) throws IOException
    {
        synchronized (lockedCamera.getLock())
        {
            setCameraDisplayOrientation(lockedCamera, APP_ORIENTATION_DEGREES);
            lockedCamera.get().getCamera().setPreviewTexture(textureView.getSurfaceTexture());

            Camera.Size previewSize = lockedCamera.get().getCamera().getParameters().getPreviewSize();

            Matrix matrix = new Matrix();
            matrix.setScale(1, (float)previewSize.height / (float)previewSize.width);
            textureView.setTransform(matrix);
        }
    }

    private static void setCameraDisplayOrientation(Locked<SharedCamera> lockedCamera, int degrees)
    {
        Camera.CameraInfo cameraInfo = lockedCamera.get().getCameraInfo();

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else // back-facing
        {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        lockedCamera.get().getCamera().setDisplayOrientation(result);
    }
}
