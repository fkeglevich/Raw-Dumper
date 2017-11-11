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

package com.fkeglevich.rawdumper.camera.feature;

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;

import java.util.List;

import static com.fkeglevich.rawdumper.camera.extension.VirtualParameters.PICTURE_FORMAT;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class PictureFormatFeature extends WritableFeature<PictureFormat, List<PictureFormat>> implements VirtualFeature
{
    private final CameraActions cameraActions;

    PictureFormatFeature(ParameterCollection parameterCollection, CameraActions cameraActions)
    {
        super(PICTURE_FORMAT, parameterCollection, ListValidator.<PictureFormat>createInvalid(), true);
        this.cameraActions = cameraActions;
    }

    @Override
    public void performUpdate()
    {
        cameraActions.setPictureFormat(getValue());
    }

    @Override
    public boolean isAvailable()
    {
        return true;
    }
}
