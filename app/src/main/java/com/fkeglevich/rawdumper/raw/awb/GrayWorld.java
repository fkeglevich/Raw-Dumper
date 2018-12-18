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

package com.fkeglevich.rawdumper.raw.awb;

import com.fkeglevich.rawdumper.raw.data.RawImageSize;
import com.fkeglevich.rawdumper.raw.data.image.RawImage;

public class GrayWorld
{
    static
    {
        System.loadLibrary("raw-dumper");
    }

    private static native void nativeCalculate(int width, int height, int bpl, byte[] data, double[] output);

    public static void calculate(RawImage imageData, double[] output)
    {
        RawImageSize imageSize = imageData.getSize();
        nativeCalculate(imageSize.getPaddedWidth(), imageSize.getPaddedHeight(),
                imageSize.getBytesPerLine(), imageData.getData(), output);
    }
}
