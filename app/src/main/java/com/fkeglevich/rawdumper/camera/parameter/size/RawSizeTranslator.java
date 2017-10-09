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

package com.fkeglevich.rawdumper.camera.parameter.size;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 18/09/17.
 */

class RawSizeTranslator { } /* extends PictureSizeTranslator
{
    private final SharedParameters parameters;
    private final SensorInfo sensorInfo;

    RawSizeTranslator(SharedParameters parameters, SensorInfo sensorInfo)
    {
        this.parameters = parameters;
        this.sensorInfo = sensorInfo;
    }

    @Override
    List<CaptureSize> getPictureSizes()
    {
        ArrayList<CaptureSize> result = new ArrayList<>();
        for (RawImageSize size : sensorInfo.getRawImageSizes())
            result.add(new CaptureSize(size.getPaddedWidth(), size.getPaddedHeight()));

        return result;
    }

    @Override
    CaptureSize getCurrentSize()
    {
        Camera.Size pictureSize = parameters.getRawParameters().getPictureSize();
        for (RawImageSize size : sensorInfo.getRawImageSizes())
            if (pictureSize.width == size.getWidth() && pictureSize.height == size.getHeight())
                return new CaptureSize(size.getPaddedWidth(), size.getPaddedHeight());

        return null;
    }

    @Override
    void setCurrentSize(CaptureSize pictureSize)
    {
        for (RawImageSize size : sensorInfo.getRawImageSizes())
            if (pictureSize.getWidth() == size.getPaddedWidth() && pictureSize.getHeight() == size.getPaddedHeight())
            {
                Camera.Parameters rawParameters = parameters.getRawParameters();
                rawParameters.setPictureSize(size.getWidth(), size.getHeight());
                parameters.setRawParameters(rawParameters);
            }
    }
}
*/