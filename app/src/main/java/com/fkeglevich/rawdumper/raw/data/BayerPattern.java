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

import androidx.annotation.Keep;

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
    BGGR(new byte[] {2, 1, 1, 0}, 3, 0, 2, 1, 2),
    RGGB(new byte[] {0, 1, 1, 2}, 0, 3, 1, 2, 1),
    GBRG(new byte[] {1, 2, 0, 1}, 2, 1, 3, 0, 3),
    GRBG(new byte[] {1, 0, 2, 1}, 1, 2, 0, 3, 0),
    UNKNOWN(null, -1, -1, -1, -1, -1);

    private final byte[] bytePattern;
    private final int R;
    private final int B;
    private final int Gr;
    private final int Gb;
    private final int phase;

    BayerPattern(byte[] bytePattern, int R, int B, int Gr, int Gb, int phase)
    {
        this.bytePattern = bytePattern;
        this.R = R;
        this.B = B;
        this.Gr = Gr;
        this.Gb = Gb;
        this.phase = phase;
    }

    public byte[] getBytePattern()
    {
        checkIfValid();
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

    public int getPhase()
    {
        checkIfValid();
        return phase;
    }

    public int[] getRedPosition()
    {
        return new int[]{ getR() % 2, getR() / 2 };
    }

    public BayerPattern invertVertically()
    {
        switch (this)
        {
            case BGGR: return GRBG;
            case RGGB: return GBRG;
            case GBRG: return RGGB;
            case GRBG: return BGGR;
            default:   return UNKNOWN;
        }
    }

    private void checkIfValid()
    {
        if (bytePattern == null) throw new RuntimeException("An unknown bayer pattern can't be encoded!");
    }
}
