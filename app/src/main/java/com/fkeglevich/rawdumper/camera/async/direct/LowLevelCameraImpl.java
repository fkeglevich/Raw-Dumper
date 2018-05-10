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
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.direct.mutable.MutableParameterCollection;
import com.fkeglevich.rawdumper.camera.async.pipeline.StandardPipelineManager;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.IntelCameraExtensionLoader;
import com.fkeglevich.rawdumper.camera.helper.PreviewHelper;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.PictureSizeLayer;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public class LowLevelCameraImpl implements LowLevelCamera, RestartableCamera
{
    private final Object lock = new Object();
    private final CameraContext cameraContext;

    //mutable fields
    private final Mutable<ICameraExtension>  cameraExtension         = Mutable.createInvalid();
    private final MutableParameterCollection parameterCollection     = MutableParameterCollection.createInvalid();
    private final PictureSizeLayer           pictureSizeLayer        = PictureSizeLayer.createInvalid();
    private final AsyncParameterSender       asyncParameterSender    = new AsyncParameterSender();
    private final LowLevelCameraActions      lowLevelCameraActions;
    private final StandardPipelineManager    pipelineManager;

    public LowLevelCameraImpl(CameraContext cameraContext, ICameraExtension extension) throws IOException
    {
        this.cameraContext = cameraContext;
        this.pipelineManager = StandardPipelineManager.createInvalid();
        this.lowLevelCameraActions = LowLevelCameraActions.createInvalid(cameraExtension, lock, pictureSizeLayer, pipelineManager);
        setupMutableState(extension);
    }

    private void setupMutableState(ICameraExtension extension) throws IOException
    {
        synchronized (lock)
        {
            Camera camera = extension.getCameraDevice();
            int displayRotation = PreviewHelper.setupPreviewTexture(cameraContext, camera);
            LowLevelParameterInterfaceImpl parameterInterface = new LowLevelParameterInterfaceImpl(camera, lock);
            ParameterCollection parameters = new ParameterCollection(parameterInterface);

            if (cameraContext.getCameraInfo().canDisableShutterSound())
                extension.getCameraDevice().enableShutterSound(false);

            asyncParameterSender.setupMutableState(parameters);
            cameraExtension.setupMutableState(extension);
            parameterCollection.setupMutableState(parameters);
            pictureSizeLayer.setupMutableState(parameters, cameraContext.getSensorInfo());
            pipelineManager.setupMutableState(cameraExtension, lock, cameraContext, this);
            lowLevelCameraActions.setupMutableState(displayRotation, cameraContext.getCameraInfo().getFlipType());
        }
    }

    @Override
    public void close()
    {
        synchronized (lock)
        {
            asyncParameterSender.clearPendingOperations();
            cameraExtension.get().release();
        }
    }

    @Override
    public CameraContext getCameraContext()
    {
        return cameraContext;
    }

    @Override
    public ParameterCollection getParameterCollection()
    {
        return parameterCollection;
    }

    @Override
    public AsyncParameterSender getAsyncParameterSender()
    {
        return asyncParameterSender;
    }

    @Override
    public ParameterCollection getPictureSizeLayer()
    {
        return pictureSizeLayer;
    }

    @Override
    public CameraActions getCameraActions()
    {
        return lowLevelCameraActions;
    }

    @Override
    public void restartCamera() throws MessageException, IOException
    {
        synchronized (lock)
        {
            Camera.Parameters backup = cameraExtension.get().getCameraDevice().getParameters();
            close();
            ICameraExtension cameraExtension = IntelCameraExtensionLoader.extendedOpenCamera(cameraContext);
            setupMutableState(cameraExtension);
            cameraExtension.getCameraDevice().setParameters(backup);
            lowLevelCameraActions.startPreview();
        }
    }
}
