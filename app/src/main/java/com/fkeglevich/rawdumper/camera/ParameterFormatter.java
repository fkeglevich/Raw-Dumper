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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Flávio Keglevich on 24/04/2017.
 * TODO: Add a class header comment!
 */

public class ParameterFormatter
{
    public static List<CaptureSize> convertSizeList(List<Camera.Size> list)
    {
        List<CaptureSize> result = new ArrayList<CaptureSize>();
        for (Camera.Size size : list)
            result.add(toCaptureSize(size));
        return result;
    }

    public static CaptureSize toCaptureSize(Camera.Size size)
    {
        return new CaptureSize(size.width, size.height);
    }

    public static Camera.Size toCameraSize(Camera camera, CaptureSize size)
    {
        return camera.new Size(size.getWidth(), size.getHeight());
    }

    public static List<CaptureSize> unmodfConvertSizeList(List<Camera.Size> list)
    {
        return Collections.unmodifiableList(convertSizeList(list));
    }
}
