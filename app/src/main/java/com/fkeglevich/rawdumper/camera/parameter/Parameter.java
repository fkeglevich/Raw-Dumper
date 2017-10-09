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

import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 22/09/17.
 */

public abstract class Parameter<T> implements DeltaParameter<T>
{
    private final EventDispatcher<ParameterChangeEvent<T>> onChanging = new SimpleDispatcher<>();
    private final EventDispatcher<ParameterChangeEvent<T>> onChanged = new SimpleDispatcher<>();

    abstract String getKey();

    abstract ValueDecoder<T> getDecoder();

    abstract ValueEncoder<T> getEncoder();

    @Override
    public EventDispatcher<ParameterChangeEvent<T>> getOnChanging()
    {
        return onChanging;
    }

    @Override
    public EventDispatcher<ParameterChangeEvent<T>> getOnChanged()
    {
        return onChanged;
    }
}
