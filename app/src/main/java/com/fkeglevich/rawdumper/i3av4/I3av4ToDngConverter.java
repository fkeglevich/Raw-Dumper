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

package com.fkeglevich.rawdumper.i3av4;

import android.content.Context;
import android.hardware.Camera;

import com.fkeglevich.rawdumper.dng.DngImageWriter;
import com.fkeglevich.rawdumper.dng.DngWriter;
import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.capture.DateExtractor;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfo;
import com.fkeglevich.rawdumper.raw.capture.MakerNoteInfoExtractor;
import com.fkeglevich.rawdumper.raw.capture.WhiteBalanceInfoExtractor;
import com.fkeglevich.rawdumper.raw.data.ImageOrientation;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.CameraInfo;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flávio Keglevich on 14/06/2017.
 * TODO: Add a class header comment!
 */

public class I3av4ToDngConverter
{
    private List<CameraConfig> cameraConfigList;
    private DeviceInfo deviceInfo;
    private MakerNoteInfoExtractor makerNoteInfoExtractor;

    public I3av4ToDngConverter(DeviceInfo deviceInfo)
    {
        this.cameraConfigList = new ArrayList<>();
        this.deviceInfo = deviceInfo;
        this.makerNoteInfoExtractor = new MakerNoteInfoExtractor();

        for (CameraInfo cameraInfo : deviceInfo.getCameras())
            for (RawImageSize imageSize : cameraInfo.getSensor().getRawImageSizes())
                cameraConfigList.add(new CameraConfig(imageSize, cameraInfo));
    }

    public void convert(String i3av4Path, String dngPath, Context context) throws IOException
    {
        File i3av4File = new File(i3av4Path);
        RandomAccessFile i3av4RAFile = new RandomAccessFile(i3av4Path, "r");
        CameraConfig cameraConfig = getBestCameraConfig(i3av4RAFile.length());
        long rawDataStart = i3av4RAFile.length() - cameraConfig.rawImageSize.getRawBufferLength();

        byte[] mknBytes = new byte[(int)rawDataStart];
        i3av4RAFile.seek(0);
        i3av4RAFile.read(mknBytes);

        CaptureInfo captureInfo = new CaptureInfo();
        captureInfo.device = deviceInfo;
        captureInfo.date = new DateExtractor().extractFromFilename(i3av4File.getName());
        captureInfo.captureParameters = null;
        captureInfo.originalRawFilename = i3av4File.getName();
        captureInfo.orientation = ImageOrientation.TOPLEFT;
        captureInfo.camera = cameraConfig.cameraInfo;
        captureInfo.imageSize = cameraConfig.rawImageSize;
        captureInfo.extraJpegBytes = null;

        if (cameraConfig.cameraInfo.hasKnownMakernote())
        {
            captureInfo.makerNoteInfo = makerNoteInfoExtractor.extractFrom(mknBytes);
            captureInfo.whiteBalanceInfo = new WhiteBalanceInfoExtractor().extractFrom(captureInfo.makerNoteInfo, cameraConfig.cameraInfo.getColor());
        }
        else
        {
            captureInfo.makerNoteInfo = new MakerNoteInfo(mknBytes);
            captureInfo.whiteBalanceInfo = new WhiteBalanceInfoExtractor().extractFrom(cameraConfig.cameraInfo.getColor());
        }

        i3av4RAFile.seek(rawDataStart);
        DngWriter writer = DngWriter.open(dngPath);

        if (writer != null)
        {
            writer.writeMetadata(context, captureInfo);
            writer.writeImageData(new DngImageWriter(), i3av4RAFile);
            writer.writeExifInfo(captureInfo);
            writer.close();
        }
        else
        {
            i3av4RAFile.close();
            throw new IOException();
        }

        i3av4RAFile.close();
    }

    private class CameraConfig
    {
        RawImageSize rawImageSize;
        CameraInfo cameraInfo;

        CameraConfig(RawImageSize rawImageSize, CameraInfo cameraInfo)
        {
            this.rawImageSize = rawImageSize;
            this.cameraInfo = cameraInfo;
        }
    }

    private CameraConfig getBestCameraConfig(long i3av4Size)
    {
        long diff = Long.MAX_VALUE;
        long currentDiff;
        long currentLength;
        CameraConfig finalConfig = null;
        for (CameraConfig config : cameraConfigList)
        {
            currentLength = config.rawImageSize.getRawBufferLength();
            currentDiff = i3av4Size - currentLength;
            if (i3av4Size > currentLength && currentDiff < diff)
            {
                diff = currentDiff;
                finalConfig = config;
            }
        }
        return finalConfig;
    }
}
