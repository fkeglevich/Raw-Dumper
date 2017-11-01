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
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class JpegStrategy extends PictureSizeStrategy
{
    private final ParameterCollection parameterCollection;
    private final Parameter<List<CaptureSize>> pictureSizeValues;

    public JpegStrategy(ParameterCollection parameterCollection)
    {
        this.parameterCollection = parameterCollection;
        this.pictureSizeValues = Parameters.PICTURE_SIZE_VALUES;
    }

    @Override
    public List<CaptureSize> getAvailableSizes()
    {
        return parameterCollection.get(pictureSizeValues);
    }
}
