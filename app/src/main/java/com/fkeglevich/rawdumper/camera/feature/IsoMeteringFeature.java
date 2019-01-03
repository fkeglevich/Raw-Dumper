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

import com.asus.camera.extensions.AsusCameraExtension;
import com.fkeglevich.rawdumper.camera.data.Iso;
import com.fkeglevich.rawdumper.camera.parameter.ExposureParameterFactory;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.service.ProDataMeteringService;
import com.fkeglevich.rawdumper.camera.service.available.SensorGainMeteringService;
import com.fkeglevich.rawdumper.raw.info.SensorInfo;
import com.fkeglevich.rawdumper.util.Nullable;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 13/10/17.
 */

public class IsoMeteringFeature extends Feature<Nullable<Iso>>
{
    private final SensorInfo sensorInfo;

    IsoMeteringFeature(ParameterCollection parameterCollection, SensorInfo sensorInfo)
    {
        super(ExposureParameterFactory.createIsoMeteringParameter(), parameterCollection);
        this.sensorInfo = sensorInfo;
    }

    @Override
    public boolean isAvailable()
    {
        return ProDataMeteringService.getInstance().isAvailable()
                || super.isAvailable()
                || SensorGainMeteringService.getInstance().isAvailable();
    }

    @Override
    public Nullable<Iso> getValue()
    {
        AsusCameraExtension.ProfessionalData data = ProDataMeteringService.getInstance().getLatestData();
        if (data != null)
            return Nullable.of(data.getIso());

        if (super.isAvailable())
            return super.getValue();
        else
        {
            Double gain = SensorGainMeteringService.getInstance().getValue();
            if (gain != null)
                return Nullable.of(Iso.decodeAnalogGain(gain, sensorInfo.getBaseISO()));
        }
        return Nullable.of(null);
    }
}
