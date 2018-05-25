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

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 25/09/17.
 */

public abstract class WritableFeature<T, A> extends Feature<T>
{
    private ValueValidator<T, A> validator;
    private final boolean isMutable;

    public EventDispatcher<Void> onValidatorChanged = new SimpleDispatcher<>();

    WritableFeature(Parameter<T> featureParameter, ParameterCollection parameterCollection, ValueValidator<T, A> validator)
    {
        this(featureParameter, parameterCollection, validator, false);
    }

    WritableFeature(Parameter<T> featureParameter, ParameterCollection parameterCollection, ValueValidator<T, A> validator, boolean isMutable)
    {
        super(featureParameter, parameterCollection);
        this.validator = validator;
        this.isMutable = isMutable;
    }

    @Override
    public boolean isAvailable()
    {
        return getValidator().isAvailable();
    }

    public void setValue(T value)
    {
        checkFeatureAvailability(this);
        if (!getValidator().isValid(value))
            throw new IllegalArgumentException();

        parameterCollection.set(parameter, value);
    }

    public A getAvailableValues()
    {
        return getValidator().getAvailableValues();
    }

    public void changeValidator(ValueValidator<T, A> newValidator, T newValue)
    {
        if (!isMutable())
            throw new IllegalStateException("Attempting to change the validator of a immutable WritableFeature!");

        if (!newValidator.isValid(newValue))
            throw new IllegalArgumentException();

        validator = newValidator;
        setValue(newValue);
        onValidatorChanged.dispatchEvent(null);
    }

    public boolean isMutable()
    {
        return isMutable;
    }

    public ValueValidator<T, A> getValidator()
    {
        return validator;
    }
}