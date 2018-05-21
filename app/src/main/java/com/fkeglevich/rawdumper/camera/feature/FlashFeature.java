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

import android.support.annotation.NonNull;
import android.util.Log;

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.async.CameraContext;
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;

import java.util.Arrays;
import java.util.List;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 13/10/17.
 */

public class FlashFeature extends WritableFeature<Flash, List<Flash>> implements VirtualFeature
{
    private final CameraActions cameraActions;
    private final List<Flash> originalList;

    @NonNull
    private static ListValidator<Flash> createValidator(ParameterCollection parameterCollection, CameraContext cameraContext)
    {
        List<Flash> flashList = parameterCollection.get(Parameters.FLASH_MODE_VALUES);
        if (cameraContext.getCameraInfo().getFacing() == CAMERA_FACING_FRONT)
        {
            if (flashList.isEmpty()) flashList.add(Flash.OFF);
            if (flashList.size() == 1) flashList.add(Flash.SCREEN);
        }

        return new ListValidator<>(flashList);
    }

    FlashFeature(ParameterCollection parameterCollection, ParameterCollection cameraParameterCollection, CameraActions cameraActions, CameraContext cameraContext)
    {
        super(Parameters.FLASH_MODE, parameterCollection, createValidator(cameraParameterCollection, cameraContext));
        this.originalList = cameraParameterCollection.get(Parameters.FLASH_MODE_VALUES);
        this.cameraActions = cameraActions;
        setValue(Flash.OFF);
        getOnChanged().addListener(eventData -> performUpdate());
    }

    @Override
    public boolean isAvailable()
    {
        return getAvailableValues().size() > 1;
    }

    @Override
    public void performUpdate()
    {
        Flash value = getValue();
        if (originalList.contains(value))
            cameraActions.setFlash(value);
    }
}
