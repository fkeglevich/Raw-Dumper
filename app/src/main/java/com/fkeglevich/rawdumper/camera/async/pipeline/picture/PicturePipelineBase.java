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

import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

public abstract class PicturePipelineBase implements PicturePipeline
{
    private final Camera lowLevelCamera;
    private final Object lock;

    PicturePipelineBase(Camera lowLevelCamera, Object lock)
    {
        this.lowLevelCamera = lowLevelCamera;
        this.lock = lock;
    }

    @Override
    public void takePicture(final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback)
    {
        synchronized (lock)
        {
            final PipelineData pipelineData = new PipelineData();

            lowLevelCamera.takePicture(null, new Camera.PictureCallback()
            {
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    pipelineData.rawData = data;
                }
            }
            , new Camera.PictureCallback()
            {
                @Override
                public void onPictureTaken(byte[] data, Camera camera)
                {
                    pipelineData.jpegData = data;
                    processPipeline(pipelineData, pictureCallback, exceptionCallback);
                }
            });
        }
    }

    protected abstract void processPipeline(PipelineData pipelineData, PictureListener pictureCallback, PictureExceptionListener exceptionCallback);
}
