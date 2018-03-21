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

import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.CameraThread;
import com.fkeglevich.rawdumper.camera.async.direct.RestartableCamera;
import com.fkeglevich.rawdumper.camera.async.pipeline.picture.dummy.RetryingPipelineSimulator;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.debug.DebugFlag;
import com.fkeglevich.rawdumper.io.Directories;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.io.async.exception.SaveFileException;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.builder.ACaptureInfoBuilder;
import com.fkeglevich.rawdumper.raw.capture.builder.FromI3av4FileBuilder;
import com.fkeglevich.rawdumper.su.ShellManager;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.File;

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

    private Camera.Parameters               parameters = null;
    private boolean                         ignoreError = false;

    private final Camera.ErrorCallback      errorCallback = (error, camera) ->
    {
        if (ignoreError)
        {
            ThreadUtil.simpleDelay(2000);
            restartCamera();
            ignoreError = false;
        }
    };

    private final Camera.PictureCallback dummyJpegCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            cameraExtension.get().getCameraDevice().startPreview();
            errorCallback.onError(0, camera);
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
    }

    @Override
    public void takePicture(PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            nextPictureCallback = pictureCallback;
            nextExceptionCallback = exceptionCallback;
            ignoreError = true;
            Camera camera = cameraExtension.get().getCameraDevice();
            parameters = camera.getParameters();

            if (DebugFlag.usingRetryPipelineSimulator())
                RetryingPipelineSimulator.simulate(cameraContext, errorCallback);
            else
            {
                camera.setErrorCallback(errorCallback);
                camera.takePicture(null, null, dummyJpegCallback);
            }
        }
    }

    private void movePictures()
    {
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
            {   }
        });
    }

    private void processPicture()
    {
        File[] files = Directories.getPartialPicturesDirectory().listFiles();
        if (files.length != 1)
        {
            nextExceptionCallback.onException(new SaveFileException());
            return;
        }

        File i3av4File = files[0];
        ACaptureInfoBuilder captureInfoBuilder = new FromI3av4FileBuilder(cameraContext, i3av4File, parameters);
        CaptureInfo captureInfo = captureInfoBuilder.build();

        IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
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
    }

    private void restartCamera()
    {
        CameraThread.getInstance().restartCamera(restartableCamera, new AsyncOperation<Void>()
        {
            @Override
            protected void execute(Void argument)
            {
                movePictures();
                nextPictureCallback.onPictureTaken();
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                nextExceptionCallback.onException(argument);
            }
        });
    }


}
