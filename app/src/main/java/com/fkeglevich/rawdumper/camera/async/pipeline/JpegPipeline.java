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
import android.os.Handler;

import com.fkeglevich.rawdumper.async.Locked;
import com.fkeglevich.rawdumper.async.operation.AsyncOperation;
import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.async.pipeline.filename.FilenameBuilder;
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.io.async.IOThread;
import com.fkeglevich.rawdumper.util.exception.MessageException;

import java.util.Calendar;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

public class JpegPipeline implements PicturePipeline
{
    private final Camera camera;
    private final Object lock;
    private final CameraActions cameraActions;
    private final Handler uiHandler;
    private final FilenameBuilder filenameBuilder;

    public JpegPipeline(Camera camera, Object lock, CameraActions cameraActions, Handler uiHandler)
    {
        this.camera = camera;
        this.lock = lock;
        this.cameraActions = cameraActions;
        this.uiHandler = uiHandler;
        this.filenameBuilder = new FilenameBuilder()
                                .isPicture()
                                .useFileFormat(FileFormat.JPEG);
    }

    @Override
    public void takePicture(final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            camera.takePicture(null, null, new Camera.PictureCallback()
            {
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    String filename = filenameBuilder.useCalendar(Calendar.getInstance())
                                                     .build();


                    IOThread.getIOAccess().saveFileAsync(new Locked<byte[]>(data), filename, new AsyncOperation<Void>()
                    {
                        @Override
                        protected void execute(Void argument)
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
                    cameraActions.startPreview();
                    uiHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pictureCallback.onPictureTaken();
                        }
                    });
                }
            });
        }
    }
}
