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

package com.fkeglevich.rawdumper.camera.async.pipeline.picture;

import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.direct.RestartableCamera;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.util.Mutable;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 03/11/17.
 */

public class RawPipeline implements PicturePipeline
{
    private final PicturePipeline actualPipeline;

    public RawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, byte[] buffer, RestartableCamera restartableCamera)
    {
        actualPipeline = chooseRawPipeline(cameraExtension, lock, cameraContext, buffer, restartableCamera);
    }

    private PicturePipeline chooseRawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, byte[] buffer, RestartableCamera restartableCamera)
    {
        if (cameraContext.getCameraInfo().isRetryOnError() || DebugFlag.isForceRetryingPipeline())
            return new RetryingRawPipeline(cameraExtension, lock, cameraContext, restartableCamera);
        else
            return new StandardRawPipeline(cameraExtension, lock, cameraContext, buffer);
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        actualPipeline.takePicture(pictureCallback, exceptionCallback);
    }

    @Override
    public void updateShutterSpeed(ShutterSpeed shutterSpeed)
    {
        actualPipeline.updateShutterSpeed(shutterSpeed);
    }
}
