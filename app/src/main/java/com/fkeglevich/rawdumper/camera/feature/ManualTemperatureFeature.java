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

import android.content.SharedPreferences;

import com.fkeglevich.rawdumper.camera.async.direct.AsyncParameterSender;
import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.ManualTemperatureRange;
import com.fkeglevich.rawdumper.camera.extension.AsusParameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.RangeValidator;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;
import com.fkeglevich.rawdumper.raw.info.ColorInfo;
import com.fkeglevich.rawdumper.util.MathUtil;

import androidx.annotation.NonNull;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class ManualTemperatureFeature extends RangeFeature<ManualTemperature>
{
    private final int[] temperatureRange;

    @NonNull
    private static ValueValidator<ManualTemperature, DataRange<ManualTemperature>> createRangeValidator(ColorInfo colorInfo)
    {
        return RangeValidator.create(ManualTemperatureRange.getFrom(colorInfo));
    }

    ManualTemperatureFeature(ColorInfo colorInfo, AsyncParameterSender asyncParameterSender, ParameterCollection parameterCollection)
    {
        super(asyncParameterSender, AsusParameters.MANUAL_TEMPERATURE, parameterCollection, createRangeValidator(colorInfo));
        temperatureRange = colorInfo.getTemperatureRange();
    }

    @Override
    public void setValueAsProportion(double proportion)
    {
        int lower = getAvailableValues().getLower().getNumericValue();
        int upper = getAvailableValues().getUpper().getNumericValue();

        double numericValue = calculateTemp(proportion, lower, upper);

        int finalValue = MathUtil.clamp((int) Math.round(numericValue), lower, upper);
        setValueAsync(ManualTemperature.create(finalValue));
    }

    @Override
    void storeValue(SharedPreferences.Editor editor)
    {
        if (!isAvailable()) return;

        ManualTemperature value = getValue();
        if (!ManualTemperature.DISABLED.equals(value))
            editor.putInt(parameter.getKey(), value.getNumericValue());
    }

    @Override
    void loadValue(SharedPreferences preferences)
    {
        if (!isAvailable()) return;

        int numValue = preferences.getInt(parameter.getKey(), 0);
        if (numValue != 0)
            setValue(ManualTemperature.create(numValue));
    }

    private double calculateTemp(double proportion, int lower, int upper)
    {
        if (proportion < 0.25)
            return lin(inverseLin(proportion, 0, 0.25),       temperatureRange[0], temperatureRange[1]);
        else if (proportion < 0.75)
            return lin(inverseLin(proportion, 0.25, 0.75),    temperatureRange[1], temperatureRange[2]);
        else
            return lin(inverseLin(proportion, 0.75, 1),       temperatureRange[2], temperatureRange[3]);
    }

    private double inverseLin(double x, double min, double max)
    {
        return (x - min) / (max - min);
    }

    private double lin(double x, double min, double max)
    {
        return (max - min) * x + min;
    }
}
