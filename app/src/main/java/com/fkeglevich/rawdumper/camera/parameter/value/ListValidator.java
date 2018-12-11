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

package com.fkeglevich.rawdumper.camera.parameter.value;

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 25/09/17.
 */

public class ListValidator<V> implements ValueValidator<V, List<V>>
{
    private final List<V> valueList;

    @NonNull
    public static <V> ListValidator<V> createFromListParameter(ParameterCollection parameterCollection, Parameter<List<V>> listParameter)
    {
        return new ListValidator<>(parameterCollection.get(listParameter));
    }

    @NonNull
    public static <V> ListValidator<V> createInvalid()
    {
        return new ListValidator<>(Collections.emptyList());
    }

    public ListValidator(List<V> valueList)
    {
        this.valueList = valueList != null ? Collections.unmodifiableList(valueList) : Collections.emptyList();
    }

    @Override
    public boolean isValid(V value)
    {
        return valueList.contains(value);
    }

    @Override
    public List<V> getAvailableValues()
    {
        return valueList;
    }

    @Override
    public boolean isAvailable()
    {
        return !valueList.isEmpty();
    }
}
