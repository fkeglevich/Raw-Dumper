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
import java.nio.charset.Charset;

/**
 * Created by Flávio Keglevich on 03/08/2017.
 * TODO: Add a class header comment!
 */

public class ResourceUtil
{
    public static byte[] getRawResource(Context context, int id) throws IOException
    {
        InputStream input = context.getResources().openRawResource(id);
        return readInputStream(input);
    }

    public static byte[] getRawResource(Context context, String filename) throws IOException
    {
        InputStream input = context.getResources().openRawResource(
                context.getResources().getIdentifier(filename, "raw", context.getPackageName()));
        return readInputStream(input);
    }

    public static String getStringRawResource(Context context, int id) throws IOException
    {
        return new String(getRawResource(context, id), Charset.defaultCharset());
    }

    public static String getStringRawResource(Context context, String filename) throws IOException
    {
        return new String(getRawResource(context, filename), Charset.defaultCharset());
    }

    public static byte[] readInputStream(InputStream input) throws IOException
    {
        byte[] result = new byte[input.available()];
        input.read(result);
        input.close();
        return result;
    }
}
