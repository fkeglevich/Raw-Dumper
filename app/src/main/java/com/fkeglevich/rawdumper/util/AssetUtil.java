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

import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Helper class for dealing with assets
 *
 * Created by Flávio Keglevich on 16/08/2017.
 */

public class AssetUtil
{
    public synchronized static byte[] getAssetBytes(String fileName) throws IOException
    {
        Context context = ContextManager.getApplicationContext();
        try (InputStream stream = context.getAssets().open(fileName, AssetManager.ACCESS_BUFFER))
        {
            byte[] result = new byte[stream.available()];
            int bytesRead = stream.read(result);
            Assert.assertEquals(result.length, bytesRead);
            return result;
        }
    }

    public synchronized static String getAssetAsString(String fileName) throws IOException
    {
        return new String(getAssetBytes(fileName), Charset.defaultCharset());
    }
}
