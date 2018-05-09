/*
 * Copyright 2018, Flávio Keglevich
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

import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.ManualTemperatureRange;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.RangeValidator;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class ManualTemperatureFeature extends ProportionFeature<ManualTemperature, ManualTemperatureRange>
{
    ManualTemperatureFeature(ParameterCollection parameterCollection)
    {
        super(AsusParameters.MANUAL_TEMPERATURE, parameterCollection, RangeValidator.create(parameterCollection, AsusParameters.MANUAL_TEMPERATURE_RANGE));
    }

    private long nanos = System.nanoTime();

    @Override
    public void setValueAsProportion(double proportion)
    {
        if (System.nanoTime() - nanos < 30e6)
        {
            nanos = System.nanoTime();
            return;
        }

        int lower = getAvailableValues().getLower().getNumericValue();
        int upper = getAvailableValues().getUpper().getNumericValue();

        double numericValue = (upper - lower) * proportion + lower;

        ManualTemperature manualFocus = ManualTemperature.create((int) Math.round(numericValue));
        setValue(manualFocus);
    }
}
