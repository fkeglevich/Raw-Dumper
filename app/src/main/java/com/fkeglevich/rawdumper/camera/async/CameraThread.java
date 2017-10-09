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

import android.os.HandlerThread;
import android.os.Looper;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunctionContext;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.async.function.CameraOpenFunction;
import com.fkeglevich.rawdumper.util.exception.MessageException;

/**
 * Created by Flávio Keglevich on 09/08/2017.
 * TODO: Add a class header comment!
 */

public class CameraThread
{
    private static final String THREAD_NAME = "CameraThread";
    private static CameraThread instance = null;

    public static CameraThread getInstance()
    {
        if (instance == null) instance = new CameraThread();
        return instance;
    }

    private final ThrowingAsyncFunctionContext functionContext;

    private CameraThread()
    {
        HandlerThread thread = new HandlerThread(THREAD_NAME);
        thread.start();
        functionContext = new ThrowingAsyncFunctionContext(thread.getLooper(), Looper.getMainLooper());
    }

    public void openCamera(CameraContext cameraContext, AsyncOperation<TurboCamera> callback,
                           AsyncOperation<MessageException> exception)
    {
        functionContext.call(new CameraOpenFunction(), cameraContext, callback, exception);
    }

    public void closeCamera(TurboCamera turboCamera)
    {
        Closeable closeableCamera = (Closeable)turboCamera;
        closeableCamera.close();
    }
}