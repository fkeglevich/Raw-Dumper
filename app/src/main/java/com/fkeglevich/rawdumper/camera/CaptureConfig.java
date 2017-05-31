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

package com.fkeglevich.rawdumper.camera;

import android.hardware.Camera;

import com.intel.camera.extensions.IntelCamera;

import java.util.Collections;
import java.util.List;

/**
 * Created by Flávio Keglevich on 28/05/2017.
 * TODO: Add a class header comment!
 */

public class CaptureConfig
{
    private List<CaptureSize> pictureSizes;
    private List<CaptureSize> previewSizes;
    private List<CaptureSize> videoSizes;
    private IntelCamera camera;

    public CaptureConfig(IntelCamera camera)
    {
        Camera.Parameters parameters = camera.getCameraDevice().getParameters();
        this.camera = camera;
        this.pictureSizes = generateCaptureSizeList(parameters.getSupportedPictureSizes());
        this.previewSizes = generateCaptureSizeList(parameters.getSupportedPreviewSizes());
        this.videoSizes = generateCaptureSizeList(parameters.getSupportedVideoSizes());
    }

    private List<CaptureSize> generateCaptureSizeList(List<Camera.Size> sizeList)
    {
        List<CaptureSize> result = ParameterFormatter.convertSizeList(sizeList);
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    public List<CaptureSize> getPictureSizes()
    {
        return pictureSizes;
    }

    public CaptureSize getSelectedPictureSize()
    {
        return ParameterFormatter.toCaptureSize(camera.getCameraDevice().getParameters().getPictureSize());
    }

    public void setSelectedPictureSize(CaptureSize size)
    {
        Camera.Parameters parameters = camera.getCameraDevice().getParameters();
        parameters.setPictureSize(size.getWidth(), size.getHeight());
        camera.getCameraDevice().setParameters(parameters);
    }

    public List<CaptureSize> getPreviewSizes()
    {
        return previewSizes;
    }

    public CaptureSize getSelectedPreviewSize()
    {
        return ParameterFormatter.toCaptureSize(camera.getCameraDevice().getParameters().getPreviewSize());
    }

    public void setSelectedPreviewSize(CaptureSize size)
    {
        Camera.Parameters parameters = camera.getCameraDevice().getParameters();
        parameters.setPreviewSize(size.getWidth(), size.getHeight());
        camera.getCameraDevice().setParameters(parameters);
    }

    public List<CaptureSize> getVideoSizes()
    {
        return videoSizes;
    }
}
