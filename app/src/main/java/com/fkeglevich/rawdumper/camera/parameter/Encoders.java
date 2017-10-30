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
import com.fkeglevich.rawdumper.camera.data.Flash;
import com.fkeglevich.rawdumper.camera.data.FocusMode;
import com.fkeglevich.rawdumper.camera.data.ManualFocus;
import com.fkeglevich.rawdumper.camera.data.ParameterValue;
import com.fkeglevich.rawdumper.camera.data.PictureFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 20/09/17.
 */

class Encoders
{
    private static final Map<Class, ValueEncoder> dispatcher = new HashMap<>();

    static
    {
        dispatcher.put(String.class, new ValueEncoder<String>() {
            @Override
            public String encode(String value) {
                return value;
            }
        });
        dispatcher.put(Integer.TYPE, new ValueEncoder<Integer>() {
            @Override
            public String encode(Integer value) {return value.toString();
            }
        });
        dispatcher.put(Float.TYPE, new ValueEncoder<Float>() {
            @Override
            public String encode(Float value) {return value.toString();
            }
        });
        dispatcher.put(CaptureSize.class, new ValueEncoder<CaptureSize>() {
            @Override
            public String encode(CaptureSize value) {return value.getWidth() + "x" + value.getHeight(); }
        });
        dispatcher.put(Flash.class, createParameterValueDecoder());
        dispatcher.put(FocusMode.class, createParameterValueDecoder());
        dispatcher.put(ManualFocus.class, new ValueEncoder<ManualFocus>()
        {
            @Override
            public String encode(ManualFocus value)
            {
                if (value == ManualFocus.DISABLED)
                    return "0";
                return String.valueOf(value.getNumericValue());
            }
        });
        dispatcher.put(PictureFormat.class, createParameterValueDecoder());
    }

    @SuppressWarnings("unchecked")
    static <T> ValueEncoder<T> selectEncoder(Class<T> valueClass)
    {
        if (!dispatcher.containsKey(valueClass))
            throw new RuntimeException("Encoder not found for class: " + valueClass.getSimpleName());

        return dispatcher.get(valueClass);
    }

    static <T> ValueEncoder<List<T>> selectListEncoder(Class<T> elementClass)
    {
        return new ListEncoder<>(selectEncoder(elementClass));
    }

    private static <T extends ParameterValue> ValueEncoder<T> createParameterValueDecoder()
    {
        return new ValueEncoder<T>()
        {
            @Override
            public String encode(T value)
            {
                return value.getParameterValue();
            }
        };
    }
}
