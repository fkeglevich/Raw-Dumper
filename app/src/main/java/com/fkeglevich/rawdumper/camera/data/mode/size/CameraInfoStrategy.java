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

package com.fkeglevich.rawdumper.camera.data.mode.size;

import com.fkeglevich.rawdumper.camera.data.CaptureSize;
import com.fkeglevich.rawdumper.raw.info.ExtraCameraInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

abstract class CameraInfoStrategy<T> extends PictureSizeStrategy
{
    private final List<CaptureSize> sizeList;

    CameraInfoStrategy(ExtraCameraInfo cameraInfo)
    {
        sizeList = initRawSizes(cameraInfo);
    }

    private List<CaptureSize> initRawSizes(ExtraCameraInfo cameraInfo)
    {
        T[] sizes = getSizeListFromCameraInfo(cameraInfo);
        if (sizes.length > 0)
        {
            List<CaptureSize> result = new ArrayList<>();
            for (T size : sizes)
                result.add(toCaptureSize(size));

            return Collections.unmodifiableList(result);
        }
        else
            return Collections.emptyList();
    }

    @Override
    public List<CaptureSize> getAvailableSizes()
    {
        return sizeList;
    }

    abstract T[] getSizeListFromCameraInfo(ExtraCameraInfo cameraInfo);

    abstract CaptureSize toCaptureSize(T size);
}
