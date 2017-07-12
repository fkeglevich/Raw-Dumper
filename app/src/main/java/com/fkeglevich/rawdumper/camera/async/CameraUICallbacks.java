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

import android.os.Handler;

import com.fkeglevich.rawdumper.camera.async.callbacks.IAutoFocusCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IOpenCameraCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IPictureCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IRawCaptureCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IReopenCameraCallback;
import com.fkeglevich.rawdumper.camera.async.callbacks.IStartPreviewCallback;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Flávio Keglevich on 08/07/2017.
 * TODO: Add a class header comment!
 */

class CameraUICallbacks
{
    private final Handler uiHandler;
    private final ConcurrentLinkedQueue<Runnable> callbacks;

    CameraUICallbacks()
    {
        uiHandler = new Handler();
        callbacks = new ConcurrentLinkedQueue<>();
    }

    private void removeCallback(Runnable runnable)
    {
        synchronized (uiHandler)
        {
            uiHandler.removeCallbacks(runnable);
        }
    }

    void postCameraOpenedCallback(final boolean couldOpen,
                                  final IOpenCameraCallback openCameraCallback,
                                  final CameraOpenError openError,
                                  final CameraAccess camAccess)
    {

        synchronized (uiHandler)
        {
            Runnable callback = new Runnable()
            {
                @Override
                public void run()
                {
                    openCameraCallback.cameraOpened(couldOpen ? camAccess : null, openError);
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void postStartPreviewCallback(final IStartPreviewCallback startPreviewCallback)
    {
        synchronized (uiHandler)
        {
            Runnable callback =new Runnable()
            {
                @Override
                public void run()
                {
                    startPreviewCallback.previewStarted();
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void postAutoFocusCallback(final boolean success, final IAutoFocusCallback autoFocusCallback)
    {
        synchronized (uiHandler)
        {
            Runnable callback =new Runnable()
            {
                @Override
                public void run()
                {
                    autoFocusCallback.onAutoFocus(success);
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void postTakePictureCallbacks(final byte[] data, final IPictureCallback pictureCallback)
    {
        synchronized (uiHandler)
        {
            Runnable callback =new Runnable()
            {
                @Override
                public void run()
                {
                    pictureCallback.onPictureTaken(data);
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void postTakeRawPictureCallback(final boolean success, final IRawCaptureCallback rawCaptureCallback)
    {
        synchronized (uiHandler)
        {
            Runnable callback =new Runnable()
            {
                @Override
                public void run()
                {
                    rawCaptureCallback.onPictureTaken(success);
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void postOpenShellCallback(final int commandCode,
                               final Shell.OnCommandResultListener shellCallback, final int exitCode,
                               final List<String> output)
    {
        synchronized (uiHandler)
        {
            Runnable callback =new Runnable()
            {
                @Override
                public void run()
                {
                    shellCallback.onCommandResult(commandCode, exitCode, output);
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void postReopenCameraCallback(final IReopenCameraCallback reopenCameraCallback)
    {
        synchronized (uiHandler)
        {
            Runnable callback =new Runnable()
            {
                @Override
                public void run()
                {
                    reopenCameraCallback.onReopen();
                    removeCallback(this);
                }
            };
            callbacks.add(callback);
            uiHandler.post(callback);
        }
    }

    void removeAllCallbacks()
    {
        synchronized (uiHandler)
        {
            Runnable current;
            while ((current = callbacks.poll()) != null)
                uiHandler.removeCallbacks(current);
        }
    }
}
