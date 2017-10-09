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

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 22/09/17.
 */

public class ParameterFactory
{
    public static <T> Parameter<T> create(String key, Class<T> valueClass)
    {
        return StaticParameter.create(key, Decoders.selectDecoder(valueClass), Encoders.selectEncoder(valueClass));
    }

    public static <T> Parameter<List<T>> createList(String key, Class<T> valueClass)
    {
        return StaticParameter.create(key, Decoders.selectListDecoder(valueClass), Encoders.selectListEncoder(valueClass));
    }

    public static <T> Parameter<T> createReadOnly(String key, Class<T> valueClass)
    {
        return StaticParameter.createReadOnly(key, Decoders.selectDecoder(valueClass));
    }

    public static <T> Parameter<List<T>> createReadOnlyList(String key, Class<T> valueClass)
    {
        return StaticParameter.createReadOnly(key, Decoders.selectListDecoder(valueClass));
    }

    public static <T> Parameter<T> createRuntime(EventDispatcher<Parameter<T>> parameterImplementor)
    {
        return new RuntimeParameter<>(parameterImplementor);
    }
}
