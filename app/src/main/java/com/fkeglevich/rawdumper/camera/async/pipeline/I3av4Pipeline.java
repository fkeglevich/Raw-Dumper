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

public class I3av4Pipeline extends APicturePipeline
{
    private Shell.Interactive rootShell;
    private IIOResultCallback ioCallback;

    public I3av4Pipeline(Shell.Interactive rootShell, IIOResultCallback ioCallback)
    {
        this.rootShell = rootShell;
        this.ioCallback = ioCallback;
    }

    @Override
    void initCallbacks()
    {
        pictureCallback = new Camera.PictureCallback()
        {
            @Override
            public void onPictureTaken(byte[] data, Camera camera)
            {
                camera.startPreview();
                /*rootShell.addCommand(new String[] {"mv " + RAW_PATH + "/* " + partialDir.getAbsolutePath()}, 2, new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode) {
                        if (exitCode < 0)
                        {
                            ioCallback.onResult(false);
                        }
                        else
                        {
                            //write dng and get filename
                            IOAccess.writeBytesToFile(null, "/rawpath", ioCallback);
                        }
                    }

                    @Override
                    public void onLine(String line) {   }
                });*/
            }
        };
    }
}
