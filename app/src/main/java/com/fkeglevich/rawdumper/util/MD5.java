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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * TODO: Add class header
 * <p>
 * Created by Flávio Keglevich on 07/11/17.
 */

public class MD5
{
    public static byte[] calculate(InputStream inputStream) throws IOException
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            //This should never happen
            throw new IllegalStateException(e.getMessage());
        }

        byte[] buffer = new byte[8192];

        int read;
        while ((read = inputStream.read(buffer)) > 0)
            digest.update(buffer, 0, read);

        return digest.digest();
    }

    public static String calculateAsBase16(InputStream inputStream) throws IOException
    {
        String md5 = new BigInteger(1, calculate(inputStream)).toString(16);
        StringBuilder stringBuilder = new StringBuilder(md5);
        int padding = 32 - md5.length();
        for (int i = 0; i < padding; i++)
            stringBuilder.append('0');

        return stringBuilder.toString();
    }
}
