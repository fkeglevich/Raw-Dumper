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

import java.util.List;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 20/09/17.
 */

class ListEncoder<T> implements ValueEncoder<List<T>>
{
    private final ValueEncoder<T> elementEncoder;

    ListEncoder(ValueEncoder<T> elementEncoder)
    {
        this.elementEncoder = elementEncoder;
    }

    @Override
    public String encode(List<T> value)
    {
        StringBuilder valueBuilder = new StringBuilder();
        boolean firstTime = true;
        for (T element : value)
        {
            if (firstTime)
                firstTime = false;
            else
                valueBuilder.append(",");

            valueBuilder.append(elementEncoder.encode(element));
        }
        return valueBuilder.toString();
    }
}
