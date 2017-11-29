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
import android.content.res.AssetManager;

import com.fkeglevich.rawdumper.controller.context.ContextManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Flávio Keglevich on 16/08/2017.
 * TODO: Add a class header comment!
 */

public class AssetUtil
{
    public synchronized static byte[] getAssetBytes(String fileName) throws IOException
    {
        Context context = ContextManager.getApplicationContext();
        return readInputStream(context.getAssets().open(fileName, AssetManager.ACCESS_BUFFER));
    }

    public synchronized static String getAssetAsString(String fileName) throws IOException
    {
        return new String(getAssetBytes(fileName), Charset.defaultCharset());
    }

    private static byte[] readInputStream(InputStream input) throws IOException
    {
        byte[] result;
        try
        {
            result = new byte[input.available()];
            input.read(result);
        }
        finally
        {
            input.close();
        }
        return result;
    }
}
