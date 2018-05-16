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

package com.fkeglevich.rawdumper.util;

/**
 * TODO: add header comment
 * Created by Flávio Keglevich on 16/05/18.
 */
public class YuvUtil
{
    public static void switchUVPlanes(byte[] yuv, int width, int height)
    {
        final int length = yuv.length;
        final int size = width * height;
        byte temp;
        for (int i1 = 0; i1 < length; i1 += 2)
        {
            if (i1 >= size)
            {
                temp = yuv[i1];
                yuv[i1] = yuv[i1+1];
                yuv[i1+1] = temp;
            }
        }
    }
}
