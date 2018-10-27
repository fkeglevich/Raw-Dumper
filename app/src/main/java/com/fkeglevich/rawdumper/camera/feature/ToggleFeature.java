/*
 * Copyright 2018, Flávio Keglevich
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

import com.fkeglevich.rawdumper.camera.parameter.Parameter;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ToggleValidator;
import com.fkeglevich.rawdumper.camera.parameter.value.ValueValidator;

public class ToggleFeature extends WritableFeature<Boolean, ToggleValidator>
{
    ToggleFeature(Parameter<Boolean> featureParameter, ParameterCollection parameterCollection, ValueValidator<Boolean, ToggleValidator> validator)
    {
        super(featureParameter, parameterCollection, validator);
    }

    ToggleFeature(Parameter<Boolean> featureParameter, ParameterCollection parameterCollection, ValueValidator<Boolean, ToggleValidator> validator, boolean isMutable)
    {
        super(featureParameter, parameterCollection, validator, isMutable);
    }
}
