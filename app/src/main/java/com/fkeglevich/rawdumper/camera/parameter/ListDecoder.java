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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 20/09/17.
 */

class ListDecoder<T> implements ValueDecoder<List<T>>
{
    private final ValueDecoder<T> elementDecoder;

    ListDecoder(ValueDecoder<T> elementDecoder)
    {
        this.elementDecoder = elementDecoder;
    }

    @Override
    public List<T> decode(String value)
    {
        if (value == null)
            return null;
        List<T> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, ",");

        while (tokenizer.hasMoreElements())
            result.add(elementDecoder.decode(tokenizer.nextToken()));

        return result;
    }
}
