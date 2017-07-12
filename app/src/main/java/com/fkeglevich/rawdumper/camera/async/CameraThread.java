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

import android.content.Context;
import android.os.HandlerThread;

import com.fkeglevich.rawdumper.camera.async.callbacks.IOpenCameraCallback;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Flávio Keglevich on 25/06/2017.
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

    private HandlerThread thread;
    private CameraAccess cameraAccess;

    private CameraThread()
    {
        thread = new HandlerThread(THREAD_NAME);
        thread.start();
        cameraAccess = new CameraAccess(thread.getLooper());
    }

    public void openCamera(int cameraId, Context applicationContext, IOpenCameraCallback callback)
    {
        cameraAccess.openCameraAsync(cameraId, applicationContext, callback);
    }

    public void closeCamera()
    {
        cameraAccess.close();
    }

    public void openShell(final Shell.OnCommandResultListener callback)
    {
        cameraAccess.openShell(callback);
    }

    public boolean isShellRunning()
    {
        return cameraAccess.isShellRunning();
    }
}
