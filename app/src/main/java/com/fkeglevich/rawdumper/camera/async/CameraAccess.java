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

import android.util.Log;
import android.view.TextureView;

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunctionContext;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.function.CameraOpenArgument;
import com.fkeglevich.rawdumper.camera.async.function.CameraOpenFunction;
import com.fkeglevich.rawdumper.camera.async.function.StartPreviewFunction;
import com.fkeglevich.rawdumper.camera.async.function.TakePicFunction;
import com.fkeglevich.rawdumper.camera.helper.PreviewHelper;
import com.fkeglevich.rawdumper.camera.shared.SharedCamera;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.IOException;

/**
 * Created by Flávio Keglevich on 13/08/2017.
 * TODO: Add a class header comment!
 */

public class CameraAccess
{
    private final ThrowingAsyncFunctionContext functionContext;
    private final Locked<SharedCamera> sharedCamera;
    private final SharedCameraSetter sharedCameraSetter;
    private final SharedCameraGetter sharedCameraGetter;

    CameraAccess(ThrowingAsyncFunctionContext functionContext)
    {
        this.functionContext = functionContext;
        this.sharedCamera = new Locked<>(null);
        this.sharedCameraSetter = new SharedCameraSetter()
        {
            @Override
            public void setSharedCamera(SharedCamera camera)
            {setCameraSafely(camera);
            }
        };
        this.sharedCameraGetter = new SharedCameraGetter()
        {
            @Override
            public Locked<SharedCamera> getSharedCamera()
            {return sharedCamera;
            }
        };
    }

    void openCameraAsync(CameraOpenArgument argument, AsyncOperation<CameraAccess> callback, AsyncOperation<MessageException> exception)
    {
        functionContext.call(new CameraOpenFunction(sharedCameraSetter, this), argument, callback, exception);
    }

    void close()
    {
        synchronized (sharedCamera.getLock())
        {
            try {   sharedCamera.get().getCamera().stopPreview();    }
            finally
            {
                functionContext.ignoreAllPendingCalls();
                sharedCamera.get().getCameraExtension().release();
                setCameraSafely(null);
            }
        }
    }

    public void startPreviewAsync(AsyncOperation<Void> callback)
    {
        functionContext.call(new StartPreviewFunction(), sharedCamera, callback);
    }

    public void setupPreview(TextureView textureView) throws IOException
    {
        new CameraSetup(this).setupCameraParameters(sharedCameraGetter);
        PreviewHelper.setupPreviewTexture(textureView, sharedCamera);
    }

    private void setCameraSafely(SharedCamera camera)
    {
        synchronized (sharedCamera.getLock())
        {
            sharedCamera.set(camera);
        }
    }

    public void setParameter(String key, String value)
    {
        synchronized (sharedCamera.getLock())
        {
            sharedCamera.get().getParameters().setAndUpdate(key, value);
        }
    }
}
