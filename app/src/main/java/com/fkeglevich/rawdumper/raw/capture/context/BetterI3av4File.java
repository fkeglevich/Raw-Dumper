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

/**
 * Fills a CaptureInfo object when the only sources of information are a DeviceInfo object,
 * capture parameters and a .i3av4 raw file.
 *
 * Created by Flávio Keglevich on 26/07/2017.
 */

public class BetterI3av4File extends I3av4FileOnly
{
    /**
     * Creates a new BetterI3av4File object
     */
    public BetterI3av4File()
    {
        super();
    }

    @Override
    public void fillCaptureInfo(CaptureInfo captureInfo)
    {
        if (!canFillCaptureInfo(captureInfo))
            throw new RuntimeException("This capture does not contain enough information!");

        Camera.Parameters captureParameters = captureInfo.captureParameters;
        super.fillCaptureInfo(captureInfo);
        captureInfo.captureParameters = captureParameters;
    }

    @Override
    public boolean canFillCaptureInfo(CaptureInfo captureInfo)
    {
        return super.canFillCaptureInfo(captureInfo) &&
               captureInfo.captureParameters != null;
    }
}
