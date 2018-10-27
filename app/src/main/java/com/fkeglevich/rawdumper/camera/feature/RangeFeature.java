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

import com.fkeglevich.rawdumper.camera.async.direct.AsyncParameterSender;
import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

/**
 * TODO: Add class header
 *
 * Created by Flávio Keglevich on 09/05/17.
 */

public abstract class RangeFeature<T extends Comparable<T>> extends WritableFeature<T, DataRange<T>>
{
    private final AsyncParameterSender asyncParameterSender;

    RangeFeature(AsyncParameterSender asyncParameterSender, Parameter<T> featureParameter, ParameterCollection parameterCollection, ValueValidator<T, DataRange<T>> validator)
    {
        super(featureParameter, parameterCollection, validator);
        this.asyncParameterSender = asyncParameterSender;
    }

    public abstract void setValueAsProportion(double proportion);

    void setValueAsync(T value)
    {
        checkFeatureAvailability(this);
        if (!getValidator().isValid(value))
            throw new IllegalArgumentException();

        asyncParameterSender.sendParameterAsync(parameter, value);
    }
}
