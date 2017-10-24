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

import android.util.Log;

import com.fkeglevich.rawdumper.camera.data.Displayable;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 25/09/17.
 */

public abstract class WritableFeature<T, A> extends Feature<T>
{
    private final ValueValidator<T, A> validator;

    WritableFeature(Parameter<T> featureParameter, ParameterCollection parameterCollection, ValueValidator<T, A> validator)
    {
        super(featureParameter, parameterCollection);
        this.validator = validator;
    }

    @Override
    public boolean isAvailable()
    {
        return validator.isAvailable();
    }

    public void setValue(T value)
    {
        checkFeatureAvailability(this);
        if (!validator.isValid(value))
            throw new IllegalArgumentException();

        parameterCollection.set(parameter, value);
    }

    public A getAvailableValues()
    {
        return validator.getAvailableValues();
    }

}
