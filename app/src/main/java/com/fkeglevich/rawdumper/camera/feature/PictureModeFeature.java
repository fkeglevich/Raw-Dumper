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
import com.fkeglevich.rawdumper.camera.data.mode.Mode;
import com.fkeglevich.rawdumper.camera.extension.VirtualParameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 30/10/17.
 */

public class PictureModeFeature extends ListFeature<Mode> implements VirtualFeature
{
    private final CameraActions cameraActions;

    PictureModeFeature(ParameterCollection parameterCollection, CameraActions cameraActions, List<Mode> valueList)
    {
        super(VirtualParameters.PICTURE_MODE, parameterCollection, new ListValidator<>(valueList), true);
        this.cameraActions = cameraActions;
    }

    @Override
    public void performUpdate()
    {
        cameraActions.setMode(getValue());
    }

    @Override
    public boolean isAvailable()
    {
        return true;
    }
}
