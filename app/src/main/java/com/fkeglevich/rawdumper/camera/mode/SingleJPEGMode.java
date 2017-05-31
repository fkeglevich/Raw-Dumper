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

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.CaptureConfig;
import com.fkeglevich.rawdumper.camera.ImageFormat;
import com.fkeglevich.rawdumper.camera.ModeInfo;
import com.fkeglevich.rawdumper.camera.ParameterFormatter;
import com.fkeglevich.rawdumper.camera.pipeline.JPEGPipeline;
import com.intel.camera.extensions.IntelCamera;

import java.util.Collections;

/**
 * Created by Flávio Keglevich on 22/04/2017.
 * TODO: Add a class header comment!
 */

public class SingleJPEGMode extends ACameraMode
{
    public SingleJPEGMode()
    {   }

    @Override
    public void setupMode(IntelCamera camera)
    {
        Camera.Parameters parameters = camera.getCameraDevice().getParameters();
        captureConfig = new CaptureConfig(camera);
        formats = Collections.unmodifiableList(Collections.singletonList(ImageFormat.JPEG));
        picturePipeline = new JPEGPipeline(camera);
    }

    @Override
    public ModeInfo getModeInfo()
    {
        return ModeInfo.SINGLE_JPEG;
    }
}
