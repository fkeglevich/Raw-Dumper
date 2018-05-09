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

import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.async.direct.AsyncParameterSender;
import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.ManualTemperatureRange;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.RangeValidator;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class ManualTemperatureFeature extends ProportionFeature<ManualTemperature, ManualTemperatureRange>
{
    @NonNull
    private static ValueValidator<ManualTemperature, ManualTemperatureRange> createRangeValidator(ColorInfo colorInfo)
    {
        return RangeValidator.create(ManualTemperatureRange.getFrom(colorInfo));
    }

    ManualTemperatureFeature(ColorInfo colorInfo, AsyncParameterSender asyncParameterSender, ParameterCollection parameterCollection)
    {
        super(asyncParameterSender, AsusParameters.MANUAL_TEMPERATURE, parameterCollection, createRangeValidator(colorInfo));
    }

    @Override
    public void setValueAsProportion(double proportion)
    {
        int lower = getAvailableValues().getLower().getNumericValue();
        int upper = getAvailableValues().getUpper().getNumericValue();

        double numericValue = (upper - lower) * proportion + lower;

        ManualTemperature manualTemperature = ManualTemperature.create((int) Math.round(numericValue));
        setValueAsync(manualTemperature);
    }
}
