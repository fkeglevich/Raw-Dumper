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

import com.fkeglevich.rawdumper.camera.data.WhiteBalancePreset;
import com.fkeglevich.rawdumper.camera.extension.Parameters;
import com.fkeglevich.rawdumper.camera.parameter.ParameterCollection;
import com.fkeglevich.rawdumper.camera.parameter.value.ListValidator;

import java.util.List;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 06/05/18.
 */
public class WhiteBalancePresetFeature extends WritableFeature<WhiteBalancePreset, List<WhiteBalancePreset>>
{
    WhiteBalancePresetFeature(ParameterCollection parameterCollection)
    {
        super(Parameters.WHITE_BALANCE, parameterCollection, ListValidator.createFromListParameter(parameterCollection, Parameters.WHITE_BALANCE_VALUES));
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean isAvailable()
    {
        List<WhiteBalancePreset> values = getAvailableValues();
        if (values.size() == 1 && values.get(0).equals(WhiteBalancePreset.AUTO))
            return false;
        else
            return super.isAvailable();
    }
}
