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

import java.util.Collections;
import java.util.List;

/**
 * Created by Flávio Keglevich on 08/07/2017.
 * TODO: Add a class header comment!
 */

public class CaptureSizes
{
    private final List<CaptureSize> pictureSizes;
    private final List<CaptureSize> previewSizes;
    private final List<CaptureSize> videoSizes;

    public CaptureSizes(Camera.Parameters parameters)
    {
        this.pictureSizes = generateCaptureSizeList(parameters.getSupportedPictureSizes());
        this.previewSizes = generateCaptureSizeList(parameters.getSupportedPreviewSizes());
        this.videoSizes = generateCaptureSizeList(parameters.getSupportedVideoSizes());
    }

    public CaptureSizes(List<Camera.Size> pictureSizes, List<Camera.Size> previewSizes, List<Camera.Size> videoSizes)
    {
        this.pictureSizes = generateCaptureSizeList(pictureSizes);
        this.previewSizes = generateCaptureSizeList(previewSizes);
        this.videoSizes = generateCaptureSizeList(videoSizes);
    }

    private List<CaptureSize> generateCaptureSizeList(List<Camera.Size> sizeList)
    {
        List<CaptureSize> result = ParameterFormatter.convertSizeList(sizeList);
        if (result.size() < 1)
            throw new RuntimeException("The capture size list should have at least one element!");
        Collections.sort(result);
        return Collections.unmodifiableList(result);
    }

    public List<CaptureSize> getPictureSizes()
    {
        return pictureSizes;
    }

    public List<CaptureSize> getPreviewSizes()
    {
        return previewSizes;
    }

    public List<CaptureSize> getVideoSizes()
    {
        return videoSizes;
    }

    public CaptureSize getLargestPictureSize()
    {
        return pictureSizes.get(pictureSizes.size() - 1);
    }

    public CaptureSize getSmallestPictureSize()
    {
        return pictureSizes.get(0);
    }

    public CaptureSize getLargestCompatiblePreviewSize(CaptureSize pictureSize)
    {
        CaptureSize result = null;
        for (CaptureSize size : previewSizes)
        {
            if (size.hasSameAspectRatio(pictureSize) && size.compareTo(pictureSize) <= 0)
            {
                if (result == null)
                    result = size;
                else if (size.compareTo(result) > 0)
                    result = size;
            }
        }
        return result;
    }
}
