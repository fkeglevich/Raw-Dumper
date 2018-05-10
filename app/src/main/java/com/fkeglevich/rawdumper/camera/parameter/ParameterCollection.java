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

package com.fkeglevich.rawdumper.camera.parameter;

import android.support.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.async.direct.LowLevelParameterInterface;
import com.fkeglevich.rawdumper.camera.async.direct.VirtualParameterInterface;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 19/09/17.
 */

public class ParameterCollection
{
    private final LowLevelParameterInterface parameterInterface;

    public ParameterCollection(LowLevelParameterInterface parameterInterface)
    {
        this.parameterInterface = parameterInterface;
    }

    public <T> void remove(final Parameter<T> parameter)
    {
        parameterInterface.remove(parameter.getKey());
    }

    public <T> boolean has(Parameter<T> parameter)
    {
        return parameterInterface.has(parameter.getKey());
    }

    public <T> T get(Parameter<T> parameter)
    {
        return parameter.getDecoder().decode(parameterInterface.get(parameter.getKey()));
    }

    public <T> void set(Parameter<T> parameter, T value)
    {
        final ParameterChangeEvent<T> changeEvent = new ParameterChangeEvent<>(value);
        parameter.getOnChanging().dispatchEvent(changeEvent);
        parameterInterface.set(parameter.getKey(), parameter.getEncoder().encode(value));
        parameter.getOnChanged().dispatchEvent(changeEvent);
    }
}
