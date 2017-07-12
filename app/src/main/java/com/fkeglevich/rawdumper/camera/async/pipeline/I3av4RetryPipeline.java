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

package com.fkeglevich.rawdumper.camera.async.pipeline;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.async.callbacks.IIOResultCallback;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Flávio Keglevich on 26/06/2017.
 * TODO: Add a class header comment!
 */

public class I3av4RetryPipeline extends APicturePipeline
{
    private Shell.Interactive rootShell;
    private IIOResultCallback ioCallback;
    private Camera.ErrorCallback cameraErrorCallback;
    private Camera.ErrorCallback newErrorCallback;

    private boolean bypassErrorCallback = false;

    public I3av4RetryPipeline(Shell.Interactive rootShell, IIOResultCallback ioCallback, Camera.ErrorCallback cameraErrorCallback)
    {
        this.rootShell = rootShell;
        this.ioCallback = ioCallback;
        this.cameraErrorCallback = cameraErrorCallback;
    }

    @Override
    void initCallbacks()
    {
        newErrorCallback = new Camera.ErrorCallback()
        {
            @Override
            public void onError(int error, Camera camera)
            {
                if (!bypassErrorCallback)
                    cameraErrorCallback.onError(error, camera);
                else
                {
                    //Thread.sleep(getShutterSpeed());
                    //Wait
                    //Close
                    //Try again
                    bypassErrorCallback = false;
                }
            }
        };
    }

    @Override
    public void takePicture(Camera camera)
    {
        camera.setErrorCallback(newErrorCallback);
        bypassErrorCallback = true;
        super.takePicture(camera);
    }
}
