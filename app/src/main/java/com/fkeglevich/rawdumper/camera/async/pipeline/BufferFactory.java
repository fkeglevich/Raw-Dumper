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
import com.fkeglevich.rawdumper.camera.extension.ICameraExtension;
import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.util.Mutable;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/11/17.
 */

class BufferFactory
{
    static byte[] createLargestImageCallbackBuffer(Mutable<ICameraExtension> cameraExtension,
                                                   CameraContext cameraContext)
    {
        int largestRawBufferSize = largestRawBufferSize(cameraContext.getSensorInfo());
        int largestYuvBufferSize = largestYuvBufferSize(cameraExtension);
        int largestBufferSize    = Math.max(largestRawBufferSize, largestYuvBufferSize);
        return new byte[largestBufferSize];
    }

    private static int largestYuvBufferSize(Mutable<ICameraExtension> cameraExtension)
    {
        Camera camera = cameraExtension.get().getCameraDevice();
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

        int largestNumPixels = 0;
        for (Camera.Size size : sizes)
        {
            int sizeNumPixels = size.width * size.height;
            if (sizeNumPixels > largestNumPixels)
                largestNumPixels = sizeNumPixels;
        }

        return largestNumPixels * 3 / 2;
    }

    private static int largestRawBufferSize(SensorInfo sensorInfo)
    {
        int largest = 0;
        for (RawImageSize imageSize : sensorInfo.getRawImageSizes())
            if (imageSize.getBufferLength() > largest)
                largest = imageSize.getBufferLength();

        return largest;
    }
}
