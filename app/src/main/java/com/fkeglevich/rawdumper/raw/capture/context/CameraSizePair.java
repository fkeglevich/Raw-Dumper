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

import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.CameraInfo;

/**
 * A simple immutable pair made of a CameraInfo and RawImageSize.
 *
 * Created by Flávio Keglevich on 26/07/2017.
 */

class CameraSizePair
{
    private final RawImageSize rawImageSize;
    private final CameraInfo cameraInfo;

    /**
     * Creates a new CameraSizePair object
     *
     * @param rawImageSize  The raw image size
     * @param cameraInfo    The camera info
     */
    CameraSizePair(RawImageSize rawImageSize, CameraInfo cameraInfo)
    {
        this.rawImageSize = rawImageSize;
        this.cameraInfo = cameraInfo;
    }

    RawImageSize getRawImageSize()
    {
        return rawImageSize;
    }

    CameraInfo getCameraInfo()
    {
        return cameraInfo;
    }
}
