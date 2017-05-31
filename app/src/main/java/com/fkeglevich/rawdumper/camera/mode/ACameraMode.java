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

package com.fkeglevich.rawdumper.camera.mode;

import com.fkeglevich.rawdumper.camera.CaptureConfig;
import com.fkeglevich.rawdumper.camera.CaptureSize;
import com.fkeglevich.rawdumper.camera.ImageFormat;
import com.fkeglevich.rawdumper.camera.ModeInfo;
import com.fkeglevich.rawdumper.camera.pipeline.APicturePipeline;
import com.intel.camera.extensions.IntelCamera;

import java.util.List;

/**
 * Created by Flávio Keglevich on 22/04/2017.
 * TODO: Add a class header comment!
 */

public abstract class ACameraMode
{
    protected CaptureConfig captureConfig;
    protected List<ImageFormat> formats;
    protected APicturePipeline picturePipeline;

    public abstract void setupMode(IntelCamera camera);

    public void resetMode(IntelCamera camera)
    {
        captureConfig = null;
        formats = null;
        picturePipeline = null;
    }

    public APicturePipeline getPicturePipeline()
    {
        return picturePipeline;
    }

    public CaptureConfig getCaptureConfig()
    {
        return captureConfig;
    }

    public List<ImageFormat> getFormats()
    {
        return formats;
    }

    public abstract ModeInfo getModeInfo();

    /*public abstract int getExposureMode();
    public abstract int getWhiteBalanceMode();
    public abstract int getFocusMode();
    public abstract int getFlashMode();
    pubblic abstract int getPreviewMode();*/
}
