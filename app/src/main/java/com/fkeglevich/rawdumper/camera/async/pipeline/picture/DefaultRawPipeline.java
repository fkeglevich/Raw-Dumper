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
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.RawImageCallbackAccess;
import com.fkeglevich.rawdumper.dng.DngWriter;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.builder.ACaptureInfoBuilder;
import com.fkeglevich.rawdumper.raw.capture.builder.FromI3av4FileBuilder;
import com.fkeglevich.rawdumper.raw.capture.builder.FromRawAndJpegBuilder;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.su.ShellManager;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import eu.chainfire.libsuperuser.Shell;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 03/11/17.
 */

public class DefaultRawPipeline extends PicturePipelineBase
{
    private final CameraContext cameraContext;
    private final byte[] buffer;
    private final Handler uiHandler;

    private Camera.Parameters parameters = null;

    DefaultRawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, byte[] buffer)
    {
        super(cameraExtension, lock);
        this.cameraContext = cameraContext;
        this.buffer = buffer;
        this.uiHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void setupCameraBefore(Camera camera)
    {
        super.setupCameraBefore(camera);
        parameters = camera.getParameters();
        RawImageCallbackAccess.addRawImageCallbackBuffer(camera, buffer);
    }

    @Override
    protected void processPipeline(PipelineData pipelineData, final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback)
    {
        ACaptureInfoBuilder captureInfoBuilder = new FromRawAndJpegBuilder(cameraContext, parameters, pipelineData.rawData, pipelineData.jpegData);
        CaptureInfo captureInfo = captureInfoBuilder.build();

        IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Nothing>()
        {
            @Override
            protected void execute(Nothing argument)
            {
                pictureCallback.onPictureSaved();
            }
        }, new AsyncOperation<MessageException>()
        {
            @Override
            protected void execute(MessageException argument)
            {
                exceptionCallback.onException(argument);
            }
        });

        startPreview();
        String dumpDirectory = cameraContext.getDeviceInfo().getDumpDirectoryLocation();
        ShellManager.getInstance().addSingleCommand("rm " + dumpDirectory + "/*.i3av4", new Shell.OnCommandLineListener()
        {
            @Override
            public void onCommandResult(int commandCode, int exitCode)
            {
                uiHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        pictureCallback.onPictureTaken();
                    }
                });
            }

            @Override
            public void onLine(String line)
            {
                //no op
            }
        });
    }
}
