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

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.direct.mutable.MutableParameterCollection;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.helper.PreviewHelper;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.PictureSizeLayer;

import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public class LowLevelCameraImpl implements LowLevelCamera
{
    private final Object lock = new Object();
    private final CameraContext cameraContext;
    private final ICameraExtension cameraExtension;
    private final LowLevelCameraActions lowLevelCameraActions;
    private final ParameterCollection parameterCollection;
    private final PictureSizeParameterCollection pictureSizeParameterCollection;

    public LowLevelCameraImpl(CameraContext cameraContext, ICameraExtension cameraExtension) throws IOException
    {
        this.cameraContext = cameraContext;
        this.cameraExtension = cameraExtension;
        int displayRotation = PreviewHelper.setupPreviewTexture(cameraContext, cameraExtension.getCameraDevice());
        LowLevelParameterInterfaceImpl parameterInterface = new LowLevelParameterInterfaceImpl(cameraExtension.getCameraDevice(), lock);
        this.parameterCollection = new ParameterCollection(parameterInterface);
        this.pictureSizeParameterCollection = new PictureSizeParameterCollection(parameterCollection, cameraContext.getCameraInfo().getSensor());
        this.lowLevelCameraActions = new LowLevelCameraActions(cameraExtension, lock, displayRotation, pictureSizeParameterCollection);
    }

    @Override
    public void close()
    {
        synchronized (lock)
        {
            cameraExtension.release();
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
    public ParameterCollection getPictureSizeParameterCollection()
    {
        return pictureSizeParameterCollection;
    }

    @Override
    public CameraActions getCameraActions()
    {
        return lowLevelCameraActions;
    }
}
