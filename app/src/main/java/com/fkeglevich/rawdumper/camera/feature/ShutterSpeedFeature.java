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

import com.fkeglevich.rawdumper.camera.action.CameraActions;
import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.extension.IntelParameters;
import com.fkeglevich.rawdumper.camera.parameter.ExposureParameterFactory;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.ValueCollectionFactory;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;
import com.fkeglevich.rawdumper.raw.info.ExposureInfo;

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 26/09/17.
 */

public class ShutterSpeedFeature extends ListFeature<ShutterSpeed> implements VirtualFeature
{
    private final CameraActions cameraActions;
    private final boolean requiresIntelCamera;

    @NonNull
    static ShutterSpeedFeature create(ExposureInfo exposureInfo, ParameterCollection parameterCollection, CameraActions cameraActions)
    {
        Parameter<ShutterSpeed> ssParameter = ExposureParameterFactory.createSSParameter(exposureInfo);
        List<String> ssValues = exposureInfo.getShutterSpeedValues();
        List<ShutterSpeed> valueList = ValueCollectionFactory.decodeValueList(ssParameter, ssValues);
        return new ShutterSpeedFeature(ssParameter, parameterCollection, valueList, cameraActions, IntelParameters.KEY_SHUTTER.equals(exposureInfo.getShutterSpeedParameter()));
    }

    private ShutterSpeedFeature(Parameter<ShutterSpeed> parameter, ParameterCollection parameterCollection, List<ShutterSpeed> valueList, CameraActions cameraActions, boolean requiresIntelCamera)
    {
        super(parameter, parameterCollection, new ListValidator<>(valueList));
        this.cameraActions = cameraActions;
        this.requiresIntelCamera = requiresIntelCamera;

        if (getAvailableValues().contains(ShutterSpeed.AUTO))
            setValue(ShutterSpeed.AUTO);

        getOnChanged().addListener(eventData -> performUpdate());
    }

    @Override
    public void performUpdate()
    {
        cameraActions.notifyShutterSpeed(getValue(), requiresIntelCamera);
    }
}
