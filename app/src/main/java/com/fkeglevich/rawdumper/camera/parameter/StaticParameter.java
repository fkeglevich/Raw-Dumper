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

import com.fkeglevich.rawdumper.util.event.AsyncEventDispatcher;
import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.SimpleDispatcher;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 20/09/17.
 */

class StaticParameter<T> extends Parameter<T>
{
    static <T> Parameter<T> create(String key, ValueDecoder<T> decoder, ValueEncoder<T> encoder)
    {
        return new StaticParameter<>(key, decoder, encoder, false);
    }

    static <T> Parameter<T> createAsyncEvent(String key, ValueDecoder<T> decoder, ValueEncoder<T> encoder)
    {
        return new StaticParameter<>(key, decoder, encoder, true);
    }

    static <T> Parameter<T> createReadOnly(String key, ValueDecoder<T> decoder)
    {
        return new StaticParameter<>(key, decoder, null, false);
    }

    private final String key;
    private final ValueDecoder<T> decoder;
    private final ValueEncoder<T> encoder;
    private final EventDispatcher<ParameterChangeEvent<T>> onChanging;
    private final EventDispatcher<ParameterChangeEvent<T>> onChanged;

    private StaticParameter(String key, ValueDecoder<T> decoder, ValueEncoder<T> encoder, boolean async)
    {
        this.key = key;
        this.decoder = decoder;
        this.encoder = encoder;
        this.onChanging = async ? new AsyncEventDispatcher<>() : new SimpleDispatcher<>();
        this.onChanged = async ? new AsyncEventDispatcher<>() : new SimpleDispatcher<>();
    }

    @Override
    public String getKey()
    {
        return key;
    }

    @Override
    ValueDecoder<T> getDecoder()
    {
        if (decoder == null) throw new RuntimeException("This parameter doesn't have a decoder!");
        return decoder;
    }

    @Override
    ValueEncoder<T> getEncoder()
    {
        if (encoder == null) throw new RuntimeException("This parameter is read-only!");
        return encoder;
    }

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
