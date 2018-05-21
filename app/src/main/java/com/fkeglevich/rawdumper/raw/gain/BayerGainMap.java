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

package com.fkeglevich.rawdumper.raw.gain;

import android.support.annotation.Nullable;

import junit.framework.Assert;

/**
 * Created by flavio on 24/03/18.
 */

public class BayerGainMap
{
    public final int numColumns;
    public final int numRows;

    public GainMap red;
    public GainMap blue;
    public GainMap greenRed;
    public GainMap greenBlue;

    public BayerGainMap(int numColumns, int numRows)
    {
        this.numColumns = numColumns;
        this.numRows = numRows;
        this.red = new GainMap(numColumns, numRows);
        this.blue = new GainMap(numColumns, numRows);
        this.greenRed = new GainMap(numColumns, numRows);
        this.greenBlue = new GainMap(numColumns, numRows);
    }

    public void add(BayerGainMap b)
    {
        isValid();
        b.isValid();
        Assert.assertEquals(numColumns, b.numColumns);
        Assert.assertEquals(numRows,    b.numRows);

        red.add(b.red);
        blue.add(b.blue);
        greenRed.add(b.greenRed);
        greenBlue.add(b.greenBlue);
    }

    public void divideByScalar(float value)
    {
        isValid();

        red.divideByScalar(value);
        blue.divideByScalar(value);
        greenRed.divideByScalar(value);
        greenBlue.divideByScalar(value);
    }

    public void multiplyByScalar(float value)
    {
        isValid();

        red.multiplyByScalar(value);
        blue.multiplyByScalar(value);
        greenRed.multiplyByScalar(value);
        greenBlue.multiplyByScalar(value);
    }

    @Nullable
    public GainMap getMapFromToken(String token)
    {
        switch (token)
        {
            case "R": return red;
            case "B": return blue;
            case "Gr": return greenRed;
            case "Gb": return greenBlue;
            default: return null;
        }
    }

    @Override
    public String toString()
    {
        return "[red: " + red.toString() +
                "\nblue: " + blue.toString() +
                "\ngreenRed: " + greenRed.toString() +
                "\ngreenBlue: " + greenBlue.toString() + "]";
    }

    private void isValid()
    {
        Assert.assertNotNull(red);
        Assert.assertNotNull(blue);
        Assert.assertNotNull(greenRed);
        Assert.assertNotNull(greenBlue);
    }
}
