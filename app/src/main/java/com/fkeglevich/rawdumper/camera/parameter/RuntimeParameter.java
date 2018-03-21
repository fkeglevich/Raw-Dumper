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

import android.util.Log;

import com.fkeglevich.rawdumper.util.event.EventDispatcher;
import com.fkeglevich.rawdumper.util.event.EventListener;

import junit.framework.Assert;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 21/09/17.
 */

class RuntimeParameter<T> extends Parameter<T>
{
    private String key;
    private ValueDecoder<T> decoder = null;
    private ValueEncoder<T> encoder = null;

    private static void throwUninitialized()
    {
        throw new RuntimeException("Parameter not initialized!");
    }

    RuntimeParameter(EventDispatcher<Parameter<T>> parameterImplementor)
    {
        parameterImplementor.addListener(eventData ->
        {
            Log.i("AYE", "AYE " + eventData.getKey());

            key = eventData.getKey();
            decoder = eventData.getDecoder();
            encoder = eventData.getEncoder();

            Assert.assertNotNull(key);
            Assert.assertNotNull(decoder);
            Assert.assertNotNull(encoder);
        });
    }

    @Override
    String getKey()
    {
        if (key == null) throwUninitialized();
        return key;
    }

    @Override
    ValueDecoder<T> getDecoder()
    {
        if (decoder == null) throwUninitialized();
        return decoder;
    }

    @Override
    ValueEncoder<T> getEncoder()
    {
        if (encoder == null) throwUninitialized();
        return encoder;
    }
}
