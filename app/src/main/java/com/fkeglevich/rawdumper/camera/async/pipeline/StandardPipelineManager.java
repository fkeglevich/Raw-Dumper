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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.async.pipeline.picture.JpegPipeline;
import com.fkeglevich.rawdumper.camera.async.pipeline.picture.PicturePipeline;
import com.fkeglevich.rawdumper.camera.async.pipeline.picture.RawPipeline;
import com.fkeglevich.rawdumper.camera.async.pipeline.picture.YuvPipeline;
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.util.Mutable;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/11/17.
 */

public class StandardPipelineManager implements PipelineManager
{
    private final Map<FileFormat, PicturePipeline> pipelineMap = new HashMap<>();
    private final Mutable<FileFormat> fileFormatMutable = Mutable.createInvalid();

    public static StandardPipelineManager createInvalid()
    {
        return new StandardPipelineManager();
    }

    private StandardPipelineManager()
    {   }

    public void setupMutableState(Mutable<ICameraExtension> cameraExtension, Object lock, CameraContext cameraContext)
    {
        byte[] buffer = BufferFactory.createLargestImageCallbackBuffer(cameraExtension, cameraContext);

        PicturePipeline jpegPipeline    = new JpegPipeline(cameraExtension, lock);
        PicturePipeline pngPipeline     = new YuvPipeline (cameraExtension, lock, FileFormat.PNG, buffer);
        PicturePipeline webpPipeline    = new YuvPipeline (cameraExtension, lock, FileFormat.WEBP, buffer);
        PicturePipeline dngPipeline     = new RawPipeline (cameraExtension, lock, cameraContext, buffer);

        pipelineMap.put(FileFormat.JPEG, jpegPipeline);
        pipelineMap.put(FileFormat.PNG,  pngPipeline);
        pipelineMap.put(FileFormat.WEBP, webpPipeline);
        pipelineMap.put(FileFormat.DNG,  dngPipeline);
    }

    @Override
    public PicturePipeline getPicturePipeline()
    {
        return pipelineMap.get(fileFormatMutable.get());
    }

    @Override
    public void updateFileFormat(FileFormat fileFormat)
    {
        fileFormatMutable.setupMutableState(fileFormat);
    }
}
