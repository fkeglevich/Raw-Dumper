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

import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.TextureView;

import com.fkeglevich.rawdumper.camera.CaptureSize;
import com.fkeglevich.rawdumper.camera.CaptureSizes;

import java.io.IOException;
import java.util.List;

/**
 * Created by Flávio Keglevich on 07/07/2017.
 * TODO: Add a class header comment!
 */

public class CameraConfig
{
    private final CameraAccess cameraAccess;

    CameraConfig(CameraAccess cameraAccess)
    {
        this.cameraAccess = cameraAccess;
    }

    public void setupCamera()
    {
        synchronized (cameraAccess.cameraLock)
        {
            Camera camera = cameraAccess.cameraLock.getCamera().getCameraDevice();

            //Setting camera parameters block
            Camera.Parameters parameters = camera.getParameters();
            {
                //Auto focus setup if camera has auto focus
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

                //Raw picture format setup
                parameters.set("raw-data-format", "bayer");
                parameters.set("mode", "PRO");

                //Simple fix for the green postview
                parameters.set("burst-start-index", "-1");

                //Flash mode setup
                //This check should be unnecessary since every camera should support the OFF flash mode
                List<String> flashModes = parameters.getSupportedFlashModes();
                if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_OFF))
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                //Helper CaptureSizes object
                CaptureSizes captureSizes = new CaptureSizes(parameters);

                //Setting picture size
                CaptureSize largestPicSize = captureSizes.getLargestPictureSize();
                parameters.setPictureSize(largestPicSize.getWidth(), largestPicSize.getHeight());

                //Setting preview size
                CaptureSize largestPrevSize = captureSizes.getLargestCompatiblePreviewSize(largestPicSize);
                parameters.setPreviewSize(largestPrevSize.getWidth(), largestPrevSize.getHeight());
            }
            camera.setParameters(parameters);
        }
    }

    public void setupPreviewTexture(TextureView textureView)
    {
        synchronized (cameraAccess.cameraLock)
        {
            Camera camera = cameraAccess.cameraLock.getCamera().getCameraDevice();

            //Degrees is always zero because the app is always in portrait mode
            //This could change in future versions
            setCameraDisplayOrientation(camera, 0);

            //Setting the preview texture
            try
            { camera.setPreviewTexture(textureView.getSurfaceTexture()); }
            catch (IOException e)
            { throw new RuntimeException("Exception during setPreviewTexture!", e); }

            Camera.Size previewSize = camera.getParameters().getPreviewSize();

            Matrix matrix = new Matrix();
            matrix.setScale(1, (float)previewSize.height / (float)previewSize.width);
            textureView.setTransform(matrix);
        }
    }

    public String dumpParameters()
    {
        synchronized (cameraAccess.cameraLock)
        {
            return cameraAccess.cameraLock.getCamera().getCameraDevice().getParameters().flatten();
        }
    }


    private void setCameraDisplayOrientation(Camera camera, int degrees)
    {
        if (!Thread.holdsLock(cameraAccess.cameraLock))
            throw new RuntimeException("This method should be called inside a proper synchronized block!");

        Camera.CameraInfo cameraInfo = cameraAccess.cameraLock.getCameraInfo();

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

        camera.setDisplayOrientation(result);
    }
}
