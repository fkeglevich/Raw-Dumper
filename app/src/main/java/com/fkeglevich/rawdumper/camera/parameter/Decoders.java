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

import com.fkeglevich.rawdumper.camera.data.CaptureSize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 19/09/17.
 */

class Decoders
{
    private static final Map<Class, ValueDecoder> dispatcher = new HashMap<>();

    static
    {
        dispatcher.put(String.class, new ValueDecoder<String>() {
            @Override
            public String decode(String value) {
                return value;
            }
        });
        dispatcher.put(Integer.TYPE, new ValueDecoder<Integer>() {
            @Override
            public Integer decode(String value) {return Integer.parseInt(value); }
        });
        dispatcher.put(Float.TYPE, new ValueDecoder<Float>() {
            @Override
            public Float decode(String value) {return Float.parseFloat(value); }
        });
        dispatcher.put(CaptureSize.class, new ValueDecoder<CaptureSize>()
        {
            @Override
            public CaptureSize decode(String value)
            {
                int xPos = value.indexOf('x');
                if (xPos != -1)
                {
                    int width  = Integer.parseInt(value.substring(0, xPos));
                    int height = Integer.parseInt(value.substring(xPos + 1));
                    return new CaptureSize(width, height);
                }
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    static <T> ValueDecoder<T> selectDecoder(Class<T> valueClass)
    {
        if (!dispatcher.containsKey(valueClass))
            throw new RuntimeException("Decoder not found for class: " + valueClass.getSimpleName());

        return dispatcher.get(valueClass);
    }

    static <T> ValueDecoder<List<T>> selectListDecoder(Class<T> elementClass)
    {
        return new ListDecoder<>(selectDecoder(elementClass));
    }
}
