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

import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.controller.orientation.OrientationManager;
import com.fkeglevich.rawdumper.dng.DngWriter;
import com.fkeglevich.rawdumper.dng.writer.ScanlineImageWriter;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.builder.ACaptureInfoBuilder;
import com.fkeglevich.rawdumper.raw.capture.builder.CameraSizePair;
import com.fkeglevich.rawdumper.raw.capture.builder.CameraSizePairList;
import com.fkeglevich.rawdumper.raw.capture.builder.FromI3av4FileBuilder;
import com.fkeglevich.rawdumper.raw.capture.builder.FromRawAndJpegBuilder;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.data.buffer.ArrayRawImageData;
import com.fkeglevich.rawdumper.raw.data.buffer.FileRawImageData;
import com.fkeglevich.rawdumper.raw.data.buffer.RawImageData;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public abstract class RawPipeline
{
    protected abstract ACaptureInfoBuilder getCaptureInfoBuilder();

    protected abstract RawImageData getRawImageData();

    private void asd() throws IOException
    {
        CaptureInfo captureInfo = getCaptureInfoBuilder().build();
        DngWriter dngWriter = DngWriter.open("asda.dng");
        dngWriter.write(captureInfo, new ScanlineImageWriter(), getRawImageData());
    }

    //delete i3av4 after calling this
    private void gotRawAndJpeg(byte[] raw, byte[] jpeg, Camera.Parameters parameters, CameraContext cameraContext) throws IOException
    {
        ACaptureInfoBuilder captureInfoBuilder = new FromRawAndJpegBuilder(cameraContext, parameters, raw, jpeg);
        CaptureInfo captureInfo = captureInfoBuilder.build();

        DngWriter dngWriter = DngWriter.open("asda.dng");
        if (dngWriter != null)
        {
            dngWriter.write(captureInfo, new ScanlineImageWriter(), new ArrayRawImageData(captureInfo.imageSize, captureInfo.rawDataBytes));
            //delete i3av4 after async starPreview (if it exists!)
            //delete all files in folder
        }
    }

    private void gotReadableI3av4(File i3av4File, Camera.Parameters parameters, CameraContext cameraContext) throws IOException
    {
        //move i3av4 before calling this
        ACaptureInfoBuilder captureInfoBuilder = new FromI3av4FileBuilder(cameraContext, i3av4File, parameters);
        CaptureInfo captureInfo = captureInfoBuilder.build();

        DngWriter dngWriter = DngWriter.open("asda.dng");
        if (dngWriter != null)
        {
            dngWriter.write(captureInfo, new ScanlineImageWriter(), new FileRawImageData(captureInfo.imageSize, captureInfo.relatedI3av4File.getPath()));
            i3av4File.delete();
        }
    }
}
