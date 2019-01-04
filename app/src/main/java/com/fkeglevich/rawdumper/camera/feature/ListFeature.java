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

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

import java.util.List;

public class ListFeature<T> extends WritableFeature<T, List<T>>
{
    ListFeature(Parameter<T> featureParameter, ParameterCollection parameterCollection, ValueValidator<T, List<T>> validator)
    {
        super(featureParameter, parameterCollection, validator);
    }

    ListFeature(Parameter<T> featureParameter, ParameterCollection parameterCollection, ValueValidator<T, List<T>> validator, boolean isMutable)
    {
        super(featureParameter, parameterCollection, validator, isMutable);
    }

    @Override
    void storeValue(SharedPreferences.Editor editor)
    {
        if (!isAvailable()) return;

        int index = getValidator().getAvailableValues().indexOf(getValue());
        editor.putInt(parameter.getKey(), index);
    }

    @Override
    void loadValue(SharedPreferences preferences)
    {
        if (!isAvailable()) return;

        int index = preferences.getInt(parameter.getKey(), -1);
        if (index >= 0 && index < getValidator().getAvailableValues().size())
            setValue(getValidator().getAvailableValues().get(index));
    }
}
