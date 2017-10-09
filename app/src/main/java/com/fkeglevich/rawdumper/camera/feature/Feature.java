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

import com.fkeglevich.rawdumper.camera.exception.UnavailableFeatureException;
import com.fkeglevich.rawdumper.camera.parameter.DeltaParameter;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterChangeEvent;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 05/10/17.
 */

public abstract class Feature<T> implements Available, DeltaParameter<T>
{
    protected final Parameter<T> parameter;
    protected final ParameterCollection parameterCollection;

    static <T> void checkFeatureAvailability(Feature<T> feature)
    {
        if (!feature.isAvailable())
            throw new UnavailableFeatureException();
    }

    public static <T> void clearEventDispatchers(Feature<T> feature)
    {
        feature.getOnChanging().removeAllListeners();
        feature.getOnChanged().removeAllListeners();
    }

    Feature(Parameter<T> parameter, ParameterCollection parameterCollection)
    {
        this.parameter = parameter;
        this.parameterCollection = parameterCollection;
    }

    public T getValue()
    {
        checkFeatureAvailability(this);
        return parameterCollection.get(parameter);
    }

    @Override
    public boolean isAvailable()
    {
        return parameterCollection.has(parameter);
    }

    @Override
    public EventDispatcher<ParameterChangeEvent<T>> getOnChanging()
    {
        return parameter.getOnChanging();
    }

    @Override
    public EventDispatcher<ParameterChangeEvent<T>> getOnChanged()
    {
        return parameter.getOnChanged();
    }
}
