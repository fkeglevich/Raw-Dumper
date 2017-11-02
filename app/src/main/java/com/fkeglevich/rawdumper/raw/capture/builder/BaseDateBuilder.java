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

package com.fkeglevich.rawdumper.raw.capture.builder;

import com.fkeglevich.rawdumper.camera.async.pipeline.filename.FilenameBuilder;
import com.fkeglevich.rawdumper.camera.data.FileFormat;
import com.fkeglevich.rawdumper.raw.capture.DateInfo;

/**
 * Created by Flávio Keglevich on 25/08/2017.
 * TODO: Add a class header comment!
 */

public abstract class BaseDateBuilder extends ACaptureInfoBuilder
{
    DateInfo dateInfo;

    BaseDateBuilder()
    {
        super();
        initDateInfo();
    }

    abstract void initDateInfo();

    @Override
    public void buildDate()
    {
        captureInfo.date = dateInfo;
    }

    @Override
    public void buildDestinationRawFilename()
    {
        captureInfo.destinationRawFilename = generateRawFilename();
    }

    String generateRawFilename()
    {
        return new FilenameBuilder().useDateInfo(dateInfo)
                                    .useFileFormat(FileFormat.DNG)
                                    .isPicture()
                                    .build();
    }
}
