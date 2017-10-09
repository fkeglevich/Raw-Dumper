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

import android.hardware.Camera;

import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

/**
 * A simple immutable pair made of a ExtraCameraInfo and RawImageSize.
 *
 * Created by Flávio Keglevich on 26/07/2017.
 */

class CameraSizePair
{
    private final RawImageSize rawImageSize;
    private final ExtraCameraInfo extraCameraInfo;

    public static CameraSizePair createFromParameters(Camera.Parameters parameters, ExtraCameraInfo extraCameraInfo)
    {
        Camera.Size size = parameters.getPictureSize();
        RawImageSize rawImageSize = extraCameraInfo.getSensor().getRawImageSizeFromSize(size);
        return new CameraSizePair(rawImageSize, extraCameraInfo);
    }

    /**
     * Creates a new CameraSizePair object
     *
     * @param rawImageSize  The raw image size
     * @param extraCameraInfo    The camera info
     */
    CameraSizePair(RawImageSize rawImageSize, ExtraCameraInfo extraCameraInfo)
    {
        this.rawImageSize = rawImageSize;
        this.extraCameraInfo = extraCameraInfo;
    }

    RawImageSize getRawImageSize()
    {
        return rawImageSize;
    }

    ExtraCameraInfo getExtraCameraInfo()
    {
        return extraCameraInfo;
    }
}
