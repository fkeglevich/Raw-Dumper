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

package com.fkeglevich.rawdumper.camera.async.pipeline.strategy;

import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.data.PictureMode;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class CaptureStrategyManager
{
    public static class ModeFormatCombination
    {
        public final PictureMode pictureMode;
        public final PictureFormat pictureFormat;

        public ModeFormatCombination(@NonNull PictureMode pictureMode, @NonNull PictureFormat pictureFormat)
        {
            this.pictureMode = pictureMode;
            this.pictureFormat = pictureFormat;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ModeFormatCombination that = (ModeFormatCombination) o;
            if (pictureMode != that.pictureMode) return false;
            return pictureFormat == that.pictureFormat;
        }

        @Override
        public int hashCode()
        {
            int result = pictureMode.hashCode();
            result = 31 * result + pictureFormat.hashCode();
            return result;
        }
    }

    private final Map<ModeFormatCombination, CaptureStrategy> innerMap = new HashMap<>();

    public CaptureStrategyManager(ParameterCollection parameterCollection, SensorInfo sensorInfo)
    {
        PictureSizeStrategy jpegSizeStrategy    = new JpegSizeStrategy(parameterCollection);
        PictureSizeStrategy rawSizeStrategy     = new RawSizeStrategy(sensorInfo);
        PictureSizeStrategy jpegLLSizeStrategy  = new InvalidSizeStrategy();
        PictureSizeStrategy rawLLSizeStrategy   = new LLRawSizeStrategy(sensorInfo);

        addCaptureStratey(PictureMode.NORMAL, PictureFormat.JPEG, jpegSizeStrategy);
        addCaptureStratey(PictureMode.NORMAL, PictureFormat.YUV, jpegSizeStrategy);
        addCaptureStratey(PictureMode.NORMAL, PictureFormat.RAW, rawSizeStrategy);

        addCaptureStratey(PictureMode.LOW_LIGHT, PictureFormat.JPEG, jpegLLSizeStrategy);
        addCaptureStratey(PictureMode.LOW_LIGHT, PictureFormat.YUV, jpegLLSizeStrategy);
        addCaptureStratey(PictureMode.LOW_LIGHT, PictureFormat.RAW, rawLLSizeStrategy);
    }

    private void addCaptureStratey(@NonNull PictureMode pictureMode, @NonNull PictureFormat pictureFormat, PictureSizeStrategy sizeStrategy)
    {
        innerMap.put(   new ModeFormatCombination(pictureMode, pictureFormat),
                        new CaptureStrategy(sizeStrategy, pictureFormat.getPicturePipeline()));
    }

    public CaptureStrategy getBestStrategy(@NonNull PictureMode pictureMode, @NonNull PictureFormat pictureFormat)
    {
        ModeFormatCombination combination = new ModeFormatCombination(pictureMode, pictureFormat);
        return innerMap.get(combination);
    }
}
