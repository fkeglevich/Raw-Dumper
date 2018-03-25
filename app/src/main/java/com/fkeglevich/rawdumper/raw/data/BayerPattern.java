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

package com.fkeglevich.rawdumper.raw.data;

import android.support.annotation.Keep;

/**
 * A simple enum for listing all supported sensor Bayer patterns.
 *
 * Created by Flávio Keglevich on 25/12/2016.
 */

@Keep
public enum BayerPattern
{
    /*
    The used Bayer Pattern DNG codes:
        RED = 0
        GREEN = 1
        BLUE = 2
     */
    BGGR(new byte[] {2, 1, 1, 0}, 3, 0, 2, 1),
    RGGB(new byte[] {0, 1, 1, 2}, 0, 3, 1, 2),
    GBRG(new byte[] {1, 2, 0, 1}, 2, 1, 3, 0),
    GRBG(new byte[] {1, 0, 2, 1}, 1, 2, 0, 3),
    UNKNOWN(null, -1, -1, -1, -1);

    private final byte[] bytePattern;
    private final int R;
    private final int B;
    private final int Gr;
    private final int Gb;

    BayerPattern(byte[] bytePattern, int R, int B, int Gr, int Gb)
    {
        this.bytePattern = bytePattern;
        this.R = R;
        this.B = B;
        this.Gr = Gr;
        this.Gb = Gb;
    }

    public byte[] getBytePattern()
    {
        if (bytePattern == null) throw new RuntimeException("An unknown bayer pattern can't be encoded!");
        return bytePattern.clone();
    }

    public int getR()
    {
        return R;
    }

    public int getB()
    {
        return B;
    }

    public int getGr()
    {
        return Gr;
    }

    public int getGb()
    {
        return Gb;
    }
}
