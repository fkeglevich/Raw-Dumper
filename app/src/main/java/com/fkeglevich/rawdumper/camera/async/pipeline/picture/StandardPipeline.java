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

import android.os.Handler;
import android.os.Looper;

import com.fkeglevich.rawdumper.camera.action.listener.PictureExceptionListener;
import com.fkeglevich.rawdumper.camera.action.listener.PictureListener;
import com.fkeglevich.rawdumper.camera.async.pipeline.filename.FilenameBuilder;
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.io.Directories;
import com.fkeglevich.rawdumper.util.Mutable;

import java.io.File;
import java.util.Calendar;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 02/11/17.
 */

abstract class StandardPipeline extends PicturePipelineBase
{
    private final Handler uiHandler;
    private final FilenameBuilder filenameBuilder;

    StandardPipeline(Mutable<ICameraExtension> cameraExtension, Object lock, FileFormat fileFormat)
    {
        super(cameraExtension, lock);
        this.filenameBuilder    = new FilenameBuilder().isPicture().useFileFormat(fileFormat);
        this.uiHandler          = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void processPipeline(PipelineData pipelineData, final PictureListener pictureCallback, final PictureExceptionListener exceptionCallback)
    {
        String filename = new File(Directories.getPicturesDirectory(), filenameBuilder.useCalendar(Calendar.getInstance()).build()).getAbsolutePath();
        saveImage(pipelineData, pictureCallback, exceptionCallback, filename);
        startPreview();
        uiHandler.post(pictureCallback::onPictureTaken);
    }

    abstract void saveImage(PipelineData pipelineData,
                            PictureListener pictureCallback,
                            PictureExceptionListener exceptionCallback,
                            String filename);
}
