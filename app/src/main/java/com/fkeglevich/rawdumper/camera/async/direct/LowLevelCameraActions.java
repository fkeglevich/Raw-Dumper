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

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.action.listener.AutoFocusResult;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.pipeline.PipelineManager;
import com.fkeglevich.rawdumper.camera.data.DataFormat;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.PreviewArea;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.IntelParameters;
import com.fkeglevich.rawdumper.camera.helper.FocusHelper;
import com.fkeglevich.rawdumper.camera.parameter.PictureSizeLayer;
import com.fkeglevich.rawdumper.camera.service.available.WhiteBalanceService;
import com.fkeglevich.rawdumper.util.Mutable;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 28/10/17.
 */

public class LowLevelCameraActions implements CameraActions
{
    private final Mutable<ICameraExtension> cameraExtension;
    private final Object lock;
    private final PictureSizeLayer pictureSizeLayer;
    private final PipelineManager pipelineManager;

    //Mutable state fields
    private boolean isPreviewing;
    private int displayRotation;
    private PreviewArea.FlipType flipType;

    static LowLevelCameraActions createInvalid(Mutable<ICameraExtension> cameraExtension,
                                               Object lock,
                                               PictureSizeLayer pictureSizeLayer,
                                               PipelineManager pipelineManager)
    {
        return new LowLevelCameraActions(cameraExtension, lock, pictureSizeLayer, pipelineManager);
    }

    private LowLevelCameraActions(Mutable<ICameraExtension> cameraExtension,
                          Object lock,
                          PictureSizeLayer pictureSizeLayer,
                          PipelineManager pipelineManager)
    {
        this.cameraExtension = cameraExtension;
        this.lock = lock;
        this.pictureSizeLayer = pictureSizeLayer;
        this.pipelineManager = pipelineManager;
    }

    void setupMutableState(int displayRotation, PreviewArea.FlipType flipType)
    {
        synchronized (lock)
        {
            this.displayRotation = displayRotation;
            this.flipType = flipType;
            this.isPreviewing = false;
        }
    }

    @Override
    public void startAutoFocus(PreviewArea focusArea, final AutoFocusResult callback)
    {
        synchronized (lock)
        {
            setMeteringArea(focusArea);
            getCamera().autoFocus((success, camera) -> callback.autoFocusDone(success));
        }
    }

    @Override
    public void cancelAutoFocus()
    {
        synchronized (lock)
        {
            Camera camera = getCamera();
            camera.cancelAutoFocus();
        }
    }

    @Override
    public boolean setMeteringArea(PreviewArea area)
    {
        synchronized (lock)
        {
            Camera camera = getCamera();
            return setAreasParameters(FocusHelper.generateFocusAreas(area.rotate(displayRotation).flip(flipType)), camera);
        }
    }

    private boolean setAreasParameters(List<Camera.Area> areas, Camera camera)
    {
        Camera.Parameters parameters = camera.getParameters();
        boolean hasAnyFocusArea = parameters.getMaxNumFocusAreas() >= 1;
        boolean hasAnyMeteringArea = parameters.getMaxNumMeteringAreas() >= 1;

        if (hasAnyFocusArea)    parameters.setFocusAreas(areas);
        if (hasAnyMeteringArea) parameters.setMeteringAreas(areas);

        camera.setParameters(parameters);
        return hasAnyMeteringArea;
    }

    @Override
    public void stopPreview()
    {
        synchronized (lock)
        {
            if (isPreviewing)
                getCamera().stopPreview();
            isPreviewing = false;
        }
    }

    @Override
    public void startPreview()
    {
        synchronized (lock)
        {
            if (!isPreviewing)
                getCamera().startPreview();
            isPreviewing = true;
        }
    }

    @Override
    public void setMode(Mode mode)
    {
        synchronized (lock)
        {
            Camera.Parameters parameters = getCamera().getParameters();
            parameters.set(AsusParameters.ASUS_MODE, mode.getParameterValue());
            parameters.set(AsusParameters.ASUS_IMAGE_OPTIMIZE, "off");
            parameters.set(AsusParameters.ASUS_ULTRA_PIXELS, mode.isUseUltraPixels() ? "on" : "off");
            parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, IntelParameters.RAW_DATA_FORMAT_NONE);
            getCamera().setParameters(parameters);
        }
    }

    @Override
    public void setPictureFormat(PictureFormat pictureFormat)
    {
        synchronized (lock)
        {
            if (pictureFormat.getDataFormat() == DataFormat.RAW)
                pictureSizeLayer.enableRawMode();
            else
                pictureSizeLayer.disableRawMode();

            pipelineManager.updateFileFormat(pictureFormat.getFileFormat());

            Camera.Parameters parameters = getCamera().getParameters();
            parameters.set(IntelParameters.KEY_RAW_DATA_FORMAT, pictureFormat.getDataFormat().getParameterValue());
            getCamera().setParameters(parameters);
        }
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            WhiteBalanceService.getInstance().fixValue();
            pipelineManager.getPicturePipeline().takePicture(pictureCallback, exceptionCallback);
        }
    }

    @Override
    public String dumpParameters()
    {
        synchronized (lock)
        {
            return getCamera().getParameters().flatten();
        }
    }

    private Camera getCamera()
    {
        return cameraExtension.get().getCameraDevice();
    }

    @Override
    public void setFlash(Flash flashValue)
    {
        synchronized (lock)
        {
            Camera.Parameters parameters = getCamera().getParameters();
            parameters.setFlashMode(flashValue.getParameterValue());
            getCamera().setParameters(parameters);
        }
    }

    @Override
    public void notifyShutterSpeed(ShutterSpeed value)
    {
        synchronized (lock)
        {
            if (cameraExtension.get().hasIntelFeatures())
            {
                Camera.Parameters parameters = getCamera().getParameters();
                parameters.set("ae-mode", ShutterSpeed.AUTO.equals(value) ? "auto" :  "shutter-priority");
                getCamera().setParameters(parameters);
            }
        }
    }
}
