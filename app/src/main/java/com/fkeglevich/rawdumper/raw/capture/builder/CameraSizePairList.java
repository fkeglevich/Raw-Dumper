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

import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.info.DeviceInfo;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single list of CameraSizePair objects.
 *
 * Created by Flávio Keglevich on 25/08/2017.
 */

public class CameraSizePairList
{
    private final List<CameraSizePair> pairList;

    /**
     * Initializes a camera size pair list containing all valid combinations of
     * device cameras and raw image sizes.
     *
     * @param device    The DeviceInfo object whose the information is taken from
     */
    public CameraSizePairList(DeviceInfo device)
    {
        pairList = new ArrayList<>();
        for (ExtraCameraInfo extraCameraInfo : device.getCameras())
            for (RawImageSize imageSize : extraCameraInfo.getSensor().getRawImageSizes())
                getPairList().add(new CameraSizePair(imageSize, extraCameraInfo));
    }

    public List<CameraSizePair> getPairList()
    {
        return pairList;
    }

    /**
     * Finds the nearest CameraSizePair relative to the size of the i3av4 file.
     *
     * @param i3av4Size Size of the file
     * @return  The chosen camera size pair
     */
    public CameraSizePair getBestPair(long i3av4Size)
    {
        long diff = Long.MAX_VALUE;
        long currentDiff;
        long currentLength;
        CameraSizePair finalConfig = null;
        for (CameraSizePair config : pairList)
        {
            currentLength = config.getRawImageSize().getBufferLength();
            currentDiff = i3av4Size - currentLength;
            if (i3av4Size > currentLength && currentDiff < diff)
            {
                diff = currentDiff;
                finalConfig = config;
            }
        }
        return finalConfig;
    }
}
