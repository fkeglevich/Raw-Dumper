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

package com.fkeglevich.rawdumper.raw.capture.context;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.raw.capture.CaptureInfo;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;

/**
 * Created by Flávio Keglevich on 26/07/2017.
 * TODO: Add a class header comment!
 */

public class NoI3av4File implements ICaptureContext
{
    public NoI3av4File()
    {

    }

    @Override
    public void fillCaptureInfo(CaptureInfo captureInfo)
    {
        captureInfo.imageSize = getRawImageSize(captureInfo.camera.getSensor(), captureInfo.captureParameters.getPictureSize());
    }

    @Override
    public boolean canFillCaptureInfo(CaptureInfo captureInfo)
    {
        return  captureInfo.device != null &&
                captureInfo.camera != null &&
                captureInfo.date != null;
    }

    private RawImageSize getRawImageSize(SensorInfo sensorInfo, Camera.Size pictureSize)
    {
        RawImageSize[] rawImageSizes = sensorInfo.getRawImageSizes();
        for (RawImageSize rawImageSize : rawImageSizes)
        {
            if (rawImageSize.getWidth() == pictureSize.width && rawImageSize.getHeight() == pictureSize.height)
                return rawImageSize;
        }
        return null;
    }
}
