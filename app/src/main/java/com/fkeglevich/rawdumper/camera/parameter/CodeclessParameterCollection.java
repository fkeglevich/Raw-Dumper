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

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 31/10/17.
 */

public class CodeclessParameterCollection extends ParameterCollection
{
    private final Map<String, Object> innerMap = new HashMap<>();

    public CodeclessParameterCollection()
    {
        super(null);
    }

    @Override
    public <T> void remove(Parameter<T> parameter)
    {
        innerMap.remove(parameter.getKey());
    }

    @Override
    public <T> boolean has(Parameter<T> parameter)
    {
        return innerMap.containsKey(parameter.getKey());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Parameter<T> parameter)
    {
        return (T) innerMap.get(parameter.getKey());
    }

    @Override
    public <T> void set(Parameter<T> parameter, T value)
    {
        final ParameterChangeEvent<T> changeEvent = new ParameterChangeEvent<>(value);
        parameter.getOnChanging().dispatchEvent(changeEvent);
        override(parameter, value);
        parameter.getOnChanged().dispatchEvent(changeEvent);
    }

    @Override
    public <T> void override(Parameter<T> parameter, T value)
    {
        innerMap.put(parameter.getKey(), value);
    }
}
