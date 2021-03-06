/*
 * Copyright 2019, Flávio Keglevich
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

package com.fkeglevich.rawdumper.camera.feature;

import android.content.SharedPreferences;

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.util.Nullable;

public abstract class MeteringFeature<T> extends Feature<Nullable<T>>
{
    MeteringFeature(Parameter<Nullable<T>> parameter, ParameterCollection parameterCollection)
    {
        super(parameter, parameterCollection);
    }

    @Override
    void storeValue(SharedPreferences.Editor editor)
    {
        /*no op*/
    }

    @Override
    void loadValue(SharedPreferences preferences)
    {
        /*no op*/
    }
}
