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

import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.direct.RestartableCamera;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.RawImageCallbackAccess;
import com.fkeglevich.rawdumper.io.Directories;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.builder.ACaptureInfoBuilder;
import com.fkeglevich.rawdumper.raw.capture.builder.FromI3av4FileBuilder;
import com.fkeglevich.rawdumper.su.ShellManager;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.File;
import java.io.IOException;

import eu.chainfire.libsuperuser.Shell;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/11/17.
 */

public class RetryingRawPipeline implements PicturePipeline
{
    private final Mutable<ICameraExtension> cameraExtension;
    private final Object                    lock;
    private final CameraContext             cameraContext;
    private final RestartableCamera         restartableCamera;
    private final Handler                   uiHandler;

    private Camera.Parameters               parameters = null;
    private boolean                         ignoreError = false;

    private final Camera.ErrorCallback      errorCallback = new Camera.ErrorCallback()
    {
        @Override
        public void onError(int error, Camera camera)
        {
            if (ignoreError)
            {
                ThreadUtil.simpleDelay(2000);
                ignoreError = false;
                String dumpDirectory = cameraContext.getDeviceInfo().getDumpDirectoryLocation();
                String partialPath = Directories.getPartialPicturesDirectory().getAbsolutePath();
                ShellManager.getInstance().addSingleCommand("mv " + dumpDirectory + "/*.i3av4" + " " + partialPath, new Shell.OnCommandLineListener()
                {
                    @Override
                    public void onCommandResult(int commandCode, int exitCode)
                    {
                        processPicture();
                    }

                    @Override
                    public void onLine(String line)
                    {

                    }
                });
            }
        }
    };
    private PictureListener nextPictureCallback;
    private PictureExceptionListener nextExceptionCallback;

    RetryingRawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, RestartableCamera restartableCamera)
    {
        this.cameraExtension    = cameraExtension;
        this.lock               = lock;
        this.cameraContext      = cameraContext;
        this.restartableCamera  = restartableCamera;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            nextPictureCallback = pictureCallback;
            nextExceptionCallback = exceptionCallback;
            Camera camera = cameraExtension.get().getCameraDevice();
            parameters = camera.getParameters();
            ignoreError = true;
            camera.setErrorCallback(errorCallback);
            camera.takePicture(null, null, null);
        }
    }

    private void processPicture()
    {
        File i3av4File = Directories.getPartialPicturesDirectory().listFiles()[0];
        ACaptureInfoBuilder captureInfoBuilder = new FromI3av4FileBuilder(cameraContext, i3av4File, parameters);
        CaptureInfo captureInfo = captureInfoBuilder.build();

        IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Nothing>()
        {
            @Override
            protected void execute(Nothing argument)
            {
                nextPictureCallback.onPictureSaved();
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                nextExceptionCallback.onException(argument);
            }
        });

        try
        {
            restartableCamera.restartCamera();
        } catch (MessageException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        uiHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                nextPictureCallback.onPictureTaken();
            }
        });
    }
}
