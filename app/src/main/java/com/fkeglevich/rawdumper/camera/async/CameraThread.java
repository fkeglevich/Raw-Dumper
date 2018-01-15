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
import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.async.function.ThrowingAsyncFunctionContext;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.async.operation.WrappingOperation;
import com.fkeglevich.rawdumper.camera.async.direct.RestartableCamera;
import com.fkeglevich.rawdumper.camera.async.function.CameraCloseFunction;
import com.fkeglevich.rawdumper.camera.async.function.CameraOpenFunction;
import com.fkeglevich.rawdumper.camera.async.function.CameraRestartFunction;
import java.lang.Void;
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
    private TurboCamera openedCamera = null;

    private CameraThread()
    {
        HandlerThread thread = new HandlerThread(THREAD_NAME);
        thread.start();
        functionContext = new ThrowingAsyncFunctionContext(thread.getLooper(), Looper.getMainLooper());
    }

    public synchronized void openCamera(CameraContext cameraContext, AsyncOperation<TurboCamera> callback,
                           AsyncOperation<MessageException> exception)
    {
        if (getCurrentCamera() != null)
            throw new IllegalStateException("Camera is already opened!");
        functionContext.call(new CameraOpenFunction(), cameraContext, wrapOpenCallback(callback), exception);
    }

    public void closeCamera()
    {
        assertCameraWasOpened();
        functionContext.ignoreAllPendingCalls();
        ((Closeable) getCurrentCamera()).close();
        openedCamera = null;
    }

    public void closeCameraAsync(AsyncOperation<Void> callback)
    {
        assertCameraWasOpened();
        functionContext.call(new CameraCloseFunction(), getCurrentCamera(), wrapCloseCallback(callback));
    }

    public void restartCamera(RestartableCamera camera, AsyncOperation<Void> callback,
                              AsyncOperation<MessageException> exception)
    {
        functionContext.call(new CameraRestartFunction(), camera, callback, exception);
    }

    public TurboCamera getCurrentCamera()
    {
        return openedCamera;
    }

    @NonNull
    private WrappingOperation<TurboCamera> wrapOpenCallback(final AsyncOperation<TurboCamera> callback)
    {
        return new WrappingOperation<TurboCamera>(callback)
        {
            @Override
            protected void execute(TurboCamera argument)
            {
                openedCamera = argument;
                executeWrapped(argument);
            }
        };
    }

    @NonNull
    private WrappingOperation<Void> wrapCloseCallback(final AsyncOperation<Void> callback)
    {
        return new WrappingOperation<Void>(callback)
        {
            @Override
            protected void execute(Void argument)
            {
                openedCamera = null;
                executeWrapped(argument);
            }
        };
    }

    private void assertCameraWasOpened()
    {
        if (getCurrentCamera() == null) throw new IllegalStateException("Camera wasn't opened!");
    }
}