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

import androidx.annotation.NonNull;

import com.fkeglevich.rawdumper.camera.data.DataRange;
import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 29/10/17.
 */

public class RangeValidator<T extends Comparable<T>> implements ValueValidator<T, DataRange<T>>
{
    private final DataRange<T> range;

    public static <T extends Comparable<T>> RangeValidator<T> create(DataRange<T> dataRange)
    {
        return new RangeValidator<>(dataRange);
    }

    @NonNull
    public static <T extends Comparable<T>> RangeValidator<T> create(ParameterCollection parameterCollection, Parameter<? extends DataRange<T>> parameter)
    {
        return new RangeValidator<T>(parameterCollection.get(parameter));
    }

    private RangeValidator(DataRange<T> range)
    {
        this.range = range;
    }

    @Override
    public boolean isAvailable()
    {
        return range != null;
    }

    @Override
    public boolean isValid(T value)
    {
        return range.contains(value);
    }

    @Override
    public DataRange<T> getAvailableValues()
    {
        return range;
    }
}
