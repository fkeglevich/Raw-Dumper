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

class JpegSizeTranslator //extends PictureSizeTranslator
{
    //private final SharedParameters parameters;
    /*
    JpegSizeTranslator(SharedParameters parameters)
    {
        this.parameters = parameters;
    }

    @Override
    public List<CaptureSize> getPictureSizes()
    {
        Camera.Parameters rawParameters = parameters.getRawParameters();
        return ParameterHelper.convertSizeList(rawParameters.getSupportedPictureSizes());
    }

    @Override
    public CaptureSize getCurrentSize()
    {
        Camera.Size pictureSize = parameters.getRawParameters().getPictureSize();
        return new CaptureSize(pictureSize.width, pictureSize.height);
    }

    @Override
    public void setCurrentSize(CaptureSize pictureSize)
    {
        Camera.Parameters rawParameters = parameters.getRawParameters();
        rawParameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
        parameters.setRawParameters(rawParameters);
    }*/
}
