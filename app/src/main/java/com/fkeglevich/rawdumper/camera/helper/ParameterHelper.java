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

package com.fkeglevich.rawdumper.camera.helper;

import android.hardware.Camera;

import com.fkeglevich.rawdumper.camera.CaptureSize;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Flávio Keglevich on 17/08/2017.
 * TODO: Add a class header comment!
 */

public class ParameterHelper
{
    private interface ValueParser<T>
    {
        T parse(String value);
    }

    private static final ValueParser<String> STRING_SPLIT_PARSER = new ValueParser<String>()
    {
        @Override
        public String parse(String value)
        {return value;
        }
    };
    private static final ValueParser<Integer> INT_SPLIT_PARSER = new ValueParser<Integer>()
    {
        @Override
        public Integer parse(String value)
        {return Integer.parseInt(value);}
    };

    public static List<String> splitValues(String value)
    {
        return performActualSplit(value, STRING_SPLIT_PARSER);
    }

    public static List<Integer> splitIntValues(String value)
    {
        return performActualSplit(value, INT_SPLIT_PARSER);
    }

    public static List<CaptureSize> convertSizeList(List<Camera.Size> list)
    {
        List<CaptureSize> result = new ArrayList<CaptureSize>();
        for (Camera.Size size : list)
            result.add(toCaptureSize(size));
        return result;
    }

    private static CaptureSize toCaptureSize(Camera.Size size)
    {
        return new CaptureSize(size.width, size.height);
    }

    private static <T> List<T> performActualSplit(String value, ValueParser<T> parser)
    {
        if (value == null) return null;
        List<T> values = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, ",");

        while (tokenizer.hasMoreElements())
            values.add(parser.parse(tokenizer.nextToken()));

        return values;
    }
}
