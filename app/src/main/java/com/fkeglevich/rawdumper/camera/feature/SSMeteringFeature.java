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

import com.fkeglevich.rawdumper.camera.data.ShutterSpeed;
import com.fkeglevich.rawdumper.camera.parameter.ExposureParameterFactory;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.service.available.CoarseIntegrationTimeMeteringService;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.util.Nullable;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 16/10/17.
 */

public class SSMeteringFeature extends Feature<Nullable<ShutterSpeed>>
{
    private final SensorInfo sensorInfo;

    SSMeteringFeature(ParameterCollection parameterCollection, SensorInfo sensorInfo)
    {
        super(ExposureParameterFactory.createSSMeteringParameter(), parameterCollection);
        this.sensorInfo = sensorInfo;
    }

    @Override
    public boolean isAvailable()
    {
        return super.isAvailable() || CoarseIntegrationTimeMeteringService.getInstance().isAvailable();
    }

    @Override
    public Nullable<ShutterSpeed> getValue()
    {
        if (super.isAvailable())
            return super.getValue();
        else
        {
            Integer integrationTime = CoarseIntegrationTimeMeteringService.getInstance().getValue();
            if (integrationTime != null)
            {
                ShutterSpeed shutterSpeed = ShutterSpeed.create(((double)integrationTime) / ((double)sensorInfo.getIntegrationTimeScale()));
                return Nullable.of(shutterSpeed);
            }
        }
        return Nullable.of(null);
    }
}