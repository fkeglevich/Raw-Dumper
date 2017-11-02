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
import com.fkeglevich.rawdumper.camera.data.DataFormat;
import com.fkeglevich.rawdumper.camera.data.PicFormat;
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.IntelParameters;
import com.fkeglevich.rawdumper.camera.helper.FocusHelper;
import com.fkeglevich.rawdumper.camera.parameter.PictureSizeParameterCollection;

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
    private final int displayRotation;
    private final PictureSizeParameterCollection pictureSizeParameterCollection;

    private boolean isPreviewing = false;

    LowLevelCameraActions(ICameraExtension cameraExtension, Object lock, int displayRotation, PictureSizeParameterCollection pictureSizeParameterCollection)
    {
        this.cameraExtension = cameraExtension;
        this.lock = lock;
        this.displayRotation = displayRotation;
        this.pictureSizeParameterCollection = pictureSizeParameterCollection;
    }

    @Override
    public void startAutoFocus(PreviewArea focusArea, final AutoFocusResult callback)
    {
        List<Camera.Area> areas = FocusHelper.generateFocusAreas(focusArea.rotate(displayRotation));
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

    @Override
    public void stopPreview()
    {
        synchronized (lock)
        {
            if (isPreviewing)
                cameraExtension.getCameraDevice().stopPreview();
            isPreviewing = false;
        }
    }

    @Override
    public void startPreview()
    {
        synchronized (lock)
        {
            if (!isPreviewing)
                cameraExtension.getCameraDevice().startPreview();
            isPreviewing = true;
        }
    }

    @Override
    public void setMode(Mode mode)
    {
        synchronized (lock)
        {
            Camera.Parameters parameters = cameraExtension.getCameraDevice().getParameters();
            parameters.set(AsusParameters.ASUS_MODE, mode.getParameterValue());
            parameters.set("image_optimize", "off");
            parameters.set(AsusParameters.ASUS_ULTRA_PIXELS, mode.isUseUltraPixels() ? "on" : "off");
            parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, IntelParameters.RAW_DATA_FORMAT_NONE);
            cameraExtension.getCameraDevice().setParameters(parameters);
        }
    }

    @Override
    public void setPictureFormat(PicFormat pictureFormat)
    {
        synchronized (lock)
        {
            if (pictureFormat.getFormat() == DataFormat.RAW)
                pictureSizeParameterCollection.enableRawMode();
            else
                pictureSizeParameterCollection.disableRawMode();

            Camera.Parameters parameters = cameraExtension.getCameraDevice().getParameters();
            parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, pictureFormat.getFormat().getParameterValue());
            cameraExtension.getCameraDevice().setParameters(parameters);
        }
    }
}
