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

package com.fkeglevich.rawdumper.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Flávio Keglevich on 16/01/2017.
 * TODO: Add a class header comment!
 */

public class ByteArrayUtil
{
    private static boolean isMatch(byte[] array, byte[] pattern, int index)
    {
        if (pattern.length > (array.length - index))
            return false;

        for (int i = 0; i < pattern.length; i++)
        {
            if (array[index + i] != pattern[i])
                return false;
        }
        return true;
    }

    public static int indexOf(byte[] array, byte[] pattern, int startIndex)
    {
        for (int i = startIndex; i < array.length; i++)
        {
            if (isMatch(array, pattern, i))
                return i;
        }
        return -1;
    }

    public static int indexOf(byte[] array, byte[] pattern)
    {
        return indexOf(array, pattern, 0);
    }

    public static int indexOf(byte[] array, byte value, int startIndex)
    {
        for (int i = startIndex; i < array.length; i++)
        {
            if(array[i] == value) return i;
        }
        return -1;
    }

    public static int indexOf(byte[] array, byte value)
    {
        return indexOf(array, value, 0);
    }

    public static byte[] getRawResource(Context context, int id) throws IOException
    {
        InputStream input = context.getResources().openRawResource(id);
        byte[] result = new byte[input.available()];
        input.read(result);
        input.close();
        return result;
    }
}
