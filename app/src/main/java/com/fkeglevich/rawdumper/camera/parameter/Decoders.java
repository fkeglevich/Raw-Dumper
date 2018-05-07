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
import com.fkeglevich.rawdumper.camera.data.ManualFocusRange;
import com.fkeglevich.rawdumper.camera.data.ManualTemperature;
import com.fkeglevich.rawdumper.camera.data.ManualTemperatureRange;
import com.fkeglevich.rawdumper.camera.data.ParameterValue;
import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;

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
        dispatcher.put(String.class,            String::toString);
        dispatcher.put(Integer.TYPE,            Integer::parseInt);
        dispatcher.put(Float.TYPE,              Float::parseFloat);
        dispatcher.put(CaptureSize.class,       CaptureSize::parse);
        dispatcher.put(Flash.class,             createParameterValueDecoder(Flash.values(), Flash.OFF));
        dispatcher.put(FocusMode.class,         createParameterValueDecoder(FocusMode.values(), FocusMode.AUTO));
        dispatcher.put(WhiteBalancePreset.class,createParameterValueDecoder(WhiteBalancePreset.values(), WhiteBalancePreset.AUTO));
        dispatcher.put(ManualFocus.class,       ManualFocus::parse);
        dispatcher.put(ManualFocusRange.class,  ManualFocusRange::parse);
        dispatcher.put(ManualTemperature.class, ManualTemperature::parse);
        dispatcher.put(ManualTemperatureRange.class, ManualTemperatureRange::parse);
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

    private static <T extends ParameterValue> ValueDecoder<T> createParameterValueDecoder(final T[] enumValues, final T nullValue)
    {
        return value ->
        {
            if (value == null) return nullValue;

            for (T item : enumValues)
                if (item.getParameterValue().equals(value))
                    return item;

            return null;
        };
    }

    private static <T extends Enum<T> & ParameterValue> ValueDecoder<T> createEnumValueDecoder(T nullValue)
    {
        return value ->
        {
            if (value == null) return nullValue;

            for (T item : nullValue.getDeclaringClass().getEnumConstants())
                if (item.getParameterValue().equals(value))
                    return item;

            return null;
        };
    }
}
