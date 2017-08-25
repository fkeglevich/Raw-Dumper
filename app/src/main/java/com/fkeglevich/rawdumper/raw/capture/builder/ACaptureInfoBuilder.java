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

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;

/**
 * Created by Flávio Keglevich on 24/08/2017.
 * TODO: Add a class header comment!
 */

public abstract class ACaptureInfoBuilder
{
    CaptureInfo captureInfo;

    public ACaptureInfoBuilder()
    {
        captureInfo = new CaptureInfo();
    }

    //Building required fields
    public abstract void buildDevice();
    public abstract void buildCamera();
    public abstract void buildDate();
    public abstract void buildWhiteBalanceInfo();
    public abstract void buildImageSize();
    public abstract void buildOriginalRawFilename();
    public abstract void buildDestinationRawFilename();
    public abstract void buildOrientation();

    //Building optional fields
    public abstract void buildMakerNoteInfo();
    public abstract void buildCaptureParameters();
    public abstract void buildExtraJpegBytes();
    public abstract void buildRawDataBytes();
    public abstract void buildRelatedI3av4File();

    public CaptureInfo build()
    {
        buildDevice();
        buildCamera();
        buildDate();
        buildWhiteBalanceInfo();
        buildImageSize();
        buildOriginalRawFilename();
        buildDestinationRawFilename();
        buildOrientation();
        buildMakerNoteInfo();
        buildCaptureParameters();
        buildExtraJpegBytes();
        buildRawDataBytes();
        buildRelatedI3av4File();
        return captureInfo;
    }
}
