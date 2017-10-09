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

import com.fkeglevich.rawdumper.camera.data.Ev;
import com.fkeglevich.rawdumper.camera.extension.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 04/10/17.
 */

public class ValueCollectionFactory
{
    public static <T> List<T> decodeValueList(Parameter<T> parameter, Iterable<String> values)
    {
        List<T> result = new ArrayList<>();
        ValueDecoder<T> decoder = parameter.getDecoder();
        for (String value : values)
            result.add(decoder.decode(value));

        return result;
    }

    public static List<Ev> createEvValueList(ParameterCollection parameterCollection, Parameter<Ev> parameter)
    {
        List<String> evValues = new ArrayList<>();
        int min = parameterCollection.get(Parameters.MIN_EXPOSURE_COMPENSATION);
        int max = parameterCollection.get(Parameters.MAX_EXPOSURE_COMPENSATION);

        if (!(min == 0 && max == 0))
            for (int i = min; i <= max; i++)
                evValues.add("" + i);

        return decodeValueList(parameter, evValues);
    }
}
