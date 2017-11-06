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
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.camera.extension.RawImageCallbackAccess;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.builder.FromI3av4FileBuilder;
import com.fkeglevich.rawdumper.su.ShellManager;
import com.fkeglevich.rawdumper.util.Mutable;
import com.fkeglevich.rawdumper.util.Nothing;
import com.fkeglevich.rawdumper.util.ThreadUtil;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.io.File;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/11/17.
 */

public class RetryingRawPipeline extends PicturePipelineBase
{
    private final CameraContext cameraContext;
    private final byte[] buffer;
    private Camera.Parameters parameters = null;
    private boolean ignoreError = false;

    RetryingRawPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext, byte[] buffer)
    {
        super(cameraExtension, lock);
        this.cameraContext = cameraContext;
        this.buffer = buffer;
    }

    @Override
    protected void setupCameraBefore(Camera camera)
    {
        super.setupCameraBefore(camera);
        parameters = camera.getParameters();
        RawImageCallbackAccess.addRawImageCallbackBuffer(camera, buffer);
        camera.setErrorCallback(new Camera.ErrorCallback()
        {
            @Override
            public void onError(int error, Camera camera)
            {
                if (ignoreError)
                {
                    ThreadUtil.simpleDelay(2000);
                    //ShellManager.getInstance().addCommand(); //move image to directory
                    //File i3av4;
                    //FromI3av4FileBuilder captureInfoBuilder = new FromI3av4FileBuilder(cameraContext, i3av4, parameters);
                    //CaptureInfo captureInfo = captureInfoBuilder.build();

                    /*IOThread.getIOAccess().saveDng(captureInfo, new AsyncOperation<Nothing>()
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

                    restartCamera();
                    uiHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pictureCallback.onPictureTaken();
                        }
                    });*/
                    ignoreError = false;
                }
            }
        });
    }

    @Override
    protected void processPipeline(PipelineData pipelineData, PictureListener pictureCallback, PictureExceptionListener exceptionCallback)
    {
        //won't be called
    }
}
